package me.hydos.castlewars.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

	@Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
	private void fuckOffYouStupidFuckingFlyCheck(Text reason, CallbackInfo ci) {
		if(reason.getString().equals("Flying is not enabled on this server")) {
			ci.cancel();
			//Fuck you mojang
		}
	}

}
