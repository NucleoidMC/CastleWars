package io.github.hydos.castlewars.game.map;

import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.game.map.template.MapTemplate;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameParameterValue")
public class MapGenerator {

    public final CastleWarsConfig config;

    public MapGenerator(CastleWarsConfig config) {
        this.config = config;
    }

    public CompletableFuture<CastleWarsMap> create() {
        return CompletableFuture.supplyAsync(this::build, Util.getMainWorkerExecutor());
    }

    private CastleWarsMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        CastleWarsMap map = new CastleWarsMap(template);

        int centerPos = config.map.platformSize / 2;

        this.createTeamPlatform(template, 0, 80, Blocks.BLUE_TERRACOTTA, config.map.platformSize);
        this.createTeamPlatform(template, 40, 80, Blocks.RED_TERRACOTTA, config.map.platformSize);

        this.createLobbyPlatform(template, 20, 200, Blocks.GLASS, 20, 3);

        //set glass platform spawn
        map.setSpawn(new BlockPos(30, 203, 10));

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

    private void createLobbyPlatform(MapTemplate template, int xPlatformOffset, int y, Block block, int width, int wallHeight) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        createTeamPlatform(template, xPlatformOffset, y, block, width);
        for (int posy = y; posy < y + wallHeight; posy++) {
            for (int x = xPlatformOffset; x < width + xPlatformOffset; x++) {
                for (int z = 0; z < width; z++) {
                    if (x == xPlatformOffset) {
                        mutablePos.set(x, posy, z);
                    } else if (x == xPlatformOffset + width - 1) {
                        mutablePos.set(x, posy, z);
                    } else if (z == width - 1) {
                        mutablePos.set(x, posy, z);
                    } else if (z == 0) {
                        mutablePos.set(x, posy, z);
                    }
                    template.setBlockState(mutablePos, block.getDefaultState());
                }
            }
        }
    }
}
