package pencil.mechanics.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.player.movement.Crawling;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void onHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (RainworldMechanicsClient.playerEntity != null) {
            World world = RainworldMechanicsClient.playerEntity.getWorld();

            if (world.isClient()) {
                // Send velocity packet to the server
                RainworldMechanicsClient.sendFallVelocityPacket(RainworldMechanicsClient.playerEntity);
            }


            // Prevent further fall damage
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
