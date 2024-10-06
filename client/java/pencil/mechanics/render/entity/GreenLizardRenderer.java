package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GreenLizardRenderer extends GeoEntityRenderer<pencil.mechanics.entity.GreenLizard> {
    public GreenLizardRenderer(EntityRendererFactory.Context renderManager, GeoModel<pencil.mechanics.entity.GreenLizard> model) {
        super(renderManager, model);
    }
}
