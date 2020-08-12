package io.github.hydos.castlewars;

import io.github.hydos.castlewars.game.CastleWarsWaiting;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.util.Identifier;
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

    public static final boolean DEBUGGING = true;

    @Override
    public void onInitializeServer() {

    }
}
