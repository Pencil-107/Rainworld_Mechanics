package pencil.mechanics.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PoleplantEntity extends PathAwareEntity implements GeoEntity {

    protected static final RawAnimation ACTIVE_ANIM = RawAnimation.begin().thenLoop("animation.model.idle_active");
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.model.idle_inactive");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public static boolean status = true;

    public PoleplantEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public AttributeContainer getAttributes() {
        return new AttributeContainer(createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 10)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .build());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Active", 5, this::activeAnimController));
    }

    protected <E extends PoleplantEntity> PlayState activeAnimController(final AnimationState<E> event) {
        if (status) {
            return event.setAndContinue(ACTIVE_ANIM);
        }

        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        status = !status;
        return super.interactAt(player, hitPos, hand);
    }
}
