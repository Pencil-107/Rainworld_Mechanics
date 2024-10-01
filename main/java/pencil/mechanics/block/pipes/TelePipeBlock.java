package pencil.mechanics.block.pipes;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import pencil.mechanics.init.ItemInit;

import java.util.Objects;

public class TelePipeBlock extends PipeBlock {
    public TelePipeBlock(Settings settings) {
        super(settings);
    }

    public BlockPos linkedPos;

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // Custom logic for TelePipe connections, if any
        linkedPos = null;
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        linkedPos = null;
        return new TelePipeBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof TelePipeBlockEntity) {
            if (((TelePipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).linkedPos != null) {
                player.sendMessage(Text.literal(((TelePipeBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos))).linkedPos.toString()+" Current Linked Pos"), false);
            }
        } else if (player.getActiveHand() == Hand.MAIN_HAND && player.getMainHandStack().isEmpty() && player.isCreative()) {
            player.sendMessage(Text.literal("no linked position"), false);
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }
}
