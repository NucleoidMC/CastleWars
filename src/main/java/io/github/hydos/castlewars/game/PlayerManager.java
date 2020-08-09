package io.github.hydos.castlewars.game;

import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.gegy1000.plasmid.game.GameWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    public List<ServerPlayerEntity> gamePlayers = new ArrayList<>();
    GameWorld gameWorld;
    CastleWarsMap map;

    public PlayerManager(GameWorld gameWorld, CastleWarsMap map) {
        INSTANCE = this;
        this.gameWorld = gameWorld;
        this.map = map;
    }

    public static PlayerManager getInstance() {
        return INSTANCE;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gamemode) {
        player.setGameMode(gamemode);
        map.spawnPlayerIntoLobby(player, gameWorld.getWorld());
    }

    public void spawnPlayerInLobby(ServerPlayerEntity player) {
        gamePlayers.add(player);
        resetPlayer(player, GameMode.ADVENTURE);
    }

    public void tick() {
    }
}
