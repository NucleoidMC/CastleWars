package io.github.hydos.castlewars.game.ingame;

import io.github.hydos.castlewars.CastleWars;
import io.github.hydos.castlewars.game.PlayerManager;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.custom.entity.ProtectThisEntity;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.GameTeam;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("SameReturnValue")
public class CastleWarsGame {

    public final ServerWorld world;
    public final CastleWarsMap map;
    public final CastleWarsConfig config;
    public final CastleWarsScoreboard scoreboard;
    public final GameWorld gameWorld;
    public boolean gameRunning;
    public boolean killPhase = CastleWars.DEBUGGING;
    public int ticks = 0;

    public CastleWarsGame(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        this.gameRunning = false;
        this.gameWorld = gameWorld;
        this.world = gameWorld.getWorld();
        this.map = map;
        this.config = config;
        this.scoreboard = CastleWarsScoreboard.create(this);
    }

    public static void open(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        CastleWarsGame active = new CastleWarsGame(gameWorld, map, config);
        PlayerManager.getInstance().makePlayersActive(config);

        gameWorld.openGame(game -> {
            game.setRule(GameRule.PORTALS, RuleResult.DENY);
            game.setRule(GameRule.PVP, RuleResult.ALLOW);
            game.setRule(GameRule.UNSTABLE_TNT, RuleResult.DENY);
            game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            game.setRule(GameRule.HUNGER, RuleResult.DENY);

            game.on(GameOpenListener.EVENT, active::onOpen);
            game.on(GameCloseListener.EVENT, active::onClose);

            game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            game.on(PlayerAddListener.EVENT, active::addPlayer);

            game.on(GameTickListener.EVENT, active::tick);

            game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
            game.on(BreakBlockListener.EVENT, active::onBreakBlock);
            game.on(AttackEntityListener.EVENT, active::onAttackEntity);
            game.on(UseBlockListener.EVENT, active::onUseBlock);
            game.on(UseItemListener.EVENT, active::onUseItem);
            game.on(EntityDeathListener.EVENT, active::onEntityDeath);
        });


    }

    private ActionResult onEntityDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if(killPhase){
           return ActionResult.PASS;
        }
        else{
            return ActionResult.FAIL;
        }
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity playerEntity, DamageSource damageSource) {
        GameTeam playerTeam = PlayerManager.getInstance().getPlayersTeam(playerEntity);
        PlayerManager.getInstance().teams.get(playerTeam).players.remove(playerEntity);
        if (PlayerManager.getInstance().teams.get(playerTeam).players.size() == 0) {
            PlayerManager.getInstance().teams.get(playerTeam).eliminated = true;
            ProtectThisEntity.checkForGameEnd(this.gameWorld);
        }
        return ActionResult.PASS;
    }

    private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        if (item.getItem() == Items.WATER_BUCKET) {
            return TypedActionResult.fail(ItemStack.EMPTY);
        }
        return TypedActionResult.pass(ItemStack.EMPTY);
    }

    private ActionResult onUseBlock(ServerPlayerEntity serverPlayerEntity, Hand hand, BlockHitResult blockHitResult) {
        ItemStack item = serverPlayerEntity.getStackInHand(hand);
        if (item.getItem() == Items.WATER_BUCKET || item.getItem() == Items.NETHERITE_BLOCK || item.getItem() == Items.BEDROCK || item.getItem() == Items.CRYING_OBSIDIAN || item.getItem() == Items.OBSIDIAN || item.getItem() == Items.ANCIENT_DEBRIS || item.getItem() == Items.RED_TERRACOTTA || item.getItem() == Items.BLUE_TERRACOTTA) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private ActionResult onAttackEntity(ServerPlayerEntity serverPlayerEntity, Hand hand, Entity entity, EntityHitResult entityHitResult) {
        if(!killPhase){
            return ActionResult.FAIL;
        }
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity attackedEntity = (ServerPlayerEntity) entity;
            if (PlayerManager.getInstance().getPlayersTeam(attackedEntity) == PlayerManager.getInstance().getPlayersTeam(serverPlayerEntity)) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    private boolean onBreakBlock(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
        BlockState state = world.getBlockState(blockPos);
        Block type = state.getBlock();
        return type == Blocks.GLASS || type == Blocks.BEDROCK || type == Blocks.BLUE_TERRACOTTA || type == Blocks.RED_TERRACOTTA || type == Blocks.ANCIENT_DEBRIS;
    }

    private void tick() {
        ticks++;
        if (ticks == 20 * 300) { // 5 minutes
            killPhase = true;

            if (config.bridges) {
                PlayerManager.getInstance().removeWorldBorder(config);
            }

            for (ServerPlayerEntity player : PlayerManager.getInstance().participants.keySet()) {
                player.networkHandler.sendPacket(new TitleS2CPacket(20, 40, 20));
                player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText("Kill The Other").formatted(Formatting.YELLOW, Formatting.BOLD)));
                player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, new LiteralText("Teams Thing").formatted(Formatting.YELLOW, Formatting.BOLD)));
                player.setGameMode(GameMode.SURVIVAL);
                player.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 1, 1);
            }
        }
        scoreboard.tick();

    }

    private void addPlayer(ServerPlayerEntity rawPlayer) {
        if(map.redTeam == null){ //assume if red is null blue is null
            map.blueTeam = new ServerBossBar(new LiteralText("Blue").formatted(Formatting.BLUE, Formatting.BOLD), BossBar.Color.BLUE, BossBar.Style.NOTCHED_20);
            map.redTeam = new ServerBossBar(new LiteralText("Red").formatted(Formatting.RED, Formatting.BOLD), BossBar.Color.RED, BossBar.Style.NOTCHED_20);
        }
        map.blueTeam.addPlayer(rawPlayer);
        map.redTeam.addPlayer(rawPlayer);

        CastleWarsPlayer player = PlayerManager.getInstance().participants.get(rawPlayer);
        if (PlayerManager.getInstance().isParticipant(rawPlayer) && !gameRunning) {
            if (player.team.getDisplay().equals("Blue")) {
                map.spawnPlayerTeamBlue(rawPlayer, world);
            }
            if (player.team.getDisplay().equals("Red")) {
                map.spawnPlayerTeamRed(rawPlayer, world);
            }
            player.player().inventory.clear();
            player.gamemode(GameMode.CREATIVE);
            player.player().networkHandler.sendPacket(new TitleS2CPacket(20, 60, 20));
            player.player().networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText("Protect").formatted(Formatting.GREEN, Formatting.BOLD)));
            player.player().networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, new LiteralText("Your Villager").formatted(Formatting.GREEN, Formatting.BOLD)));
        } else {
            PlayerManager.getInstance().resetPlayer(rawPlayer, GameMode.SPECTATOR);
        }
    }

    private void onClose() {
        gameRunning = true;
        map.blueTeam.clearPlayers();
        map.redTeam.clearPlayers();
        scoreboard.close();
        map.close(this);
    }

    private void onOpen() {
        map.spawnVillagers(this);
    }

    public static class TeamState {
        public final Set<ServerPlayerEntity> players = new HashSet<>();
        public final GameTeam team;
        public final WorldBorder border;
        public boolean eliminated;

        public TeamState(GameTeam team, CastleWarsConfig config) {
            this.team = team;
            this.border = new WorldBorder();
            int borderWidth = config.map.platformSize;
            if (team.getDisplay().equals("Blue")) {
                border.setCenter(borderWidth / 2d, borderWidth / 2d);
            } else {
                border.setCenter(borderWidth / 2d + config.map.platformOffset, borderWidth / 2d);
            }
            border.setSize(borderWidth);
        }
    }
}
