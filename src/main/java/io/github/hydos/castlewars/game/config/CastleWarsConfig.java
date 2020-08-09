package io.github.hydos.castlewars.game.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.hydos.castlewars.game.map.MapConfig;
import net.gegy1000.plasmid.game.config.GameConfig;
import net.gegy1000.plasmid.game.config.PlayerConfig;
import net.gegy1000.plasmid.game.player.GameTeam;

import java.util.List;

public class CastleWarsConfig implements GameConfig {

    public static final Codec<CastleWarsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MapConfig.CODEC.fieldOf("map").forGetter(config -> config.map),
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.players),
            GameTeam.CODEC.listOf().fieldOf("teams").forGetter(config -> config.teams)
    ).apply(instance, CastleWarsConfig::new));


    public final MapConfig map;
    public final PlayerConfig players;
    public List<GameTeam> teams;

    public CastleWarsConfig(MapConfig map, PlayerConfig players, List<GameTeam> teams) {
        this.map = map;
        this.players = players;
        this.teams = teams;
    }
}
