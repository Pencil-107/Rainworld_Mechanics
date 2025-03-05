package pencil.mechanics.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
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

public class GreenLizardEntity extends HostileEntity implements GeoEntity {

    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.walk");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.attack");
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.idle");
    public int color = 0x24c814;

    private static BlockPos lastTargetPos = null;

    private static final float biteTimer = 20;
    private static float timer = biteTimer;
    private static float speed = 0.3F;
    private static final float health = 5;

    private static boolean biting = false;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public int getColor() {
        return color;
    }

    public GreenLizardEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public int getTeamColorValue() {
        return color;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new CustomAttackGoal(this, 0.7, RainworldMechanics.GREEN_LIZARD_HISS_EVENT, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1D));
        this.goalSelector.add(4, new WanderIntoPipeGoal(this, 0.3F, 3));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, GreenLizardEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PassiveEntity.class, true));
    }

    public void resetBiteAnim() {
        if (timer > 0) {
            timer--;
        } else {
            biting = false;
            timer = biteTimer;
        }
    }

    @Override
    public AttributeContainer getAttributes() {
        return new AttributeContainer(createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 10)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .build());
    }

    @Override
    public void tickMovement() {
        if (biting) {
            resetBiteAnim();
        }
        if (this.isAttacking() && this.getTarget() != null) {
            lastTargetPos = this.getTarget().getBlockPos();
        }
        if (!this.isAttacking() && lastTargetPos != null) {
            this.getNavigation().startMovingTo(lastTargetPos.getX(), lastTargetPos.getY(), lastTargetPos.getZ(), 1);
            lastTargetPos = null;
        }
        super.tickMovement();
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    protected <E extends GreenLizardEntity> PlayState walkAnimController(final AnimationState<E> event) {
        if (event.isMoving()) {
            return event.setAndContinue(WALK_ANIM);
        }

        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public void swingHand(Hand hand) {
        biting = true;
        this.getWorld().playSound(null, this.getBlockPos(), RainworldMechanics.BITE01_EVENT, SoundCategory.HOSTILE, 1, 1);
        super.swingHand(hand);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
