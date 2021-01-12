package me.hydos.castlewars.block;

import me.hydos.castlewars.block.entity.LaunchPadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import xyz.nucleoid.plasmid.fake.FakeBlock;

import javax.annotation.Nullable;

public class LaunchPadBlock extends BlockWithEntity implements FakeBlock {

    public LaunchPadBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new LaunchPadBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
    }

    @Override
    public Block asProxy() {
        return Blocks.SMOOTH_STONE_SLAB;
    }

    @Override
    public BlockState asProxy(BlockState blockState) {
        return Blocks.SMOOTH_STONE_SLAB.getDefaultState();
    }
}
