package pencil.mechanics.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.entity.ai.goal.CustomAttackGoal;
import pencil.mechanics.entity.ai.goal.WanderIntoPipeGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FivePebblesEntity extends PathAwareEntity implements GeoEntity {

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("float");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FivePebblesEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 10));

        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public AttributeContainer getAttributes() {
        return new AttributeContainer(createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed)
                .build());
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::idleAnimController));
    }

    protected <E extends FivePebblesEntity> PlayState idleAnimController(final AnimationState<E> event) {
        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
