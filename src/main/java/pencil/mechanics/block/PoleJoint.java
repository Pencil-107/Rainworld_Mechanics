package pencil.mechanics.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import pencil.mechanics.init.BlockInit;

public class PoleJoint extends ConnectingBlock {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public PoleJoint(Settings settings) {
        super(2, settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(NORTH, Boolean.valueOf(false))
                        .with(EAST, Boolean.valueOf(false))
                        .with(SOUTH, Boolean.valueOf(false))
                        .with(WEST, Boolean.valueOf(false))
                        .with(UP, Boolean.valueOf(false))
                        .with(DOWN, Boolean.valueOf(false))
                        .with(WATERLOGGED, Boolean.valueOf(false))
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
        return this.canConnectToPole(state);
    }

    private boolean canConnectToPole(BlockState state) {
        return state.getBlock() == BlockInit.POLE_X || state.getBlock() == BlockInit.POLE_Y || state.getBlock() == BlockInit.POLE_Z || state.getBlock() == BlockInit.POLE_JOINT;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockPos blockPos6 = blockPos.up();
        BlockPos blockPos7 = blockPos.down();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        BlockState blockState5 = blockView.getBlockState(blockPos6);
        BlockState blockState6 = blockView.getBlockState(blockPos7);
        return super.getPlacementState(ctx)
                .with(NORTH, Boolean.valueOf(this.canConnect(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH)))
                .with(EAST, Boolean.valueOf(this.canConnect(blockState2, blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.WEST), Direction.WEST)))
                .with(SOUTH, Boolean.valueOf(this.canConnect(blockState3, blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.NORTH), Direction.NORTH)))
                .with(WEST, Boolean.valueOf(this.canConnect(blockState4, blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.EAST), Direction.EAST)))
                .with(UP, Boolean.valueOf(this.canConnect(blockState5, blockState5.isSideSolidFullSquare(blockView, blockPos6, Direction.UP), Direction.UP)))
                .with(DOWN, Boolean.valueOf(this.canConnect(blockState6, blockState6.isSideSolidFullSquare(blockView, blockPos7, Direction.DOWN), Direction.DOWN)))
                .with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if ((Boolean)state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (direction.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return direction.getAxis().getType() == Direction.Type.HORIZONTAL
                    ? state.with(
                    FACING_PROPERTIES.get(direction),
                    this.canConnect(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()), direction.getOpposite())
            )
                    : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        } else if (direction.getAxis().getType() == Direction.Type.VERTICAL) {
            return direction.getAxis().getType() == Direction.Type.VERTICAL
                    ? state.with(
                    FACING_PROPERTIES.get(direction),
                    this.canConnect(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()), direction.getOpposite())
            )
                    : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, WATERLOGGED);
    }
}
