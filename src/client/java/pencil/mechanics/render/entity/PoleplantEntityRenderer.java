package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.PoleplantEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PoleplantEntityRenderer extends GeoEntityRenderer<PoleplantEntity> {
    public PoleplantEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<PoleplantEntity> model) {
        super(renderManager, model);
    }
}
