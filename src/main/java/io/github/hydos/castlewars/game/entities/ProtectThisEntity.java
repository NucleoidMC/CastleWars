package io.github.hydos.castlewars.game.entities;

import io.github.hydos.castlewars.game.PlayerManager;
import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import net.gegy1000.plasmid.game.GameWorld;
import net.gegy1000.plasmid.game.player.GameTeam;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class ProtectThisEntity extends VillagerEntity {

    public final GameTeam team;
    private final CastleWarsGame game;

    public ProtectThisEntity(World world, GameTeam team, CastleWarsGame game) {
        super(EntityType.VILLAGER, world);
        this.team = team;
        this.game = game;

        this.setCustomName(new LiteralText("Health ???/???"));

        this.setAiDisabled(true);
        this.setInvulnerable(true);
        this.setCustomNameVisible(true);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        PlayerManager.getInstance().teams.get(team).eliminated = true;
        checkForGameEnd(game.gameWorld);
    }

    public static void checkForGameEnd(GameWorld gameWorld) {
        if(PlayerManager.getInstance().teams().filter(teamState -> !teamState.eliminated).toArray().length != PlayerManager.getInstance().teams.size()){
            //someone has been eliminated. figure out who hasnt
            PlayerManager.getInstance().teams().filter(teamState -> !teamState.eliminated).forEach(teamState -> {
                for(ServerPlayerEntity playerEntity : PlayerManager.getInstance().participants.keySet()){
                    playerEntity.sendMessage(new LiteralText(teamState.team.getDisplay() + " Has won the game!").formatted(Formatting.GOLD, Formatting.BOLD), false);
                    gameWorld.closeGame();
                    gameWorld.closeWorld();
                }
            });

        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_BEACON_AMBIENT;
    }
}
