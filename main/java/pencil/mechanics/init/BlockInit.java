package pencil.mechanics.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.block.PoleX;
import pencil.mechanics.block.PoleY;
import pencil.mechanics.block.PoleZ;

public class BlockInit {
    public static final Block POLE_X = registerWithItem("pole_x", new PoleX(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block POLE_Y = registerWithItem("pole_y", new PoleY(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block POLE_Z = registerWithItem("pole_z", new PoleZ(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));
    public static final Block CRAWL_FRAME = registerWithItem("crawl_frame", new Block(AbstractBlock.Settings.create()
            .noBlockBreakParticles().noCollision()));

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

    public static void load() {}
}
