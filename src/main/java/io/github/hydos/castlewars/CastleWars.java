package io.github.hydos.castlewars;

import io.github.hydos.castlewars.game.CastleWarsWaiting;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.gegy1000.plasmid.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CastleWars implements DedicatedServerModInitializer {

    public static final String ID = "castlewars";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<CastleWarsConfig> TYPE = GameType.register(
            new Identifier(CastleWars.ID, "castlewars"),
            CastleWarsWaiting::open,
            CastleWarsConfig.CODEC
    );

    @Override
    public void onInitializeServer() {

    }
}
