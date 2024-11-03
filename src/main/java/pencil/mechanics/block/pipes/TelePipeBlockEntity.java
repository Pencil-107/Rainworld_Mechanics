package pencil.mechanics.block.pipes;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.RainworldMechanics;

public class TelePipeBlockEntity extends BlockEntity {

    public BlockPos linkedPos;

    public TelePipeBlockEntity(BlockPos pos, BlockState state) {
        super(RainworldMechanics.TELEPIPE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (linkedPos != null) {
            nbt.putInt("pos_x", linkedPos.getX());
            nbt.putInt("pos_y", linkedPos.getY());
            nbt.putInt("pos_z", linkedPos.getZ());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        linkedPos = new BlockPos(nbt.getInt("pos_x"), nbt.getInt("pos_y"), nbt.getInt("pos_z"));
    }
}
