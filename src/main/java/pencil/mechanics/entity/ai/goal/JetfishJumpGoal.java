package pencil.mechanics.entity.ai.goal;

import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import pencil.mechanics.entity.JetfishEntity;

public class JetfishJumpGoal extends DiveJumpingGoal {
    private static final int[] OFFSET_MULTIPLIERS = new int[]{0, 1, 4, 5, 6, 7};
    private JetfishEntity jetfish;
    private int chance;
    private boolean inWater;

    public JetfishJumpGoal(JetfishEntity jetfish, int chance) {
        this.jetfish = jetfish;
        this.chance = toGoalTicks(chance);
    }

    public boolean canStart() {
        if (this.jetfish.getRandom().nextInt(this.chance) != 0) {
            return false;
        } else {
            Direction direction = this.jetfish.getMovementDirection();
            int i = direction.getOffsetX();
            int j = direction.getOffsetZ();
            BlockPos blockPos = this.jetfish.getBlockPos();

            for(int k : OFFSET_MULTIPLIERS) {
                if (!this.isWater(blockPos, i, j, k) || !this.isAirAbove(blockPos, i, j, k)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean isWater(BlockPos pos, int offsetX, int offsetZ, int multiplier) {
        BlockPos blockPos = pos.add(offsetX * multiplier, 0, offsetZ * multiplier);
        return this.jetfish.getWorld().getFluidState(blockPos).isIn(FluidTags.WATER) && !this.jetfish.getWorld().getBlockState(blockPos).blocksMovement();
    }

    private boolean isAirAbove(BlockPos pos, int offsetX, int offsetZ, int multiplier) {
        return this.jetfish.getWorld().getBlockState(pos.add(offsetX * multiplier, 1, offsetZ * multiplier)).isAir() && this.jetfish.getWorld().getBlockState(pos.add(offsetX * multiplier, 2, offsetZ * multiplier)).isAir();
    }

    public boolean shouldContinue() {
        double d = this.jetfish.getVelocity().y;
        return (!(d * d < (double)0.03F) || this.jetfish.getPitch() == 0.0F || !(Math.abs(this.jetfish.getPitch()) < 10.0F) || !this.jetfish.isTouchingWater()) && !this.jetfish.isOnGround();
    }

    public boolean canStop() {
        return false;
    }

    public void start() {
        Direction direction = this.jetfish.getMovementDirection();
        this.jetfish.setVelocity(this.jetfish.getVelocity().add((double)direction.getOffsetX() * 0.6, 0.7, (double)direction.getOffsetZ() * 0.6));
        this.jetfish.getNavigation().stop();
    }

    public void stop() {
        this.jetfish.setPitch(0.0F);
    }

    public void tick() {
        boolean bl = this.inWater;
        if (!bl) {
            FluidState fluidState = this.jetfish.getWorld().getFluidState(this.jetfish.getBlockPos());
            this.inWater = fluidState.isIn(FluidTags.WATER);
        }

        if (this.inWater && !bl) {
            this.jetfish.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        }

        Vec3d vec3d = this.jetfish.getVelocity();
        if (vec3d.y * vec3d.y < (double)0.03F && this.jetfish.getPitch() != 0.0F) {
            this.jetfish.setPitch(MathHelper.lerpAngleDegrees(0.2F, this.jetfish.getPitch(), 0.0F));
        } else if (vec3d.length() > (double)1.0E-5F) {
            double d = vec3d.horizontalLength();
            double e = Math.atan2(-vec3d.y, d) * (double)(180F / (float)Math.PI);
            this.jetfish.setPitch((float)e);
        }

    }
}
