package me.hydos.castlewars.game.core.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;

public class MapConfig {

    public static final Codec<MapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("platformSize", 10).forGetter(config -> config.platformSize),
            BlockPos.CODEC.optionalFieldOf("npcPos", new BlockPos(0, 0, 0)).forGetter(config -> config.npcPos),
            Codec.INT.optionalFieldOf("platformOffset", 40).forGetter(config -> config.platformSize)
    ).apply(instance, MapConfig::new));

    public final int platformSize;
    public final double platformOffset;
    private final BlockPos npcPos;

    public MapConfig(int platformSize, BlockPos npcPos, double platformOffset) {
        this.platformSize = platformSize;
        this.npcPos = npcPos;
        this.platformOffset = platformOffset;
    }
}
