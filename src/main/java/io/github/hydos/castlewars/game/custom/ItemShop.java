package io.github.hydos.castlewars.game.custom;

import io.github.hydos.castlewars.CastleWars;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.shop.Cost;
import xyz.nucleoid.plasmid.shop.ShopUi;

public class ItemShop {

    public static ShopUi create() {
        return ShopUi.create(new LiteralText("Item Shop"), shop -> shop.addItem(new ItemStack(CastleWars.LAUNCH_PAD_BLOCK), Cost.free()));
    }

}
