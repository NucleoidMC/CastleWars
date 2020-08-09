package io.github.hydos.castlewars.game.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SuperRocketEntity extends EndCrystalEntity {

    public SuperRocketEntity(World world) {
        super(EntityType.END_CRYSTAL, world);
    }

    public SuperRocketEntity(ServerWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }
}
