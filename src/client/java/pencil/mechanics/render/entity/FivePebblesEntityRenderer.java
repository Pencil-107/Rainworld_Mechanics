package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.FivePebblesEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FivePebblesEntityRenderer extends GeoEntityRenderer<FivePebblesEntity> {
    public FivePebblesEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<FivePebblesEntity> model) {
        super(renderManager, model);
    }
}
