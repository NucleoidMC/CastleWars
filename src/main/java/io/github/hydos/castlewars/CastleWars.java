package io.github.hydos.castlewars;

import io.github.hydos.castlewars.game.CastleWarsWaiting;
import io.github.hydos.castlewars.game.block.LaunchPadBlock;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;


public class CastleWars implements DedicatedServerModInitializer {

    public static final String ID = "castlewars";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<CastleWarsConfig> TYPE = GameType.register(
            new Identifier(CastleWars.ID, "castlewars"),
            CastleWarsWaiting::open,
            CastleWarsConfig.CODEC
    );

    public static final boolean DEBUGGING = false;

    public static final LaunchPadBlock LAUNCH_PAD_BLOCK = Registry.register(Registry.BLOCK, new Identifier(ID, "launch_pad_block"), new LaunchPadBlock(AbstractBlock.Settings.of(Material.METAL)));

    @Override
    public void onInitializeServer() {
    }
}
