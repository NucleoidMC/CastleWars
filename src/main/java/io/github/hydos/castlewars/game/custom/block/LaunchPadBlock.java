package io.github.hydos.castlewars.game.custom.block;

import io.github.hydos.castlewars.game.custom.block.entity.LaunchPadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.fake.FakeBlock;

import javax.annotation.Nullable;

public class LaunchPadBlock extends BlockWithEntity implements FakeBlock<Block> {

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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.SUCCESS;
    }
}
