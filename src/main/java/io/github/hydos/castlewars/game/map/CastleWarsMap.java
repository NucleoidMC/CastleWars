package io.github.hydos.castlewars.game.map;

import net.gegy1000.plasmid.game.map.template.MapTemplate;
import net.gegy1000.plasmid.game.map.template.TemplateChunkGenerator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CastleWarsMap {

    private final MapTemplate template;
    private final BlockPos redTeamSpawn = new BlockPos(45, 81, 5);
    private final BlockPos blueTeamSpawn = new BlockPos(5, 81, 5);
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

    public void spawnPlayerTeamRed(ServerPlayerEntity player, ServerWorld world) {
        player.teleport(world, redTeamSpawn.getX(), redTeamSpawn.getY(), redTeamSpawn.getZ(), 0, 0);
    }

    public void spawnPlayerTeamBlue(ServerPlayerEntity player, ServerWorld world) {
        player.teleport(world, blueTeamSpawn.getX(), blueTeamSpawn.getY(), blueTeamSpawn.getZ(), 0, 0);
    }
}
