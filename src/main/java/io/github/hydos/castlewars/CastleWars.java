package io.github.hydos.castlewars;

import io.github.hydos.castlewars.game.CastleWarsLobby;
import io.github.hydos.castlewars.game.config.CastleWarsConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.gegy1000.plasmid.game.GameType;
import net.minecraft.util.Identifier;


public class CastleWars implements DedicatedServerModInitializer {

    public static final String ID = "castlewars";

    public static final GameType<CastleWarsConfig> TYPE = GameType.register(
            new Identifier(CastleWars.ID, "castlewars"),
            CastleWarsLobby::open,
            CastleWarsConfig.CODEC
    );

    @Override
    public void onInitializeServer() {

    }
}
