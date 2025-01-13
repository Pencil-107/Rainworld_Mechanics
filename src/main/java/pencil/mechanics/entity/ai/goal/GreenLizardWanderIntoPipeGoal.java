package pencil.mechanics.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.block.pipes.TransportManager;
import pencil.mechanics.init.BlockInit;

import java.util.EnumSet;

import static pencil.mechanics.block.pipes.PipeEntrance.CONNECTION;

public class GreenLizardWanderIntoPipeGoal extends Goal {

    protected final PathAwareEntity creature;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    private final int searchRange;
    private final float moveSpeed;
    private final float cooldown = 100;
    private float timer = 100;
    private boolean enterable = true;

    Vec3d vec3d = null;

    protected Block targetBlock = BlockInit.PIPE_ENTRANCE;
    protected BlockPos closestPos = null;
    protected BlockPos checkPos;

    public GreenLizardWanderIntoPipeGoal(PathAwareEntity mob, float speed, int range) {
        super();
        creature = mob;
        searchRange = range;
        moveSpeed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        if (!enterable && timer > 0) {
            timer--;
        } else if (!enterable && timer <= 0) {
            enterable = true;
            System.out.println("Pipe Cooldown ended");
            return false;
        }
        if (vec3d == null && enterable == true) {
            vec3d = this.getPipeTarget();
            return false;
        } else if (enterable) {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            start();
            enterable = false;
            timer = cooldown;
            vec3d = null;
            return true;
        } else {
            return false;
        }
    }

    public void start() {
        creature.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, 1);
        if (new Vec3d(targetX, targetY, targetZ).isInRange(creature.getPos(), 1.75   )) {
            TransportManager.startTransport(creature, closestPos, creature.getWorld().getBlockState(closestPos).get(CONNECTION).getDirection());
            System.out.println("Creature entered pipe");
            creature.getNavigation().stop();
        }
    }

    @Nullable
    protected Vec3d getPipeTarget() {
        for (int x = creature.getBlockX()-searchRange; x < creature.getBlockX()+searchRange; x++)
        {
            for (int y = creature.getBlockY()-searchRange; y < creature.getBlockY()+searchRange; y++)
            {
                for (int z = creature.getBlockZ()-searchRange; z < creature.getBlockZ()+searchRange; z++) {
                    checkPos = new BlockPos(x, y, z);
                    if (creature.getWorld().getBlockState(checkPos).getBlock() == targetBlock) {

                        // check if it is closer than any previously found position
                        if (closestPos == null || creature.squaredDistanceTo(creature.getX() - closestPos.getX(),
                                creature.getY() - closestPos.getY(),
                                creature.getZ() - closestPos.getZ()) < creature.squaredDistanceTo(creature.getX() - closestPos.getX(),
                                creature.getY() - closestPos.getY(),
                                creature.getZ() - closestPos.getZ())) {
                            closestPos = checkPos;
                        }
                    }
                }
            }
        }
        if (closestPos != null) {
            return new Vec3d(closestPos.getX(), closestPos.getY(), closestPos.getZ());
        } else {
            return null;
        }
    }
}
