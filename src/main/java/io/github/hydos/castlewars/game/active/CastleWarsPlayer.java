package io.github.hydos.castlewars.game.active;

import net.gegy1000.plasmid.game.player.GameTeam;
import net.minecraft.server.network.ServerPlayerEntity;

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
        return player == null;
    }
}
