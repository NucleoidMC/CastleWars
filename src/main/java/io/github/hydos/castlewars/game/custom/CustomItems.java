package io.github.hydos.castlewars.game.custom;

import io.github.hydos.castlewars.CastleWars;
import net.gegy1000.plasmid.item.CustomItem;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class CustomItems {

    public static final CustomItem SUPER_ROCKET = CustomItem.builder()
            .id(new Identifier(CastleWars.ID, "super_rocket"))
            .name(new LiteralText("Super Rocket"))
            .register();

}
