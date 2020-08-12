package io.github.hydos.castlewars.game.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import xyz.nucleoid.plasmid.fake.Fake;

public class FakeBlockItem extends BlockItem implements Fake<Item> {

    public FakeBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Item getFaking() {
        return Blocks.SMOOTH_STONE_SLAB.asItem();
    }
}
