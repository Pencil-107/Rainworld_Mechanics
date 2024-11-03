package pencil.mechanics.block.pipes;

import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.RainworldMechanics;

public class PipeBlockEntity extends BlockEntity {

    private BlockState baseBlockState;

    public int color = 0x1f1f1f;

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(RainworldMechanics.PIPE_BLOCK_ENTITY, pos, state);
    }

    public BlockState getBaseBlock() {
        return baseBlockState;
    }

    public void setBaseBlock(BlockState blockState) {
        this.baseBlockState = blockState;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (baseBlockState != null) {  // Ensure that there is a state to save
            nbt.put("BaseBlockState", NbtHelper.fromBlockState(baseBlockState));
            nbt.putInt("color", color);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        color = nbt.getInt("color");

        if (nbt.contains("BaseBlockState", 10)) {
            NbtCompound stateCompound = nbt.getCompound("BaseBlockState");
            DataResult<BlockState> blockStateResult = BlockState.CODEC.parse(NbtOps.INSTANCE, stateCompound);
            blockStateResult.result().ifPresent(this::setBaseBlock);
        }
    }

    @Override
    public @Nullable Object getRenderData() {
        // this is the method from `RenderDataBlockEntity` class.
        return color;
    }
}
