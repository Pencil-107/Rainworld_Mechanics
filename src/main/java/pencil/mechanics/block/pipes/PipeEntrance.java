package pencil.mechanics.block.pipes;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.data.client.VariantSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.init.BlockInit;
import pencil.mechanics.init.ItemInit;

public class PipeEntrance extends Block implements Waterloggable{

    private Entity lastCol;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public BlockPos exit;

    // Enum for orientation
    public enum Orientation implements StringIdentifiable {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST),
        UP(Direction.UP),
        DOWN(Direction.DOWN);

        private final Direction direction;

        Orientation(Direction direction) {
            this.direction = direction;
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public enum Variation implements StringIdentifiable {
        ONE(0),
        TWO(1),
        THREE(2),
        FOUR(3),
        FIVE(4);

        private final int value;

        Variation(int value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }

        public int getValue() {
            return value;
        }
    }

    // Property for pipe connection direction
    public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.of("orientation", Orientation.class);
    public static final EnumProperty<Orientation> CONNECTION = EnumProperty.of("connection", Orientation.class);
    public static final EnumProperty<Variation> VARIATION = EnumProperty.of("variation", Variation.class);
    public static final BooleanProperty LIGHT_ONE = BooleanProperty.of("light_one");
    public static final BooleanProperty LIGHT_TWO = BooleanProperty.of("light_two");
    public static final BooleanProperty LIGHT_THREE = BooleanProperty.of("light_three");
    public static final BooleanProperty LIGHT_FOUR = BooleanProperty.of("light_four");

    public PipeEntrance(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(ORIENTATION, Orientation.NORTH)
                .with(CONNECTION, Orientation.NORTH)
                .with(VARIATION, Variation.ONE)
                .with(LIGHT_ONE, Boolean.valueOf(true))
                .with(LIGHT_TWO, Boolean.valueOf(true))
                .with(LIGHT_THREE, Boolean.valueOf(true))
                .with(LIGHT_FOUR, Boolean.valueOf(true))
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION, CONNECTION, VARIATION, LIGHT_ONE, LIGHT_TWO, LIGHT_THREE, LIGHT_FOUR, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide(); // Get the face of the block that was clicked
        Orientation orientation = Orientation.NORTH; // Default orientation

        for (Orientation o : Orientation.values()) {
            if (o.getDirection() == facing) {
                orientation = o;
                break;
            }
        }

        BlockState state = this.getDefaultState().with(ORIENTATION, orientation);
        Direction connectionDirection = getConnectionDirection(ctx, orientation.getDirection());
        Orientation connectionOrientation = Orientation.NORTH; // Default to NORTH

        for (Orientation o : Orientation.values()) {
            if (o.getDirection() == connectionDirection) {
                connectionOrientation = o;
                break;
            }
        }

        return state.with(CONNECTION, connectionOrientation).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    private Direction getConnectionDirection(ItemPlacementContext ctx, Direction excludeDirection) {
        for (Direction direction : Direction.values()) {
            if (direction == excludeDirection) continue;
            BlockState neighborState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction));
            Block neighborBlock = neighborState.getBlock();
            if (neighborBlock instanceof PipeBlock) {
                if (((PipeBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction))).entrance1 != null &&
                        ctx.getBlockPos() == ((PipeBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction))).entrance1) {
                    exit = ((PipeBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction))).entrance2;
                }
                if (((PipeBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction))).entrance2 != null &&
                        ctx.getBlockPos() == ((PipeBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction))).entrance2) {
                    exit = ((PipeBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos().offset(direction))).entrance1;
                }
                return direction;
            }
            if (neighborBlock instanceof PipeEntrance) {
                exit = ctx.getBlockPos().offset(direction);
            }
        }
        return excludeDirection; // Default to the same direction if no connection found
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Block neighborBlock = neighborState.getBlock();
        if (neighborBlock instanceof PipeBlock || neighborBlock instanceof PipeEntrance) {
            Orientation connectionOrientation = Orientation.NORTH; // Default to NORTH

            for (Orientation o : Orientation.values()) {
                if (o.getDirection() == direction) {
                    connectionOrientation = o;
                    break;
                }
            }

            if (neighborBlock instanceof PipeBlock) {
                if (((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance1 != null &&
                        pos == ((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance1) {
                    exit = ((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance2;
                }
                if (((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance2 != null &&
                        pos == ((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance2) {
                    exit = ((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance1;
                }

                if (((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance1 == null) {
                    ((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance1 = pos;
                } else if (((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance2 == null) {
                    ((PipeBlockEntity) world.getBlockEntity(neighborPos)).entrance2 = pos;
                }
            }

            return state.with(CONNECTION, connectionOrientation);
        }

        if (state.get(WATERLOGGED)) {
            // For 1.17 and below:
            // world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            // For versions since 1.18 below 1.21.2:
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return state;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player != null) {
            TransportManager.startTransport(player, pos, state.get(CONNECTION).getDirection());
        }
        if (!world.isClient && player != null && player.isCreative() && exit != null) {
            player.sendMessage(Text.of(""+exit), true);
        } else if (!world.isClient && player != null && player.isCreative()) {
            player.sendMessage(Text.of("No exit connected"), true);
        }
        return ActionResult.SUCCESS;
    }

}
