package me.hydos.castlewars.tag;

import me.hydos.castlewars.CastleWars;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class Tags {

	public static final Tag<Block> BLACKLISTED_BLOCKS = TagRegistry.block(CastleWars.id("blacklist"));
	public static final Tag<Item> BLACKLISTED_ITEMS = TagRegistry.item(CastleWars.id("blacklist"));
}
