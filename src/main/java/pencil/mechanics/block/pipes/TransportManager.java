package pencil.mechanics.block.pipes;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import pencil.mechanics.RainworldMechanics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransportManager {
    private static final Map<Entity, TransportData> transports = new HashMap<>();

    public static void startTransport(Entity player, BlockPos startPos, Direction initialDirection) {
        if (player instanceof ServerPlayerEntity) {
            transports.put(player, new TransportData(startPos, initialDirection, 2, 0x1f1f1f));
            player.setInvulnerable(true);
            player.setInvisible(true);
        } else {
            transports.put(player, new TransportData(startPos, initialDirection, 2, player.getTeamColorValue()));
            player.setInvulnerable(true);
            player.setInvisible(true);
        }
    }

    public static void initialize() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            transports.forEach((player, data) -> {
                if (data.ticksLeft > 0) {
                    data.ticksLeft--;
                } else {
                    movePlayer(player, data);
                    data.ticksLeft = 1; // Reset the tick counter
                }
                // Override the player's position to prevent them from moving
                if (data.direction.equals(Direction.UP) || data.direction.equals(Direction.DOWN)) {
                    player.teleport(player.getWorld().getServer().getWorld(World.OVERWORLD), data.currentPos.getX() + 0.5, data.currentPos.getY(), data.currentPos.getZ() + 0.5, PositionFlag.getFlags(0), 0, data.direction.asRotation()*-1);
                } else {
                    player.teleport(player.getWorld().getServer().getWorld(World.OVERWORLD), data.currentPos.getX() + 0.5, data.currentPos.getY(), data.currentPos.getZ() + 0.5, PositionFlag.getFlags(0), data.direction.asRotation(), 0);
                }
                player.setPose(EntityPose.SWIMMING);
                player.setHeadYaw(data.direction.asRotation());
            });
            transports.entrySet().removeIf(entry -> entry.getValue().isCompleted);
        });
    }

    private static void movePlayer(Entity player, TransportData data) {
        World world = player.getWorld();
        BlockPos nextPos = data.currentPos.offset(data.direction);
        BlockState nextState = world.getBlockState(nextPos);
        Block nextBlock = nextState.getBlock();

        if (nextBlock instanceof TelePipeBlock && world.getBlockEntity(nextPos) instanceof TelePipeBlockEntity) {
            // Find the nearest non-TelePipe block
            BlockPos teleportPos = null;
            if (((TelePipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(nextPos))).linkedPos != null) {
                teleportPos = ((TelePipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(nextPos))).linkedPos;
            }
            if (teleportPos != null) {
                data.currentPos = teleportPos;
                data.direction = getNextDirection(world, teleportPos, data.direction);
                player.setHeadYaw(data.direction.asRotation());
                // Do not mark as completed; let transport continue
            }
        } else if (nextBlock instanceof PipeBlock) {
            data.currentPos = nextPos;
            data.direction = getNextDirection(world, nextPos, data.direction);
            world.playSound(null, player.getBlockPos(), RainworldMechanics.PIPE_LOOP_EVENT, SoundCategory.BLOCKS, 1f, 1f);
            player.setHeadYaw(data.direction.asRotation());
            if (player instanceof PathAwareEntity) {
                ((PipeBlock) nextBlock).setColor(player.getTeamColorValue());
            }
            ((PipeBlock) nextBlock).switchLitState(world, nextPos, nextState, 5); // Set the lit state for 5 ticks
        } else if (nextBlock instanceof PipeEntrance) {
            data.currentPos = nextPos;
            player.setInvulnerable(false);
            player.setInvisible(false);
            data.isCompleted = true; // End the transport
        } else {
            player.setInvulnerable(false);
            player.setInvisible(false);
            data.isCompleted = true; // End the transport if there's no valid pipe
        }

        if (data.isCompleted) {
            player.setInvulnerable(false);
            player.setInvisible(false);
            if (!(player instanceof ServerPlayerEntity)) {
                player.addVelocity(new Vec3d(player.getRotationVector().getX() * 1, 0, player.getRotationVector().getZ() * 1));
            }
        }
    }

    private static BlockPos findNearestNonTelePipe(World world, BlockPos startPos, Direction initialDirection) {
        BlockPos currentPos = startPos;
        Direction direction = initialDirection;

        while (true) {
            currentPos = currentPos.offset(direction);
            BlockState state = world.getBlockState(currentPos);
            Block block = state.getBlock();

            if (block instanceof TelePipeBlock) {
                direction = getNextDirection(world, currentPos, direction);
            } else if (block instanceof PipeBlock || block instanceof PipeEntrance) {
                return currentPos;
            } else {
                return null; // No valid position found
            }
        }
    }

    private static Direction getNextDirection(World world, BlockPos pos, Direction currentDirection) {
        int connections = 0;
        Direction nextDirection = currentDirection;
        Direction[] directions = Direction.values();

        for (Direction direction : directions) {
            if (direction != currentDirection.getOpposite() || world.getBlockState(pos).getBlock() == RainworldMechanics.TELEPIPE_BLOCK) {
                BlockState neighborState = world.getBlockState(pos.offset(direction));
                Block neighborBlock = neighborState.getBlock();
                if (neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance || neighborBlock instanceof TelePipeBlock) {
                    connections++;
                    if (connections == 1 || connections == 2) {
                        nextDirection = direction;
                    }
                }
            }
        }

        // If more than 2 connections, prioritize straight movement
        if (connections > 2) {
            nextDirection = currentDirection;
        }

        return nextDirection;
    }

    private static class TransportData {
        BlockPos currentPos;
        Direction direction;
        int ticksLeft;
        boolean isCompleted;
        int color;

        TransportData(BlockPos startPos, Direction direction, int ticksLeft, int entityColor) {
            this.currentPos = startPos;
            this.direction = direction;
            this.ticksLeft = ticksLeft;
            this.isCompleted = false;
            this.color = entityColor;
        }
    }
}
