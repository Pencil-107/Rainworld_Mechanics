package pencil.mechanics.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pencil.mechanics.RainworldMechanicsClient;

@Mixin(Entity.class)
public abstract class RainworldMechanicsEntityMixin {

    @Shadow public abstract void requestTeleport(double destX, double destY, double destZ);

    @Shadow public abstract void setSwimming(boolean swimming);

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (RainworldMechanicsClient.crawling) {
            RainworldMechanicsClient.clientPlayer.player.setBoundingBox(Box.of(RainworldMechanicsClient.clientPlayer.player.getBoundingBox().getCenter(), 0.6f, 0.6f, 0.6f));
        }
    }

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    private void isSwimming(CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
    }
}
