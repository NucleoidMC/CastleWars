package io.github.hydos.castlewars.game.map;

import io.github.hydos.castlewars.CastleWars;
import io.github.hydos.castlewars.game.entities.ProtectThisEntity;
import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import net.gegy1000.plasmid.game.map.template.MapTemplate;
import net.gegy1000.plasmid.game.map.template.TemplateChunkGenerator;
import net.gegy1000.plasmid.game.player.GameTeam;
import net.gegy1000.plasmid.util.BlockBounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CastleWarsMap {

    public final MapTemplate template;
    public final BlockPos redTeamSpawn = new BlockPos(45, 81, 5);
    public final BlockPos blueTeamSpawn = new BlockPos(5, 81, 5);
    public BlockPos defaultSpawn;

    public CastleWarsMap(MapTemplate template) {
        this.template = template;
    }

    public void setSpawn(BlockPos blockPos) {
        this.defaultSpawn = blockPos;
    }

    public ChunkGenerator asGenerator() {
        return new TemplateChunkGenerator(this.template, BlockPos.ORIGIN);
    }

    public void spawnPlayerIntoLobby(ServerPlayerEntity player, ServerWorld world) {
        world.getChunkManager().addTicket(ChunkTicketType.field_19347, new ChunkPos(defaultSpawn), 4, player.getEntityId());
        player.teleport(world, defaultSpawn.getX(), defaultSpawn.getY(), defaultSpawn.getZ(), 0, 0);
    }

    public void spawnPlayerTeamRed(ServerPlayerEntity player, ServerWorld world) {
        world.getChunkManager().addTicket(ChunkTicketType.field_19347, new ChunkPos(redTeamSpawn), 4, player.getEntityId());
        player.teleport(world, redTeamSpawn.getX(), redTeamSpawn.getY(), redTeamSpawn.getZ(), 0, 0);
    }

    public void spawnPlayerTeamBlue(ServerPlayerEntity player, ServerWorld world) {
        world.getChunkManager().addTicket(ChunkTicketType.field_19347, new ChunkPos(blueTeamSpawn), 4, player.getEntityId());
        player.teleport(world, blueTeamSpawn.getX(), blueTeamSpawn.getY(), blueTeamSpawn.getZ(), 0, 0);
    }

    public void trySpawnEntity(Entity entity, BlockBounds bounds) {
        Vec3d center = bounds.getCenter();

        entity.refreshPositionAndAngles(center.x, bounds.getMin().getY(), center.z, 0.0F, 0.0F);

        if (entity instanceof MobEntity) {
            MobEntity mob = (MobEntity) entity;

            LocalDifficulty difficulty = entity.world.getLocalDifficulty(mob.getBlockPos());
            mob.initialize(entity.world, difficulty, SpawnReason.COMMAND, null, null);
        }

        if (!entity.world.spawnEntity(entity)) {
            CastleWars.LOGGER.warn("Tried to spawn entity ({}) but the chunk was not loaded", entity);
        }
    }

    public void trySpawnEntity(Entity entity, BlockPos pos) {
        BlockBounds bounds = new BlockBounds(pos, pos);
        Vec3d center = bounds.getCenter();

        entity.refreshPositionAndAngles(center.x, bounds.getMin().getY(), center.z, 0.0F, 0.0F);

        if (entity instanceof MobEntity) {
            MobEntity mob = (MobEntity) entity;

            LocalDifficulty difficulty = entity.world.getLocalDifficulty(mob.getBlockPos());
            mob.initialize(entity.world, difficulty, SpawnReason.COMMAND, null, null);
        }

        if (!entity.world.spawnEntity(entity)) {
            CastleWars.LOGGER.warn("Tried to spawn entity ({}) but the chunk was not loaded", entity);
        }
    }

    public void spawnVillagers(CastleWarsGame game) {
        for (GameTeam team : game.config.teams) {
            switch (team.getDye()) {
                case RED:
                    BlockPos pos = new BlockPos(60, 81, 15);
                    trySpawnEntity(new ProtectThisEntity(game.world, team, game), pos);
                    break;
                case BLUE:
                    pos = new BlockPos(10, 81, 15);
                    trySpawnEntity(new ProtectThisEntity(game.world, team, game), pos);
                    break;
            }
        }
    }
}
