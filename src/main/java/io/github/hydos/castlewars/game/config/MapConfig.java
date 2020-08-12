package io.github.hydos.castlewars.game.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;

public class MapConfig {

    public static final Codec<MapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("platformSize", 10).forGetter(config -> config.platformSize),
            BlockPos.field_25064.optionalFieldOf("npcPos", new BlockPos(0,0,0)).forGetter(config -> config.npcPos),
            Codec.INT.optionalFieldOf("platformOffset", 40).forGetter(config -> config.platformSize)
    ).apply(instance, MapConfig::new));

    public final int platformSize;
    private final BlockPos npcPos;
    public final double platformOffset;

    public MapConfig(int platformSize, BlockPos npcPos, double platformOffset) {
        this.platformSize = platformSize;
        this.npcPos = npcPos;
        this.platformOffset = platformOffset;
    }
}
