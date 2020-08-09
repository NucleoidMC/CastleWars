package io.github.hydos.castlewars.game.map;

import net.gegy1000.plasmid.game.map.template.MapTemplate;
import net.gegy1000.plasmid.game.map.template.TemplateChunkGenerator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CastleWarsMap {

    private final MapTemplate template;

    private BlockPos defaultSpawn;
    
    public CastleWarsMap(MapTemplate template, MapConfig config) {
        this.template = template;
    }

    public void setSpawn(BlockPos blockPos) {
        this.defaultSpawn = blockPos;
    }

    public ChunkGenerator asGenerator() {
        return new TemplateChunkGenerator(this.template, BlockPos.ORIGIN);
    }

    public void spawnPlayerIntoLobby(ServerPlayerEntity player, ServerWorld world) {
        player.teleport(world, defaultSpawn.getX(), defaultSpawn.getY(), defaultSpawn.getZ(), 0, 0);
    }
}
