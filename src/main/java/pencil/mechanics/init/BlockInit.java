package pencil.mechanics.init;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.block.PoleJoint;
import pencil.mechanics.block.PoleX;
import pencil.mechanics.block.PoleY;
import pencil.mechanics.block.PoleZ;
import pencil.mechanics.block.pipes.PipeBlock;
import pencil.mechanics.block.pipes.PipeEntrance;

public class BlockInit {
    public static final Block POLE_X = registerWithItem("pole_x", new PoleX(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block POLE_Y = registerWithItem("pole_y", new PoleY(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block POLE_Z = registerWithItem("pole_z", new PoleZ(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block POLE_JOINT = registerWithItem("pole_joint", new PoleJoint(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block CRAWL_FRAME = registerWithItem("crawl_frame", new Block(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));

    public static final Block PIPE_BLOCK = Registry.register(Registries.BLOCK, new Identifier("rw-mechanics", "pipe_block"), new PipeBlock(getSetting()));
    public static final Block PIPE_ENTRANCE = Registry.register(Registries.BLOCK, new Identifier("rw-mechanics", "pipe_entrance"), new PipeEntrance(getSetting()));

    public static <T extends Block> T register(String name, T block){
        return Registry.register(Registries.BLOCK, RainworldMechanics.id(name), block);
    }

    public static  <T extends Block> T registerWithItem(String name, T block, Item.Settings settings) {
        T registered = register(name, block);
        ItemInit.register(name, new BlockItem(registered, settings));
        return registered;
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        return registerWithItem(name, block, new Item.Settings());
    }

    private static AbstractBlock.Settings getSetting() {
        AbstractBlock.ContextPredicate never = (state, world, pos) -> false;
        return FabricBlockSettings.copyOf(Blocks.GLASS).suffocates(never).blockVision(never);
    }

    public static void load() {}
}
