package me.hydos.castlewars.block.entity;

import me.hydos.castlewars.CastleWars;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Tickable;

import java.util.Random;

public class LaunchPadBlockEntity extends BlockEntity implements Tickable {

    public static final Random random = new Random();

    public int ticks = 0;

    public LaunchPadBlockEntity() {
        super(CastleWars.LAUNCH_PAD_BLOCKENTITY);
    }

    @Override
    public void tick() {
        if (ticks % (20 * 3) == 0) {
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
        }
        ticks++;
    }
}
