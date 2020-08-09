package io.github.hydos.castlewars.game.custom;

import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import net.gegy1000.plasmid.shop.Cost;
import net.gegy1000.plasmid.shop.ShopUi;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class ItemShop {

    public static ShopUi create() {
        return ShopUi.create(new LiteralText("Item Shop"), shop -> shop.addItem(CustomItems.SUPER_ROCKET.applyTo(new ItemStack(Items.END_CRYSTAL, 1)), Cost.ofEmeralds(4)));
    }

}
