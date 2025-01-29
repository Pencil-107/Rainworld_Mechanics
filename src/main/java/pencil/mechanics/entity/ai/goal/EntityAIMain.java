package pencil.mechanics.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

public abstract class EntityAIMain {
    private PathAwareEntity entity = null;
    private BlockPos targetPos = null;

    public EntityAIMain(PathAwareEntity creature, BlockPos targetBlockPos) {
        entity = creature;
        targetPos = targetBlockPos;
    }
}
