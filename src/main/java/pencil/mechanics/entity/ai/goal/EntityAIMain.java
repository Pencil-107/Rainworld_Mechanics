package pencil.mechanics.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import pencil.mechanics.init.BlockInit;

import java.util.HashMap;
import java.util.List;

public abstract class EntityAIMain {
    private PathAwareEntity entity = null;
    private BlockPos targetPos = null;

    private final int searchRange = 6;
    private final float moveSpeed;
    private final float cooldown = 20;
    private float timer = cooldown;
    private final List targetBlocks = List.of(BlockInit.PIPE_ENTRANCE, BlockInit.POLE_JOINT, BlockInit.POLE_X, BlockInit.POLE_Y, BlockInit.POLE_Z);
    HashMap<BlockPos, String> foundBlocks = new HashMap<>();
    private List foundPos = List.of();
    private BlockPos closestPos = null;
    private BlockPos checkPos;

    public EntityAIMain(PathAwareEntity creature, BlockPos targetBlockPos, float speed) {
        entity = creature;
        targetPos = targetBlockPos;
        moveSpeed = speed;
    }

    public void main() {
        for (float clock = cooldown; clock >= 0; clock--) {
            if (clock == 0) {
                calculateGrid();
            }
        }
    }

    public void calculateGrid() {
        foundPos.clear();
        foundBlocks.clear();
        findBlocks();
    }
    
    public void findBlocks() {
        for (int x = entity.getBlockX()-searchRange; x < entity.getBlockX()+searchRange; x++)
        {
            for (int y = entity.getBlockY()-searchRange; y < entity.getBlockY()+searchRange; y++)
            {
                for (int z = entity.getBlockZ()-searchRange; z < entity.getBlockZ()+searchRange; z++) {
                    checkPos = new BlockPos(x, y, z);
                    Block foundBlock = entity.getWorld().getBlockState(checkPos).getBlock();
                    if (targetBlocks.contains(foundBlock)) {

                        if (foundBlock == BlockInit.PIPE_ENTRANCE) {
                            foundBlocks.put(checkPos, "PipeEntrance");
                        }
                        if (foundBlock == BlockInit.POLE_JOINT || foundBlock == BlockInit.POLE_Y || foundBlock == BlockInit.POLE_X || foundBlock == BlockInit.POLE_Z) {
                            foundBlocks.put(checkPos, "Pole");
                        }

                        foundPos.add(checkPos);
                    }
                }
            }
        }
    }
}
