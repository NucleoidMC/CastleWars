package me.hydos.castlewars.game.core;

import com.google.common.collect.Multimap;
import me.hydos.castlewars.game.core.config.CastleWarsConfig;
import me.hydos.castlewars.game.ingame.CastleWarsGame;
import me.hydos.castlewars.game.ingame.CastleWarsPlayer;
import me.hydos.castlewars.game.map.CastleWarsMap;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.player.GameTeam;
import xyz.nucleoid.plasmid.game.player.TeamAllocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    public final Map<ServerPlayerEntity, CastleWarsPlayer> participants = new HashMap<>();
    public final Map<GameTeam, CastleWarsGame.TeamState> teams = new HashMap<>();
    final GameSpace gameWorld;
    final CastleWarsMap map;
    public List<ServerPlayerEntity> lobbyPlayers = new ArrayList<>();

    public PlayerManager(GameSpace gameWorld, CastleWarsMap map) {
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

    public void onParticipantJoin(ServerPlayerEntity player) {
        lobbyPlayers.add(player);
        resetPlayer(player, GameMode.ADVENTURE);
    }

    public void makePlayersActive(CastleWarsConfig config) {
        allocatePlayers(config).forEach((team, player) -> participants.put(player, new CastleWarsPlayer(team, player)));
        for (GameTeam team : config.teams) {
            List<CastleWarsPlayer> participants = this.getTeamPlayers(team).collect(Collectors.toList());
            if (!participants.isEmpty()) {
                CastleWarsGame.TeamState teamState = new CastleWarsGame.TeamState(team, config);
                participants.forEach(participant -> teamState.players.add(participant.player()));
                this.teams.put(team, teamState);
            }
            for (CastleWarsPlayer player : participants) {
                player.player().networkHandler.sendPacket(new WorldBorderS2CPacket(teams.get(getPlayersTeam(player)).border, WorldBorderS2CPacket.Type.INITIALIZE));
            }
        }
        lobbyPlayers = null;
    }

    public GameTeam getPlayersTeam(ServerPlayerEntity player) {
        for (GameTeam team : teams.keySet()) {
            for (Object teamPlayerObject : getTeamPlayers(team).toArray()) {
                CastleWarsPlayer teamPlayer = (CastleWarsPlayer) teamPlayerObject;
                if (teamPlayer.player().getUuid() == player.getUuid()) {
                    return team;
                }
            }
        }
        throw new RuntimeException("Failed to get players team");
    }

    public GameTeam getPlayersTeam(CastleWarsPlayer player) {
        for (GameTeam team : teams.keySet()) {
            for (Object teamPlayerObject : getTeamPlayers(team).toArray()) {
                CastleWarsPlayer teamPlayer = (CastleWarsPlayer) teamPlayerObject;
                if (teamPlayer.player().getUuid() == player.player().getUuid()) {
                    return team;
                }
            }
        }
        throw new RuntimeException("Failed to retrieve player's game team");
    }

    public boolean isParticipant(ServerPlayerEntity player) {
        return participants.containsKey(player);
    }

    public Stream<CastleWarsPlayer> getTeamPlayers(GameTeam team) {
        return this.participants.values().stream().filter(participant -> participant.team == team);
    }

    public Stream<CastleWarsPlayer> participants() {
        return this.participants.values().stream();
    }

    public Stream<CastleWarsGame.TeamState> teams() {
        return this.teams.values().stream();
    }

    private Multimap<GameTeam, ServerPlayerEntity> allocatePlayers(CastleWarsConfig config) {
        TeamAllocator<GameTeam, ServerPlayerEntity> allocator = new TeamAllocator<>(config.teams);
        for (ServerPlayerEntity player : this.gameWorld.getPlayers()) {
            allocator.add(player, config.teams.get(0));
        }
        return allocator.build();
    }

    public void removeWorldBorder(CastleWarsConfig config) {
        for (GameTeam team : config.teams) {
            List<CastleWarsPlayer> participants = this.getTeamPlayers(team).collect(Collectors.toList());
            for (CastleWarsPlayer player : participants) {
                WorldBorder border = teams.get(getPlayersTeam(player)).border;
                border.setSize(100000);
                player.player().networkHandler.sendPacket(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_SIZE));
            }
        }

    }
}
