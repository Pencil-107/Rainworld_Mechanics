package pencil.mechanics.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import pencil.mechanics.block.pipes.PipeBlock;
import pencil.mechanics.block.pipes.TelePipeBlock;
import pencil.mechanics.block.pipes.TelePipeBlockEntity;

import java.util.Objects;

public class PipeWand extends Item {
    private BlockPos selected;
    private boolean hit;

    public PipeWand(Settings settings) {
        super(settings);
        selected = null;
        hit = false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        hit = false;

        if (!context.getWorld().isClient() && ActionResult.SUCCESS.isAccepted()) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) instanceof TelePipeBlockEntity) {
                if (selected != null && ((TelePipeBlock) context.getWorld().getBlockState(context.getBlockPos()).getBlock()).linkedPos == null) {
                    ((TelePipeBlockEntity) Objects.requireNonNull(context.getWorld().getBlockEntity(context.getBlockPos()))).linkedPos = selected;
                    context.getPlayer().sendMessage(Text.literal(selected.toString()+" Linked to Block"), false);
                    hit = true;
                    selected = null;
                }
                if (selected == null && !hit) {
                    selected = context.getBlockPos();
                    context.getPlayer().sendMessage(Text.literal(selected.toString()+" Selected"), false);
                    hit = true;
                }
            } else {
                return super.useOnBlock(context);
            }
        }

        return super.useOnBlock(context);
    }
}
