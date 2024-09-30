package pencil.mechanics.block.pipes;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.RainworldMechanics;

public class TelePipeBlockEntity extends BlockEntity {
    public TelePipeBlockEntity(BlockPos pos, BlockState state) {
        super(RainworldMechanics.TELEPIPE_BLOCK_ENTITY, pos, state);
    }
}
