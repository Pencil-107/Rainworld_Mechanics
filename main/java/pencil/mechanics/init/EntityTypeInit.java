package pencil.mechanics.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.util.TypeFilter;
import org.jetbrains.annotations.Nullable;


public class EntityTypeInit<T extends Entity> implements ToggleableFeature, TypeFilter<Entity, T> {


    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, id, type.build(id));
    }

    public static void load() {
    }

    @Override
    public FeatureSet getRequiredFeatures() {
        return null;
    }

    @Nullable
    @Override
    public T downcast(Entity obj) {
        return null;
    }

    @Override
    public Class<? extends Entity> getBaseClass() {
        return null;
    }

}
