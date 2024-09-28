package pencil.mechanics.init;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import pencil.mechanics.RainworldMechanics;

public class ItemInit {
    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, RainworldMechanics.id(name), item);
    }

    public static void load() {}
}
