package io.github.hydos.castlewars.game.ingame;

import io.github.hydos.castlewars.game.PlayerManager;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.gegy1000.plasmid.game.GameWorld;
import net.gegy1000.plasmid.game.event.*;
import net.gegy1000.plasmid.game.player.GameTeam;
import net.gegy1000.plasmid.game.player.JoinResult;
import net.gegy1000.plasmid.game.rule.GameRule;
import net.gegy1000.plasmid.game.rule.RuleResult;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;

import java.util.HashSet;
import java.util.Set;

public class CastleWarsGame {

    public final ServerWorld world;
    public final CastleWarsMap map;
    public final CastleWarsConfig config;
    public final CastleWarsScoreboard scoreboard;
    public GameWorld gameWorld;
    private boolean opened;

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
            game.setRule(GameRule.INSTANT_LIGHT_TNT, RuleResult.ALLOW);
            game.setRule(GameRule.ALLOW_CRAFTING, RuleResult.ALLOW);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            game.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);

            game.on(GameOpenListener.EVENT, active::onOpen);
            game.on(GameCloseListener.EVENT, active::onClose);

            game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            game.on(PlayerAddListener.EVENT, active::addPlayerDuringGame);

            game.on(GameTickListener.EVENT, active::tick);

            game.on(BreakBlockListener.EVENT, active::onBreakBlock);
            game.on(AttackEntityListener.EVENT, active::onAttackEntity);
            game.on(UseBlockListener.EVENT, active::onUseBlock);
            game.on(UseItemListener.EVENT, active::onUseItem);
        });
    }

    private TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity serverPlayerEntity, Hand hand) {
        return TypedActionResult.pass(ItemStack.EMPTY);
    }

    private ActionResult onUseBlock(ServerPlayerEntity serverPlayerEntity, Hand hand, BlockHitResult blockHitResult) {
        return ActionResult.PASS;
    }

    private ActionResult onAttackEntity(ServerPlayerEntity serverPlayerEntity, Hand hand, Entity entity, EntityHitResult entityHitResult) {
        return ActionResult.SUCCESS;
    }

    private boolean onBreakBlock(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
        return false;
    }

    private void tick() {
        scoreboard.tick();
    }

    private void addPlayerDuringGame(ServerPlayerEntity rawPlayer) {
        CastleWarsPlayer player = PlayerManager.getInstance().participants.get(rawPlayer);
        if (this.opened && PlayerManager.getInstance().isParticipant(rawPlayer)) {
            if(player.team.getDisplay().equals("Blue")){
                map.spawnPlayerTeamBlue(rawPlayer, world);
            }
            if(player.team.getDisplay().equals("Red")){
                map.spawnPlayerTeamRed(rawPlayer, world);
            }
            player.player().inventory.clear();
            player.gamemode(GameMode.CREATIVE);
        }else{
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
    }

    public static class TeamState {
        public WorldBorder border;
        public final Set<ServerPlayerEntity> players = new HashSet<>();
        public final GameTeam team;
        boolean eliminated;

        public TeamState(GameTeam team) {
            this.team = team;
            this.border = new WorldBorder();
            if (team.getDisplay().equals("Blue")) {
                border.setCenter(5, 5);
                border.setSize(10);
            } else {
                border.setCenter(45, 5);
                border.setSize(10);
            }
        }
    }
}
