package pencil.mechanics.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.logging.Level;

public class GreenLizard extends PathAwareEntity implements GeoEntity {

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.attack");
    protected static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.death");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public GreenLizard(EntityType<? extends GreenLizard> type, World level) {
        super(type, level);
    }

    @Override
    public AttributeContainer getAttributes() {
        return new AttributeContainer(createMobAttributes().build());
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Animating", 5, this::AnimController));
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
        attackLivingEntity(player);
    }

    protected <E extends GreenLizard> PlayState AnimController(final AnimationState<E> event) {
        if (event.isMoving()) {
            return event.setAndContinue(WALK_ANIM);
        }
        if (isDead()) {
            return event.setAndContinue(DEATH_ANIM);
        }
        if (isAttacking()) {
            return event.setAndContinue(ATTACK_ANIM);
        }

        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

}
