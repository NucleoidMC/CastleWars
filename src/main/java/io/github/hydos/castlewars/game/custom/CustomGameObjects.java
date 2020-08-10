package io.github.hydos.castlewars.game.custom;

import io.github.hydos.castlewars.CastleWars;
import net.gegy1000.plasmid.item.CustomBlockItem;
import net.minecraft.block.Blocks;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class CustomGameObjects {

    public static final CustomBlockItem SUPER_ROCKET_LAUNCH_PAD = new CustomBlockItem(
            new LiteralText("Super Rocket Launch Pad").formatted(Formatting.RED, Formatting.BOLD),
            new Identifier(CastleWars.ID, "super_rocket_launch_pad"),
            Blocks.SMOOTH_STONE_SLAB.getDefaultState()
    );

}
