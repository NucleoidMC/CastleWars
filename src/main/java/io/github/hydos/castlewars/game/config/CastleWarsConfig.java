package io.github.hydos.castlewars.game.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.hydos.castlewars.game.map.MapConfig;
import net.gegy1000.plasmid.game.config.GameConfig;
import net.gegy1000.plasmid.game.config.PlayerConfig;

public class CastleWarsConfig implements GameConfig {

    public static final Codec<CastleWarsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MapConfig.CODEC.fieldOf("map").forGetter(config -> config.map),
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.players)
    ).apply(instance, CastleWarsConfig::new));


    public final MapConfig map;
    public final PlayerConfig players;

    public CastleWarsConfig(MapConfig map, PlayerConfig players) {
        this.map = map;
        this.players = players;
    }
}
