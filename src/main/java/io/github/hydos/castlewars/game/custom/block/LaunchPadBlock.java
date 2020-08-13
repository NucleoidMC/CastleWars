package io.github.hydos.castlewars.game.custom.block;

import io.github.hydos.castlewars.game.custom.block.entity.LaunchPadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
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
import java.util.Random;

public class LaunchPadBlock extends BlockWithEntity implements FakeBlock<Block> {

    public static final Random random = new Random();

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
        return Blocks.SMOOTH_STONE_SLAB;
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
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextDouble() > 0.5) {
            //fling the block above the launch pad
            BlockState block = world.getBlockState(pos.add(0, 1, 0));

            if (block.getBlock() == Blocks.TNT) {
                boolean blueteam = pos.getX() < 31; //blue should be on that side. this hardcoding kills me

                TntEntity blockEntity = new TntEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), null);
                blockEntity.setVelocity(blueteam ? 1.4f : -1.4f, random.nextFloat(), 0);

                world.spawnEntity(blockEntity);
            }
        }
        super.randomTick(state, world, pos, random);
    }
}
