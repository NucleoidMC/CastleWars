package io.github.hydos.castlewars.game.map;

import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import net.gegy1000.plasmid.game.map.template.MapTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.CompletableFuture;

public class MapGenerator {

    public CastleWarsConfig config;

    public MapGenerator(CastleWarsConfig config) {
        this.config = config;
    }

    public CompletableFuture<CastleWarsMap> create() {
        return CompletableFuture.supplyAsync(this::build, Util.getServerWorkerExecutor());
    }

    private CastleWarsMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        CastleWarsMap map = new CastleWarsMap(template);

        this.createTeamPlatform(template, 0, 80, Blocks.BLUE_TERRACOTTA, config.map.platformSize);
        this.createTeamPlatform(template, 40, 80, Blocks.RED_TERRACOTTA, config.map.platformSize);
        this.createTeamPlatform(template, 40, 200, Blocks.GLASS, 20);

        map.setSpawn(new BlockPos(50, 201, 10));

        return map;
    }

    private void createTeamPlatform(MapTemplate template, int xPlatformOffset, int y, Block block, int width) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = xPlatformOffset; x < width + xPlatformOffset; x++) {
            for (int z = 0; z < width; z++) {
                mutablePos.set(x, y, z);
                template.setBlockState(mutablePos, block.getDefaultState());
            }
        }
    }
}
