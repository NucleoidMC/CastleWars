package me.hydos.castlewars.game.waiting;

import me.hydos.castlewars.game.core.PlayerManager;
import me.hydos.castlewars.game.core.config.CastleWarsConfig;
import me.hydos.castlewars.game.ingame.CastleWarsGame;
import me.hydos.castlewars.game.map.CastleWarsMap;
import me.hydos.castlewars.game.map.MapGenerator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

public class CastleWarsWaiting {

    private final GameSpace gameWorld;
    private final CastleWarsMap map;
    private final CastleWarsConfig config;

    private final PlayerManager playerManager;

    private CastleWarsWaiting(GameSpace gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        this.gameWorld = gameWorld;
        this.map = map;
        this.config = config;

        this.playerManager = new PlayerManager(gameWorld, map);
    }

    public static GameOpenProcedure open(GameOpenContext<CastleWarsConfig> context) {
        MapGenerator generator = new MapGenerator(context.getConfig());
        CastleWarsMap map = generator.build();

        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(map.asGenerator(context.getServer()))
                .setDefaultGameMode(GameMode.ADVENTURE);

        return context.createOpenProcedure(worldConfig, game -> {
            CastleWarsWaiting waiting = new CastleWarsWaiting(game.getSpace(), map, context.getConfig());

            game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
            game.setRule(GameRule.PORTALS, RuleResult.DENY);
            game.setRule(GameRule.PVP, RuleResult.DENY);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            game.setRule(GameRule.HUNGER, RuleResult.DENY);

            game.on(RequestStartListener.EVENT, waiting::requestStart);
            game.on(OfferPlayerListener.EVENT, waiting::offerPlayer);

            game.on(PlayerAddListener.EVENT, waiting::addPlayer);
            game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
        });
    }

    private JoinResult offerPlayer(ServerPlayerEntity player) {
        if (this.gameWorld.getPlayerCount() >= this.config.players.getMaxPlayers()) {
            return JoinResult.gameFull();
        }

        return JoinResult.ok();
    }

    private StartResult requestStart() {
        if (this.gameWorld.getPlayerCount() < this.config.players.getMinPlayers()) {
            return StartResult.NOT_ENOUGH_PLAYERS;
        }
        CastleWarsGame.open(this.gameWorld, this.map, this.config);
        return StartResult.OK;
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayerToLobby(player);
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        return this.playerDied(player);
    }

    private ActionResult playerDied(ServerPlayerEntity player) {
        player.setHealth(20);
        spawnPlayerToLobby(player);
        return ActionResult.PASS;
    }

    private void spawnPlayerToLobby(ServerPlayerEntity player) {
        this.playerManager.onParticipantJoin(player);
    }

}
