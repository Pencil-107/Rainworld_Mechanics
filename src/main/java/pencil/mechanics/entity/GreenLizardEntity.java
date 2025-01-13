package pencil.mechanics.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LongJumpTask;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelectors;
import pencil.mechanics.block.pipes.PipeEntrance;
import pencil.mechanics.entity.ai.goal.GreenLizardAttackGoal;
import pencil.mechanics.entity.ai.goal.WanderIntoPipeGoal;
import pencil.mechanics.init.BlockInit;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import pencil.mechanics.block.pipes.PipeEntrance;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GreenLizardEntity extends HostileEntity implements GeoEntity {

    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.walk");
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("green_lizard.animation.idle");
    private static boolean visibleTarget = false;
    private static BlockPos lastTargetPos = null;
    public int color = 0x24c814;

    private static final float movementTimer = 20;
    private static final float movementReset = 20;
    private static float timer = movementTimer;
    private static float speed = 0.4F;
    private static boolean moving = true;

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
        this.goalSelector.add(2, new GreenLizardAttackGoal(this, 0.7, false));
        this.goalSelector.add(3, new WanderIntoPipeGoal(this, 0.3F, 5));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1D));

        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, GreenLizardEntity.class, true));
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
    public void tick() {
        if (this.navigation.isFollowingPath()) {
            if (moving) {
                if (timer > 0) {
                    timer--;
                } else {
                    this.setMovementSpeed(0F);
                    System.out.println("slowed");
                    moving = false;
                    timer = movementReset;
                    return;
                }
            } else {
                if (timer > 0) {
                    timer--;
                } else {
                    this.setMovementSpeed(speed);
                    moving = true;
                    timer = movementTimer;
                    return;
                }
            }
        }

        super.tick();
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
