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
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CastleWarsGame {

    public final Map<ServerPlayerEntity, CastleWarsPlayer> participants = new HashMap<>();
    public final Map<GameTeam, TeamState> teams = new HashMap<>();

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
        active.initPlayers(PlayerManager.getInstance().gamePlayers);

        gameWorld.newGame(game -> {
            game.setRule(GameRule.ALLOW_PORTALS, RuleResult.DENY);
            game.setRule(GameRule.ALLOW_PVP, RuleResult.ALLOW);
            game.setRule(GameRule.INSTANT_LIGHT_TNT, RuleResult.ALLOW);
            game.setRule(GameRule.ALLOW_CRAFTING, RuleResult.DENY);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            game.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);

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
        });
    }

    private void initPlayers(List<ServerPlayerEntity> gamePlayers) {
        boolean teamOne = true;
        for (ServerPlayerEntity player : gamePlayers) {
            GameTeam playersTeam = this.config.teams.get(teamOne ? 0 : 1);
            this.participants.put(player, new CastleWarsPlayer(playersTeam, player));
            teamOne = !teamOne;
        }
        for (GameTeam team : this.config.teams) {
            List<CastleWarsPlayer> participants = this.getTeamPlayers(team).collect(Collectors.toList());
            TeamState teamState = new TeamState(team);
            participants.forEach(participant -> teamState.players.add(participant.player()));
            this.teams.put(team, teamState);
        }
        teamOne = true;
        for (ServerPlayerEntity player : gamePlayers) {
            GameTeam playersTeam = this.config.teams.get(teamOne ? 0 : 1);
            player.networkHandler.sendPacket(new WorldBorderS2CPacket(teams.get(playersTeam).border, WorldBorderS2CPacket.Type.INITIALIZE));
            teamOne = !teamOne;
        }
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
        return true;
    }

    private void tick() {
        PlayerManager.getInstance().tick();
        scoreboard.tick();
    }

    private void addPlayer(ServerPlayerEntity rawPlayer) {
        CastleWarsPlayer player = participants.get(rawPlayer);
        if (this.opened && this.isParticipant(rawPlayer)) {
            if(player.team.getDisplay().equals("Blue")){
                map.spawnPlayerTeamBlue(rawPlayer, world);
            }
            if(player.team.getDisplay().equals("Red")){
                map.spawnPlayerTeamRed(rawPlayer, world);
            }
        }else{
            PlayerManager.getInstance().resetPlayer(rawPlayer, GameMode.SPECTATOR);
        }
    }

    private boolean isParticipant(ServerPlayerEntity player) {
        return participants.containsKey(player);
    }

    private void onClose() {
        scoreboard.close();
        opened = false;
    }

    private void onOpen() {
        opened = true;
    }

    public Stream<CastleWarsPlayer> getTeamPlayers(GameTeam team) {
        return this.participants.values().stream().filter(participant -> participant.team == team);
    }

    public Stream<CastleWarsPlayer> participants() {
        return this.participants.values().stream();
    }

    public Stream<TeamState> teams() {
        return this.teams.values().stream();
    }

    public static class TeamState {
        public WorldBorder border;
        final Set<ServerPlayerEntity> players = new HashSet<>();
        final GameTeam team;
        boolean eliminated;

        TeamState(GameTeam team) {
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
