package pencil.mechanics.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.entity.ai.goal.JetfishJumpGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class JetfishEntity extends WaterCreatureEntity implements GeoEntity {

    protected static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("Swim");
    static final TargetPredicate CLOSE_PLAYER_PREDICATE;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public JetfishEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::idleAnimController));
    }

    protected <E extends JetfishEntity> PlayState idleAnimController(final AnimationState<E> event) {
        return event.setAndContinue(SWIM_ANIM);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(2, new SwimWithPlayerGoal(this, (double)4.0F));
        this.goalSelector.add(4, new SwimAroundGoal(this, (double)1.0F, 10));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(5, new JetfishJumpGoal(this, 10));
    }

    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.3F;
    }

    public int getMaxLookPitchChange() {
        return 1;
    }

    public int getMaxHeadRotation() {
        return 1;
    }

    @Override
    public boolean shouldDismountUnderwater() {
        return false;
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (!this.hasPassengers()) {
            player.startRiding(this);
        }
        return super.interactAt(player, hitPos, hand);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity var2 = this.getFirstPassenger();
        if (var2 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var2;
            return playerEntity;
        }

        return null;
    }

    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        super.tickControlled(controllingPlayer, movementInput);
        this.setRotation(controllingPlayer.getHeadYaw(), controllingPlayer.getPitch());
        if (this.submergedInWater) {
            this.setVelocity(controllingPlayer.getRotationVector().getX() * .5, controllingPlayer.getRotationVector().getY() * .5, controllingPlayer.getRotationVector().getZ() * .5);
        }
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.1;
    }

    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        return new Vec3d((double)0, (double)0, (double)0);
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return true;
    }

    public void tick() {
        super.tick();
        if (this.isAiDisabled()) {
            this.setAir(this.getMaxAir());
        } else {
            if (this.getWorld().isClient && this.isTouchingWater() && this.getVelocity().lengthSquared() > 0.03) {
                Vec3d vec3d = this.getRotationVec(0.0F);
                float f = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180F)) * 0.3F;
                float g = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180F)) * 0.3F;
                float h = 1.2F - this.random.nextFloat() * 0.7F;

                for(int i = 0; i < 2; ++i) {
                    this.getWorld().addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3d.x * (double)h + (double)f, this.getY() - vec3d.y, this.getZ() - vec3d.z * (double)h + (double)g, (double)0.0F, (double)0.0F, (double)0.0F);
                    this.getWorld().addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3d.x * (double)h - (double)f, this.getY() - vec3d.y, this.getZ() - vec3d.z * (double)h - (double)g, (double)0.0F, (double)0.0F, (double)0.0F);
                }
            }

        }
    }

    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public AttributeContainer getAttributes() {
        return new AttributeContainer(createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2)
                .build());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    static {
        CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance((double)10.0F).ignoreVisibility();
    }

    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_DOLPHIN_SPLASH;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DOLPHIN_SWIM;
    }

    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add((double)0.0F, -0.005, (double)0.0F));
            }
        } else {
            super.travel(movementInput);
        }

    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    static class SwimWithPlayerGoal extends Goal {
        private final JetfishEntity jetfish;
        private final double speed;
        @Nullable
        private PlayerEntity closestPlayer;

        SwimWithPlayerGoal(JetfishEntity jetfish, double speed) {
            this.jetfish = jetfish;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        public boolean canStart() {
            this.closestPlayer = this.jetfish.getWorld().getClosestPlayer(JetfishEntity.CLOSE_PLAYER_PREDICATE, this.jetfish);
            if (this.closestPlayer == null) {
                return false;
            } else {
                return this.closestPlayer.isSwimming() && this.jetfish.getTarget() != this.closestPlayer;
            }
        }

        public boolean shouldContinue() {
            return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.jetfish.squaredDistanceTo(this.closestPlayer) < (double)256.0F;
        }

        public void stop() {
            this.closestPlayer = null;
            this.jetfish.getNavigation().stop();
        }

        public void tick() {
            this.jetfish.getLookControl().lookAt(this.closestPlayer, (float)(this.jetfish.getMaxHeadRotation() + 20), (float)this.jetfish.getMaxLookPitchChange());
            if (this.jetfish.squaredDistanceTo(this.closestPlayer) < (double)6.25F) {
                this.jetfish.getNavigation().stop();
            } else {
                this.jetfish.getNavigation().startMovingTo(this.closestPlayer, this.speed);
            }
        }
    }
}
