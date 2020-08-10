package io.github.hydos.castlewars.game.custom;

import net.gegy1000.plasmid.shop.Cost;
import net.gegy1000.plasmid.shop.ShopUi;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public class ItemShop {

    public static ShopUi create() {
        return ShopUi.create(new LiteralText("Item Shop"), shop -> shop.addItem(CustomGameObjects.SUPER_ROCKET_LAUNCH_PAD.getItem().applyTo(new ItemStack(Blocks.SMOOTH_STONE_SLAB, 1)), Cost.free()));
    }

}
