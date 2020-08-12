package io.github.hydos.castlewars.game.custom;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.shop.Cost;
import xyz.nucleoid.plasmid.shop.ShopUi;

public class ItemShop {

    public static ShopUi create() {
        return ShopUi.create(new LiteralText("Item Shop"), shop -> shop.addItem(CustomGameObjects.SUPER_ROCKET_LAUNCH_PAD.getItem().applyTo(new ItemStack(Blocks.SMOOTH_STONE_SLAB, 1)), Cost.free()));
    }

}
