package io.github.hydos.castlewars.game;

import io.github.hydos.castlewars.game.map.CastleWarsMap;
import net.gegy1000.plasmid.game.GameWorld;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class PlayerManager {

    GameWorld gameWorld;
    CastleWarsMap map;

    public PlayerManager(GameWorld gameWorld, CastleWarsMap map) {
        this.gameWorld = gameWorld;
        this.map = map;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gamemode) {
        player.setGameMode(gamemode);
        map.spawnPlayerIntoLobby(player, gameWorld.getWorld());
    }

    public void playerJoinGame(ServerPlayerEntity player) {
        player.inventory.clear();
        player.inventory.main.add(0, new ItemStack(Blocks.RED_WOOL));
        player.inventory.main.add(1, new ItemStack(Blocks.BLUE_WOOL));
        resetPlayer(player, GameMode.ADVENTURE);
    }
}
