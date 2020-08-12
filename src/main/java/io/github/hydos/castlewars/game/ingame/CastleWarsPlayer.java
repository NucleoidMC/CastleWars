package io.github.hydos.castlewars.game.ingame;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.player.GameTeam;
import xyz.nucleoid.plasmid.util.PlayerRef;

public class CastleWarsPlayer {

    private final ServerPlayerEntity player;
    public final GameTeam team;
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
