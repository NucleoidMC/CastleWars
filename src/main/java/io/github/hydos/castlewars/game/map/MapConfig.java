package io.github.hydos.castlewars.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class MapConfig {

    public static final Codec<MapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.optionalFieldOf("floor", Blocks.RED_TERRACOTTA.getDefaultState()).forGetter(config -> config.floor)
    ).apply(instance, MapConfig::new));
    public final BlockState floor;

    public MapConfig(BlockState floor) {
        this.floor = floor;
    }
}
