package io.github.hydos.castlewars.game.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import xyz.nucleoid.plasmid.fake.FakeBlock;

public class LaunchPadBlock extends Block implements FakeBlock<Block> {

    public LaunchPadBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getFaking(BlockState blockState) {
        return Blocks.SMOOTH_STONE_SLAB.getDefaultState();
    }

    @Override
    public FluidState getFaking(FluidState fluidState) {
        return null;
    }

    @Override
    public Block getFaking() {
        return Blocks.REDSTONE_LAMP;
    }
}
