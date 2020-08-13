package io.github.hydos.castlewars.game.custom.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.world.World;

public class GoodFallingBlock extends FallingBlockEntity {

    public GoodFallingBlock(World world, double x, double y, double z) {
        super(world, x, y, z, Blocks.TNT.getDefaultState());
    }

}
