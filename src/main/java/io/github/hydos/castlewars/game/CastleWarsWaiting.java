package io.github.hydos.castlewars.game;

import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import io.github.hydos.castlewars.game.map.MapGenerator;
import net.gegy1000.plasmid.game.GameWorld;
import net.gegy1000.plasmid.game.GameWorldState;
import net.gegy1000.plasmid.game.StartResult;
import net.gegy1000.plasmid.game.event.OfferPlayerListener;
import net.gegy1000.plasmid.game.event.PlayerAddListener;
import net.gegy1000.plasmid.game.event.PlayerDeathListener;
import net.gegy1000.plasmid.game.event.RequestStartListener;
import net.gegy1000.plasmid.game.player.JoinResult;
import net.gegy1000.plasmid.game.rule.GameRule;
import net.gegy1000.plasmid.game.rule.RuleResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class CastleWarsWaiting {

    private final GameWorld gameWorld;
    private final CastleWarsMap map;
    private final CastleWarsConfig config;

    private final PlayerManager playerManager;

    private CastleWarsWaiting(GameWorld gameWorld, CastleWarsMap map, CastleWarsConfig config) {
        this.gameWorld = gameWorld;
        this.map = map;
        this.config = config;

        this.playerManager = new PlayerManager(gameWorld, map);
    }

    public static CompletableFuture<Void> open(GameWorldState worldState, CastleWarsConfig config) {
        MapGenerator generator = new MapGenerator(config);

        return generator.create().thenAccept(map -> {
            GameWorld gameWorld = worldState.openWorld(map.asGenerator());

            CastleWarsWaiting waiting = new CastleWarsWaiting(gameWorld, map, config);

            gameWorld.newGame(game -> {
                game.setRule(GameRule.ALLOW_CRAFTING, RuleResult.ALLOW);
                game.setRule(GameRule.ALLOW_PORTALS, RuleResult.DENY);
                game.setRule(GameRule.ALLOW_PVP, RuleResult.DENY);
                game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
                game.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);

                game.on(RequestStartListener.EVENT, waiting::requestStart);
                game.on(OfferPlayerListener.EVENT, waiting::offerPlayer);

                game.on(PlayerAddListener.EVENT, waiting::addPlayer);
                game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
            });
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
            return StartResult.notEnoughPlayers();
        }
        CastleWarsGame.open(this.gameWorld, this.map, this.config);
        return StartResult.ok();
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayerToLobby(player);
    }

    private boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        this.playerDied(player);
        return true;
    }

    private void playerDied(ServerPlayerEntity player) {
        player.setHealth(20);
        spawnPlayerToLobby(player);
    }

    private void spawnPlayerToLobby(ServerPlayerEntity player) {
        this.playerManager.onParticipantJoin(player);
    }

}
