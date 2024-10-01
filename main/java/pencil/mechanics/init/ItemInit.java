package pencil.mechanics.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.item.PipeWand;

public class ItemInit {

    public static final Item PIPE_WAND = register("pipe_wand", new PipeWand(new Item.Settings()));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, RainworldMechanics.id(name), item);
    }

    public static void load() {}
}
