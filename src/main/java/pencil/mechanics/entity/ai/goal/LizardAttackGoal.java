package pencil.mechanics.entity.ai.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundCategory;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.entity.GreenLizardEntity;

import java.util.EnumSet;

public class LizardAttackGoal extends MeleeAttackGoal {
    private final HostileEntity lizard;
    private int ticks;

    public LizardAttackGoal(HostileEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.lizard = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public void start() {
        super.start();
        lizard.getWorld().playSound(null, lizard.getBlockPos(), RainworldMechanics.GREEN_LIZARD_HISS_EVENT, SoundCategory.HOSTILE, 1, 1);
        this.ticks = 0;
    }

    public void stop() {
        super.stop();
        this.lizard.setAttacking(false);
    }

    public void tick() {
        super.tick();
        ++this.ticks;
        this.lizard.setAttacking(this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2);

    }
}
