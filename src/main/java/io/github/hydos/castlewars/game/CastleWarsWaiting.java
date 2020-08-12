package io.github.hydos.castlewars.game;

import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import io.github.hydos.castlewars.game.map.MapGenerator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.game.world.bubble.BubbleWorldConfig;

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

    public static CompletableFuture<Void> open(MinecraftServer server, CastleWarsConfig config) {
        MapGenerator generator = new MapGenerator(config);

        return generator.create().thenAccept(map -> {
            BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                    .setGenerator(map.asGenerator(server))
                    .setDefaultGameMode(GameMode.SPECTATOR);
            GameWorld gameWorld = GameWorld.open(server, worldConfig);

            CastleWarsWaiting waiting = new CastleWarsWaiting(gameWorld, map, config);

            gameWorld.openGame(game -> {
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
