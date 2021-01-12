package me.hydos.castlewars;

import me.hydos.castlewars.game.waiting.CastleWarsWaiting;
import me.hydos.castlewars.game.core.config.CastleWarsConfig;
import me.hydos.castlewars.block.LaunchPadBlock;
import me.hydos.castlewars.block.entity.LaunchPadBlockEntity;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.fake.FakeBlockItem;
import xyz.nucleoid.plasmid.game.GameType;


public class CastleWars implements DedicatedServerModInitializer {

    public static final String ID = "castle_wars";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final boolean DEBUGGING = false;

    public static final LaunchPadBlock LAUNCH_PAD_BLOCK = Registry.register(Registry.BLOCK, CastleWars.id("launch_pad"), new LaunchPadBlock(AbstractBlock.Settings.of(Material.METAL)));
    public static final Item LAUNCH_PAD_BLOCK_ITEM = Registry.register(Registry.ITEM, CastleWars.id("launch_pad"), new FakeBlockItem(LAUNCH_PAD_BLOCK, Blocks.BONE_BLOCK, new Item.Settings()));
    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, CastleWars.id("launch_pad"), BlockEntityType.Builder.create(LaunchPadBlockEntity::new, LAUNCH_PAD_BLOCK).build(null));

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitializeServer() {
        GameType.register(
                id(ID),
                CastleWarsWaiting::open,
                CastleWarsConfig.CODEC
        );
    }
}
