package pencil.mechanics.block.pipes;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.init.BlockInit;

import java.util.Objects;

public class PipeBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    public static BlockPos location = null;
    public static World blockWorld = null;

    public static int litColor = 0xf0f0f0;

    public PipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
            .with(UP, false)
            .with(DOWN, false)
            .with(LIT, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, LIT);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // This method can be used for additional logic on placement if needed
        location = pos;
        blockWorld = world;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState();

        for (Direction direction : Direction.values()) {
            if (shouldConnect(ctx, direction)) {
                state = state.with(getProperty(direction), true);
            }

            if (ctx.getBlockPos() != null && ctx.getWorld() != null && ctx.getWorld().getBlockEntity(ctx.getBlockPos()) != null) {
                if (ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction)).getBlock() instanceof PipeEntrance) {
                    if (((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos()))).entrance1 != null) {
                        ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos()))).entrance2 = ctx.getBlockPos().offset(direction);
                    } else {
                        ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos()))).entrance1 = ctx.getBlockPos().offset(direction);
                    }
                } else if (ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction)).getBlock() instanceof PipeBlock) {
                    if (((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction)))).entrance1 != null &&
                            ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction)))).entrance2 != null) {
                        ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos()))).entrance1 = ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction)))).entrance1;
                        ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos()))).entrance2 = ((PipeBlockEntity) Objects.requireNonNull(ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction)))).entrance2;
                    }
                }
            }
        }

        return state;
    }

    private boolean shouldConnect(ItemPlacementContext ctx, Direction direction) {
        BlockState neighborState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction));
        Block neighborBlock = neighborState.getBlock();
        return neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Block neighborBlock = neighborState.getBlock();
        if (!(neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance)) {
            return state.with(getProperty(direction), false);
        }

        if (pos != null) {
            if (neighborBlock instanceof PipeEntrance) {
                if (((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).entrance1 != null) {
                    ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).entrance2 = neighborPos;
                } else {
                    ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).entrance1 = neighborPos;
                }
            } else if (neighborBlock instanceof PipeBlock) {
                ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).entrance1 = ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(neighborPos))).entrance1;
                ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).entrance2 = ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(neighborPos))).entrance2;
            }
        }

        return state.with(getProperty(direction), true);
    }

    public static BooleanProperty getProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player.isCreative()) {

            if (!player.getMainHandStack().isEmpty() && Block.getBlockFromItem(player.getMainHandStack().getItem()) != null && Block.getBlockFromItem(player.getMainHandStack().getItem()) != BlockInit.PIPE_BLOCK && Block.getBlockFromItem(player.getMainHandStack().getItem()) != BlockInit.PIPE_ENTRANCE) {
                if (world.getBlockEntity(pos) instanceof PipeBlockEntity) {
                    if (world.getBlockEntity(pos) != null) {
                        ((PipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).setBaseBlock(Block.getBlockFromItem(player.getMainHandStack().getItem()).getDefaultState());
                        Objects.requireNonNull(world.getBlockEntity(pos)).markDirty();
                        world.updateListeners(pos, state, state, 0);
                    }
                }
            }

            // Print the block's base block
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PipeBlockEntity) {
                BlockState baseBlock = ((PipeBlockEntity) blockEntity).getBaseBlock();
                System.out.println("Base Block: " + baseBlock.getBlock().toString());
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockEntityProvider.super.getTicker(world, state, type);
    }

    //@Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            world.setBlockState(pos, state.with(LIT, false));
            if (world.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity) {
                final int newColor = 0x1f1f1f;
                pipeBlockEntity.color = newColor;
                litColor = 0xf0f0f0;
                pipeBlockEntity.markDirty();
                world.updateListeners(pos, state, state, 0);
            }
        }
    }

    public void setColor(int newColor) {
        if (blockWorld != null && location != null && blockWorld.getBlockEntity(location) instanceof PipeBlockEntity pipeBlockEntity) {
            litColor = newColor;
        }
    }

    // Method to switch to lit state for a set amount of time
    public void switchLitState(World world, BlockPos pos, BlockState state, int ticks) {
        world.setBlockState(pos, state.with(LIT, true));
        world.scheduleBlockTick(pos, this, ticks);
        if (world.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity) {
            pipeBlockEntity.color = litColor;
            pipeBlockEntity.markDirty();
            world.updateListeners(pos, state, state, 0);
        }
        location = pos;
        blockWorld = world;
    }
}
