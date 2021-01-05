package io.github.hydos.castlewars.game.ingame;

import io.github.hydos.castlewars.game.PlayerManager;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.player.GameTeam;

import java.util.*;

public class CastleWarsScoreboard {

    private final CastleWarsGame game;

    private final Map<GameTeam, Team> scoreboardTeams = new HashMap<>();
    private final ScoreboardObjective objective;

    private boolean dirty = true;

    private long ticks;

    public CastleWarsScoreboard(CastleWarsGame game, ScoreboardObjective objective) {
        this.game = game;
        this.objective = objective;
    }

    public static CastleWarsScoreboard create(CastleWarsGame game) {
        MinecraftServer server = game.gameWorld.getWorld().getServer();
        ServerScoreboard scoreboard = server.getScoreboard();

        ScoreboardObjective objective = new ScoreboardObjective(
                scoreboard,
                "castle_wars",
                ScoreboardCriterion.DUMMY,
                new LiteralText("Castle Wars").formatted(Formatting.AQUA, Formatting.BOLD),
                ScoreboardCriterion.RenderType.INTEGER
        );

        scoreboard.addScoreboardObjective(objective);

        scoreboard.setObjectiveSlot(1, objective);

        return new CastleWarsScoreboard(game, objective);
    }

    private static void render(ServerScoreboard scoreboard, ScoreboardObjective objective, String[] lines) {
        clear(scoreboard, objective);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            scoreboard.getPlayerScore(line, objective).setScore(lines.length - i);
        }
    }

    private static void clear(ServerScoreboard scoreboard, ScoreboardObjective objective) {
        Collection<ScoreboardPlayerScore> existing = scoreboard.getAllPlayerScores(objective);
        for (ScoreboardPlayerScore score : existing) {
            scoreboard.resetPlayerScore(score.getPlayerName(), objective);
        }
    }

    public void addTeam(GameTeam team) {
        PlayerManager.getInstance().getTeamPlayers(team).forEach(participant -> {
            ServerPlayerEntity player = participant.player();
            if (player == null) return;

            this.addPlayer(player, team);
        });
    }

    private void addPlayer(ServerPlayerEntity player, GameTeam team) {
        MinecraftServer server = this.game.world.getServer();

        ServerScoreboard scoreboard = server.getScoreboard();
        scoreboard.addPlayerToTeam(player.getEntityName(), this.scoreboardTeam(team));
    }

    public Team scoreboardTeam(GameTeam team) {
        return this.scoreboardTeams.computeIfAbsent(team, t -> {
            MinecraftServer server = this.game.world.getServer();
            ServerScoreboard scoreboard = server.getScoreboard();
            String teamKey = t.getDisplay();
            Team scoreboardTeam = scoreboard.getTeam(teamKey);
            if (scoreboardTeam == null) {
                scoreboardTeam = scoreboard.addTeam(teamKey);
                scoreboardTeam.setColor(team.getFormatting());
                scoreboardTeam.setCollisionRule(AbstractTeam.CollisionRule.NEVER);
            }
            return scoreboardTeam;
        });
    }

    public void tick() {
        ticks++;
        if (dirty || this.ticks % 20 == 0) {
            this.rerender();
            this.dirty = false;
        }
    }

    public void markDirty() {
        this.dirty = true;
    }

    private void rerender() {
        List<String> lines = new ArrayList<>(10);

        long seconds = (this.ticks / 20) % 60;
        long minutes = this.ticks / (20 * 60);

        lines.add(String.format("%sTime: %s%02d:%02d", Formatting.RED.toString() + Formatting.BOLD, Formatting.RESET, minutes, seconds));

        long playersAlive = PlayerManager.getInstance().participants()
                .filter(participant -> !participant.eliminated && participant.isOnline())
                .count();
        lines.add(Formatting.BLUE.toString() + playersAlive + " players alive");
        lines.add("");

        lines.add(Formatting.BOLD + "Teams:");
        PlayerManager.getInstance().teams().forEach(teamState -> {
            long totalPlayerCount = PlayerManager.getInstance().getTeamPlayers(teamState.team).count();
            long alivePlayerCount = PlayerManager.getInstance().getTeamPlayers(teamState.team)
                    .filter(participant -> !participant.eliminated)
                    .count();

            if (!teamState.eliminated) {
                String state = alivePlayerCount + "/" + totalPlayerCount;

                String nameFormat = teamState.team.getFormatting().toString() + Formatting.BOLD.toString();
                String descriptionFormat = Formatting.RESET.toString() + Formatting.GRAY.toString();

                String name = teamState.team.getDisplay();
                lines.add("  " + nameFormat + name + ": " + descriptionFormat + state);
            } else {
                String nameFormat = teamState.team.getFormatting().toString() + Formatting.BOLD.toString() + Formatting.STRIKETHROUGH.toString();
                String descriptionFormat = Formatting.RESET.toString() + Formatting.RED.toString();

                String name = teamState.team.getDisplay();
                lines.add("  " + nameFormat + name + descriptionFormat + ": eliminated!");
            }
        });

        this.render(lines.toArray(new String[0]));
    }

    private void render(String[] lines) {
        MinecraftServer server = this.game.world.getServer();
        ServerScoreboard scoreboard = server.getScoreboard();

        render(scoreboard, this.objective, lines);
    }


    public void close() {
        MinecraftServer server = this.game.world.getServer();

        ServerScoreboard scoreboard = server.getScoreboard();
        this.scoreboardTeams.values().forEach(scoreboard::removeTeam);

        scoreboard.removeObjective(this.objective);
    }
}
