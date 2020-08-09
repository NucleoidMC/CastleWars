package io.github.hydos.castlewars.game.map;

import net.gegy1000.plasmid.game.map.template.MapTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.CompletableFuture;

public class MapGenerator {

    private final MapConfig config;

    public MapGenerator(MapConfig config) {
        this.config = config;
    }

    public CompletableFuture<CastleWarsMap> create() {
        return CompletableFuture.supplyAsync(this::build, Util.getServerWorkerExecutor());
    }

    private CastleWarsMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        CastleWarsMap map = new CastleWarsMap(template, this.config);

        this.createTeamPlatform(template, 0, 60, 0, Blocks.BLUE_TERRACOTTA, 10);
        this.createTeamPlatform(template, 40, 60, 0, Blocks.RED_TERRACOTTA, 10);

        map.setSpawn(new BlockPos(0, 62, 0));

        return map;
    }

    private void createTeamPlatform(MapTemplate template, int xPlatformOffset, int y, int zPlatformOffset, Block block, int width) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = xPlatformOffset; x < width + xPlatformOffset; x++) {
            for (int z = zPlatformOffset; z < width + zPlatformOffset; z++) {
                mutablePos.set(x, y, z);
                template.setBlockState(mutablePos, block.getDefaultState());
            }
        }
    }
}
