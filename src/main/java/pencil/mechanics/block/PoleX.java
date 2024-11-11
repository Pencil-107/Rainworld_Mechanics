package pencil.mechanics.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
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

public class PoleX extends Block implements Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public PoleX(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0f, 0.0f, 0.0f, 1f, 1f, 1f);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            // For 1.17 and below:
            // world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            // For versions since 1.18 below 1.21.2:
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
}
