package io.github.hydos.castlewars.game;

import com.google.common.collect.Multimap;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import io.github.hydos.castlewars.game.ingame.CastleWarsPlayer;
import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameWorld;
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
    public List<ServerPlayerEntity> lobbyPlayers = new ArrayList<>();
    final GameWorld gameWorld;
    final CastleWarsMap map;

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
        System.out.println("Failed to get players team!");
        return null;
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
        System.out.println("Failed to get players team!");
        return null;
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
            allocator.add(player, config.teams.get(0));//make the allocator pick a team
        }
        return allocator.build();
    }
}
