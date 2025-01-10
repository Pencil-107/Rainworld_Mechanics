package pencil.mechanics.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private static final double RAIN_WORLD_GRAVITY = 0.08 * (42.8 / 32); // Adjusted Rain World gravity per tick in Minecraft
    private static final float BASE_SLUGCAT_JUMP_MULTIPLIER = 1.3F; // Adjusted jump multiplier for base Slugcat

    //TODO: method to change gravity
    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private double modifyGravity(double originalGravity) {
        return RAIN_WORLD_GRAVITY;
    }
}
