package io.github.hydos.castlewars.game.ingame;

import net.gegy1000.plasmid.game.player.GameTeam;
import net.gegy1000.plasmid.util.PlayerRef;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class CastleWarsPlayer {

    private final ServerPlayerEntity player;
    public GameTeam team;
    public boolean eliminated;

    public CastleWarsPlayer(GameTeam team, ServerPlayerEntity playerEntity) {
        this.player = playerEntity;
        this.team = team;
    }

    public ServerPlayerEntity player() {
        return player;
    }

    public boolean isOnline() {
        return player != null;
    }

    public PlayerRef playerRef() {
        return PlayerRef.of(player);
    }

    public void gamemode(GameMode gamemode) {
        player.setGameMode(gamemode);
    }
}
