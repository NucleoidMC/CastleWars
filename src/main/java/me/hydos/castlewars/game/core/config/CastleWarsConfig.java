package me.hydos.castlewars.game.core.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.player.GameTeam;

import java.util.List;

public class CastleWarsConfig {

    public static final Codec<CastleWarsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MapConfig.CODEC.fieldOf("map").forGetter(config -> config.map),
            Codec.BOOL.optionalFieldOf("bridges", false).forGetter(config -> config.bridges),
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.players),
            GameTeam.CODEC.listOf().fieldOf("teams").forGetter(config -> config.teams)
    ).apply(instance, CastleWarsConfig::new));


    public final MapConfig map;
    public final PlayerConfig players;
    public final List<GameTeam> teams;
    public final boolean bridges;

    public CastleWarsConfig(MapConfig map, boolean bridges, PlayerConfig players, List<GameTeam> teams) {
        this.map = map;
        this.bridges = bridges;
        this.players = players;
        this.teams = teams;
    }
}
