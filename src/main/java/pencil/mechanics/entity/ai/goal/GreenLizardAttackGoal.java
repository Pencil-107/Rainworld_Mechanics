package pencil.mechanics.entity.ai.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import pencil.mechanics.entity.GreenLizardEntity;

import java.util.EnumSet;

public class GreenLizardAttackGoal extends MeleeAttackGoal {
    private final GreenLizardEntity lizard;
    private int ticks;

    public GreenLizardAttackGoal(GreenLizardEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.lizard = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public void start() {
        super.start();
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
