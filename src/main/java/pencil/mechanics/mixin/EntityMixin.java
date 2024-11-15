package pencil.mechanics.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityMixin {
    @Invoker("getJumpVelocityMultiplier")
    float invokeGetJumpVelocityMultiplier();
}
