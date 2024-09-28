package pencil.mechanics.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import pencil.mechanics.RainworldMechanics;

import java.util.Optional;

public class itemGroupInit {

    private static final Text RAINWORLD_TITLE = Text.translatable("itemGroup." + RainworldMechanics.MOD_ID + ".rainworld_group");
    public static final ItemGroup RAINWORLD_GROUP = register("rainworld_group", FabricItemGroup.builder()
            .displayName(RAINWORLD_TITLE)
            .icon(Items.NAUTILUS_SHELL::getDefaultStack)
            .entries((displayContext, entries) -> Registries.ITEM.getIds()
                    .stream()
                    .filter(key -> key.getNamespace().equals(RainworldMechanics.MOD_ID))
                    .map(Registries.ITEM::getOrEmpty)
                    .map(Optional::orElseThrow)
                    .forEach(entries::add))
            .build());

    public static <T extends ItemGroup> T register(String name, T itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, RainworldMechanics.id(name), itemGroup);
    }

    public static void load() {}
}
