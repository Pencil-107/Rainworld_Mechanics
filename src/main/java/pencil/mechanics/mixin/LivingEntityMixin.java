package pencil.mechanics.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    private static final double RAIN_WORLD_GRAVITY = 0.08 * (42.8 / 32); // Adjusted Rain World gravity per tick in Minecraft
    private static final float BASE_SLUGCAT_JUMP_MULTIPLIER = 1.3F; // Adjusted jump multiplier for base Slugcat



    //TODO: method to change gravity
    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private double modifyGravity(double originalGravity) {
        return RAIN_WORLD_GRAVITY;
    }

    // Change the visibility of this method to public
    public float getJumpVelocity() {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Use the invoker from EntityMixin
        float jumpVelocityMultiplier = ((EntityMixin) entity).invokeGetJumpVelocityMultiplier();
        float baseJumpVelocity = 0.42F * jumpVelocityMultiplier + this.getJumpBoostVelocityModifier();

        return baseJumpVelocity * BASE_SLUGCAT_JUMP_MULTIPLIER;
    }

    // Helper method for Jump Boost velocity modifier
    public float getJumpBoostVelocityModifier() {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.hasStatusEffect(StatusEffects.JUMP_BOOST) ? 0.1F * ((float) entity.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1.0F) : 0.0F;
    }
}
