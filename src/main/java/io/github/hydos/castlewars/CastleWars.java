package io.github.hydos.castlewars;

import io.github.hydos.castlewars.game.CastleWarsWaiting;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import io.github.hydos.castlewars.game.custom.block.LaunchPadBlock;
import io.github.hydos.castlewars.game.custom.block.entity.LaunchPadBlockEntity;
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

    public static final String ID = "castlewars";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final boolean DEBUGGING = false;

    public static final LaunchPadBlock LAUNCH_PAD_BLOCK = Registry.register(Registry.BLOCK, new Identifier(ID, "launch_pad"), new LaunchPadBlock(AbstractBlock.Settings.of(Material.METAL)));
    public static final Item LAUNCH_PAD_BLOCK_ITEM = Registry.register(Registry.ITEM, new Identifier(ID, "launch_pad"), new FakeBlockItem(LAUNCH_PAD_BLOCK, Blocks.SMOOTH_STONE_SLAB, new Item.Settings()));
    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ID, "launch_pad"), BlockEntityType.Builder.create(LaunchPadBlockEntity::new, LAUNCH_PAD_BLOCK).build(null));

    @Override
    public void onInitializeServer() {
        GameType.register(
                new Identifier(CastleWars.ID, ID),
                CastleWarsWaiting::open,
                CastleWarsConfig.CODEC
        );
    }
}
