package io.github.hydos.castlewars.game.custom.entities;

import io.github.hydos.castlewars.game.PlayerManager;
import io.github.hydos.castlewars.game.custom.ItemShop;
import io.github.hydos.castlewars.game.ingame.CastleWarsGame;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.player.GameTeam;

public class ProtectThisEntity extends VillagerEntity {

    public final GameTeam team;
    private final CastleWarsGame game;

    public ProtectThisEntity(World world, GameTeam team, CastleWarsGame game) {
        super(EntityType.VILLAGER, world);
        this.team = team;
        this.game = game;

        this.setCustomName(new LiteralText("Protecc This Entity").formatted(Formatting.YELLOW, Formatting.BOLD));

        this.setAiDisabled(true);
        this.setInvulnerable(true);
        this.setCustomNameVisible(true);
    }

    public static void checkForGameEnd(GameWorld gameWorld) {
        if (PlayerManager.getInstance().teams().filter(teamState -> !teamState.eliminated).toArray().length != PlayerManager.getInstance().teams.size()) {
            //someone has been eliminated. figure out who hasnt
            PlayerManager.getInstance().teams().filter(teamState -> !teamState.eliminated).forEach(teamState -> {
                for (ServerPlayerEntity playerEntity : PlayerManager.getInstance().participants.keySet()) {
                    playerEntity.sendMessage(new LiteralText(teamState.team.getDisplay() + " Has won the game!").formatted(Formatting.GOLD, Formatting.BOLD), false);
                    gameWorld.close();
                }
            });

        }
    }

    private static void updateBossBar(ServerBossBar bar) {
        for (ServerPlayerEntity player : PlayerManager.getInstance().participants.keySet()) {
            player.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.UPDATE_PCT, bar));
        }
    }

    @Override
    public void tick() {
        if (game.closed) {
            kill();
        }
        if (game.killPhase) {
            this.setInvulnerable(false);
        }
        super.tick();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        player.openHandledScreen(ItemShop.create());
        return ActionResult.SUCCESS;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (game.closed) {
            super.onDeath(source);
            return;
        }
        PlayerManager.getInstance().teams.get(team).eliminated = true;
        checkForGameEnd(game.gameWorld);
    }

    @Override
    public void applyDamage(DamageSource source, float amount) {
        if (team.getDisplay().equals("Blue")) {
            game.map.blueTeam.setPercent(game.map.blueTeam.getPercent() - (amount / 20));
            updateBossBar(game.map.blueTeam);
        } else {
            game.map.redTeam.setPercent(game.map.redTeam.getPercent() - (amount / 20));
            updateBossBar(game.map.redTeam);
        }
        super.applyDamage(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_BEACON_AMBIENT;
    }
}
