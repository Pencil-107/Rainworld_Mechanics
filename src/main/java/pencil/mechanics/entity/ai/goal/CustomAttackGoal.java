package pencil.mechanics.entity.ai.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import pencil.mechanics.RainworldMechanics;

import java.util.EnumSet;

public class CustomAttackGoal extends MeleeAttackGoal {
    private final HostileEntity creature;
    private int ticks;
    private final SoundEvent hiss;

    public CustomAttackGoal(HostileEntity mob, double speed, SoundEvent attackSound, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.creature = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.hiss = attackSound;
    }

    public void start() {
        super.start();
        if (hiss != null) {
            creature.getWorld().playSound(null, creature.getBlockPos(), hiss, SoundCategory.HOSTILE, 1, 1);
        }
        this.ticks = 0;
    }

    public void stop() {
        super.stop();
        this.creature.setAttacking(false);
    }

    public void tick() {
        super.tick();
        ++this.ticks;
        this.creature.setAttacking(this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2);

    }
}
