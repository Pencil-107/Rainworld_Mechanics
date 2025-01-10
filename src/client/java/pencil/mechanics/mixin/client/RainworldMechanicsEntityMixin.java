package pencil.mechanics.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pencil.mechanics.RainworldMechanicsClient;

import java.util.Objects;

@Mixin(Entity.class)
public abstract class RainworldMechanicsEntityMixin {

    @Shadow public abstract boolean isCrawling();

    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

    @Shadow public abstract void setSneaking(boolean sneaking);

    @Shadow public abstract boolean isPlayer();

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (RainworldMechanicsClient.crawling) {
            RainworldMechanicsClient.clientPlayer.player.setBoundingBox(Box.of(RainworldMechanicsClient.clientPlayer.player.getBoundingBox().getCenter(), 0.6f, 0.6f, 0.6f));
        }
    }


    @Inject(method = "isCrawling", at = @At("RETURN"), cancellable = true)
    private void isCrawling(CallbackInfoReturnable<Boolean> cir) {
        if (RainworldMechanicsClient.crawling && RainworldMechanicsClient.crawlFrame) {
            cir.setReturnValue(false);
        } else if (RainworldMechanicsClient.crawling) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isInSwimmingPose", at = @At("RETURN"), cancellable = true)
    private void isInSwimmingPose(CallbackInfoReturnable<Boolean> cir) {
        if (RainworldMechanicsClient.crawling) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isSwimming", at = @At("RETURN"), cancellable = true)
    private void isSwimming(CallbackInfoReturnable<Boolean> cir) {
        if (RainworldMechanicsClient.crawling) {
            assert RainworldMechanicsClient.clientPlayer.player != null;
            if (RainworldMechanicsClient.clientPlayer.player.isSubmergedInWater()) {
                cir.setReturnValue(true);
            }
        }
    }
}
