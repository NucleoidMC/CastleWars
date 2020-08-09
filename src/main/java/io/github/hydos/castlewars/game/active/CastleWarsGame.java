package io.github.hydos.castlewars.game.active;

import io.github.hydos.castlewars.game.PlayerManager;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.gegy1000.plasmid.game.GameWorld;
import net.gegy1000.plasmid.game.event.*;
import net.gegy1000.plasmid.game.player.GameTeam;
import net.gegy1000.plasmid.game.player.JoinResult;
import net.gegy1000.plasmid.game.rule.GameRule;
import net.gegy1000.plasmid.game.rule.RuleResult;
import net.gegy1000.plasmid.util.PlayerRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.stream.Stream;

public class CastleWarsGame {

    public final Map<PlayerRef, CastleWarsPlayer> participants = new HashMap<>();
    public final Map<GameTeam, TeamState> teams = new HashMap<>();

    public final ServerWorld world;
    public final CastleWarsMap map;
    public final CastleWarsConfig config;
    public final CastleWarsScoreboard scoreboard;
    public GameWorld gameWorld;

    public CastleWarsGame(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        this.gameWorld = gameWorld;
        this.world = gameWorld.getWorld();
        this.map = map;
        this.config = config;
        this.scoreboard = CastleWarsScoreboard.create(this);

    }

    public static void open(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        CastleWarsGame active = new CastleWarsGame(gameWorld, map, config);

        gameWorld.newGame(game -> {
            game.setRule(GameRule.ALLOW_PORTALS, RuleResult.DENY);
            game.setRule(GameRule.ALLOW_PVP, RuleResult.ALLOW);
            game.setRule(GameRule.INSTANT_LIGHT_TNT, RuleResult.ALLOW);
            game.setRule(GameRule.ALLOW_CRAFTING, RuleResult.DENY);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            game.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);

            game.on(GameOpenListener.EVENT, active::onOpen);
            game.on(GameOpenListener.EVENT, active::onClose);

            game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            game.on(PlayerAddListener.EVENT, active::addPlayer);

            game.on(GameTickListener.EVENT, active::tick);

            game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);

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
        return ActionResult.PASS;
    }

    private boolean onBreakBlock(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
        return true;
    }

    private boolean onPlayerDeath(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
        serverPlayerEntity.setHealth(20);
        return false;
    }

    private void tick() {
        PlayerManager.getInstance().tick();
        scoreboard.tick();
    }

    private void addPlayer(ServerPlayerEntity player) {
        PlayerManager.getInstance().resetPlayer(player, GameMode.SPECTATOR);
        PlayerManager.getInstance().spawnPlayerInLobby(player);
    }

    private void onClose() {
    }

    private void onOpen() {
    }

    public Stream<CastleWarsPlayer> getTeamPlayers(GameTeam team) {
        return this.participants.values().stream().filter(participant -> participant.team == team);
    }

    public Stream<CastleWarsPlayer> participants() {
        return this.participants.values().stream();
    }

    public Stream<ServerPlayerEntity> players() {
        return this.participants().map(CastleWarsPlayer::player).filter(Objects::nonNull);
    }

    public Stream<TeamState> teams() {
        return this.teams.values().stream();
    }

    public int getTeamCount() {
        return this.teams.size();
    }

    public TeamState getTeam(GameTeam team) {
        return this.teams.get(team);
    }

    public static class TeamState {
        final Set<PlayerRef> players = new HashSet<>();
        final GameTeam team;
        boolean eliminated;

        TeamState(GameTeam team) {
            this.team = team;
        }
    }
}
