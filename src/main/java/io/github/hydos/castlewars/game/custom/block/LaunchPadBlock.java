package io.github.hydos.castlewars.game.custom.block;

import io.github.hydos.castlewars.game.custom.block.entity.LaunchPadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.fake.FakeBlock;
import xyz.nucleoid.plasmid.fake.FakeItem;

import javax.annotation.Nullable;
import java.util.Random;

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
