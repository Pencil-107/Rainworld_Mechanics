package pencil.mechanics.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import pencil.mechanics.init.BlockInit;

import java.util.List;

public abstract class EntityAIMain {
    private PathAwareEntity entity = null;
    private BlockPos targetPos = null;

    private final int searchRange = 6;
    private final float moveSpeed;
    private final float cooldown = 200;
    private float timer = cooldown;
    private List targetBlocks = List.of(BlockInit.PIPE_ENTRANCE);
    private List foundPaths = List.of();
    private BlockPos closestPos = null;
    private BlockPos checkPos;

    public EntityAIMain(PathAwareEntity creature, BlockPos targetBlockPos, float speed) {
        entity = creature;
        targetPos = targetBlockPos;
        moveSpeed = speed;
    }
    
    public Vec3d findBlocks() {
        for (int x = entity.getBlockX()-searchRange; x < entity.getBlockX()+searchRange; x++)
        {
            for (int y = entity.getBlockY()-searchRange; y < entity.getBlockY()+searchRange; y++)
            {
                for (int z = entity.getBlockZ()-searchRange; z < entity.getBlockZ()+searchRange; z++) {
                    checkPos = new BlockPos(x, y, z);
                    Block foundBlock = entity.getWorld().getBlockState(checkPos).getBlock();
                    if (targetBlocks.contains(foundBlock)) {

                        if (foundBlock == BlockInit.PIPE_ENTRANCE) {
                            foundPaths.add(List.of(checkPos, foundBlock, "exit position"));
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
