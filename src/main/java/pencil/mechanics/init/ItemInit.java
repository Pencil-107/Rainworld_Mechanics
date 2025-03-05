package pencil.mechanics.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.item.PipeWand;
import pencil.mechanics.item.SpearItem;

public class ItemInit {

    public static final Item SPEAR_ITEM = register("spear_item", new SpearItem(new Item.Settings().maxDamage(250)));

    public static final Item PIPE_WAND = register("pipe_wand", new PipeWand(new Item.Settings()));

    public static final Item GREEN_LIZARD_SPAWN_EGG = register("green_lizard_spawn_egg", new SpawnEggItem(EntityTypeInit.GREEN_LIZARD, 0x000000, 0x00AA00, new Item.Settings()));

    public static final Item NOODLE_FLY_SPAWN_EGG = register("noodle_fly_spawn_egg", new SpawnEggItem(EntityTypeInit.NOODLEFLY_ENTITY, 0xf7785d, 0xf75d5d, new Item.Settings()));

    public static final Item POLEPLANT_SPAWNN_EGG = register("poleplant_spawn_egg", new SpawnEggItem(EntityTypeInit.NOODLEFLY_ENTITY, 0xf7785d, 0xf75d5d, new Item.Settings()));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, RainworldMechanics.id(name), item);
    }

    public static void load() {}
}
