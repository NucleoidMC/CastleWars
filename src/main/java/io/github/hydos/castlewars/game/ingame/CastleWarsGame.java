package io.github.hydos.castlewars.game.ingame;

import io.github.hydos.castlewars.CastleWars;
import io.github.hydos.castlewars.game.PlayerManager;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.custom.CustomGameObjects;
import io.github.hydos.castlewars.game.entities.ProtectThisEntity;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import xyz.nucleoid.plasmid.block.CustomBlock;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.GameTeam;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CastleWarsGame {

    public final ServerWorld world;
    public final CastleWarsMap map;
    public final CastleWarsConfig config;
    public final CastleWarsScoreboard scoreboard;
    public GameWorld gameWorld;
    private boolean opened;
    private int ticks = 0;
    private boolean killPhase = CastleWars.DEBUGGING;
    private static final Random random = new Random();

    public CastleWarsGame(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        this.gameWorld = gameWorld;
        this.world = gameWorld.getWorld();
        this.map = map;
        this.config = config;
        this.scoreboard = CastleWarsScoreboard.create(this);

    }

    public static void open(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        CastleWarsGame active = new CastleWarsGame(gameWorld, map, config);
        PlayerManager.getInstance().makePlayersActive(config);

        gameWorld.newGame(game -> {
            game.setRule(GameRule.ALLOW_PORTALS, RuleResult.DENY);
            game.setRule(GameRule.ALLOW_PVP, RuleResult.ALLOW);
            game.setRule(GameRule.INSTANT_LIGHT_TNT, RuleResult.DENY);
            game.setRule(GameRule.ALLOW_CRAFTING, RuleResult.ALLOW);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            game.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);

            game.on(GameOpenListener.EVENT, active::onOpen);
            game.on(GameCloseListener.EVENT, active::onClose);

            game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            game.on(PlayerAddListener.EVENT, active::addPlayerDuringGame);

            game.on(GameTickListener.EVENT, active::tick);

            game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
            game.on(BreakBlockListener.EVENT, active::onBreakBlock);
            game.on(AttackEntityListener.EVENT, active::onAttackEntity);
            game.on(UseBlockListener.EVENT, active::onUseBlock);
            game.on(UseItemListener.EVENT, active::onUseItem);
        });
    }

    private boolean onPlayerDeath(ServerPlayerEntity playerEntity, DamageSource damageSource) {
        GameTeam playerTeam = PlayerManager.getInstance().getPlayersTeam(playerEntity);
        PlayerManager.getInstance().teams.get(playerTeam).players.remove(playerEntity);
        if (PlayerManager.getInstance().teams.get(playerTeam).players.size() == 0) {
            PlayerManager.getInstance().teams.get(playerTeam).eliminated = true;
            ProtectThisEntity.checkForGameEnd(this.gameWorld);
        }

        return false;
    }

    private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        if (item.getItem() == Items.WATER_BUCKET || item.getItem() == Items.NETHERITE_BLOCK || item.getItem() == Items.BEDROCK || item.getItem() == Items.OBSIDIAN) {
            return TypedActionResult.fail(ItemStack.EMPTY);
        }
        return TypedActionResult.pass(ItemStack.EMPTY);
    }

    private ActionResult onUseBlock(ServerPlayerEntity serverPlayerEntity, Hand hand, BlockHitResult blockHitResult) {
        ItemStack item = serverPlayerEntity.getStackInHand(hand);
        if (item.getItem() == Items.WATER_BUCKET || item.getItem() == Items.NETHERITE_BLOCK || item.getItem() == Items.BEDROCK || item.getItem() == Items.OBSIDIAN) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private ActionResult onAttackEntity(ServerPlayerEntity serverPlayerEntity, Hand hand, Entity entity, EntityHitResult entityHitResult) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity attackedEntity = (ServerPlayerEntity) entity;
            if (PlayerManager.getInstance().getPlayersTeam(attackedEntity) == PlayerManager.getInstance().getPlayersTeam(serverPlayerEntity)) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    private boolean onBreakBlock(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
        return false;
    }

    private void tick() {
        ticks++;
        if (ticks == 20 * 300) { // 5 minutes
            killPhase = true;
            for (ServerPlayerEntity player : PlayerManager.getInstance().participants.keySet()) {
                player.networkHandler.sendPacket(new TitleS2CPacket(20, 60, 20));
                player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText("Kill Their Thing idk").formatted(Formatting.RED, Formatting.BOLD)));
                player.setGameMode(GameMode.SURVIVAL);
                player.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 100, 1);
            }
        }
        if (killPhase) {
            //Handle custom block logic
            if (ticks % (20 * 5) == 0) {//20*5 = 5 seconds in ticks
                for (BlockPos pos : CustomBlock.allOfType(CustomGameObjects.SUPER_ROCKET_LAUNCH_PAD.getBlock())) {
                    //fling the block above the launch pad
                    BlockState block = world.getBlockState(pos.add(0, 1, 0));

                    if (block.getBlock() == Blocks.TNT) {
                        boolean blueteam = pos.getX() < 31; //blue should be on that side. this hardcoding kills me

                        TntEntity blockEntity = new TntEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), null);
                        blockEntity.setVelocity(blueteam ? 1.4f : -1.4f, random.nextFloat(), 0);

                        world.spawnEntity(blockEntity);
                    }
                }
            }
        }
        scoreboard.tick();

    }

    private void addPlayerDuringGame(ServerPlayerEntity rawPlayer) {
        CastleWarsPlayer player = PlayerManager.getInstance().participants.get(rawPlayer);
        System.out.println(opened);
        if (PlayerManager.getInstance().isParticipant(rawPlayer)) {
            if (player.team.getDisplay().equals("Blue")) {
                map.spawnPlayerTeamBlue(rawPlayer, world);
            }
            if (player.team.getDisplay().equals("Red")) {
                map.spawnPlayerTeamRed(rawPlayer, world);
            }
            player.player().inventory.clear();
            player.gamemode(GameMode.CREATIVE);
            player.player().networkHandler.sendPacket(new TitleS2CPacket(20, 60, 20));
            player.player().networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText("Protect Your Thing idk").formatted(Formatting.GREEN, Formatting.BOLD)));
        } else {
            PlayerManager.getInstance().resetPlayer(rawPlayer, GameMode.SPECTATOR);
        }
    }

    private void onClose() {
        scoreboard.close();
        ServerWorld overworld = world.getServer().getOverworld();
        overworld.getChunkManager().addTicket(ChunkTicketType.field_19347, new ChunkPos(overworld.getSpawnPos()), 4, 1);
        opened = false;
    }

    private void onOpen() {
        opened = true;
        map.spawnVillagers(this);
    }

    public static class TeamState {
        public WorldBorder border;
        public final Set<ServerPlayerEntity> players = new HashSet<>();
        public final GameTeam team;
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
