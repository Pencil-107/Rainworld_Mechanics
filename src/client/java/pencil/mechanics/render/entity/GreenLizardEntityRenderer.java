package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.GreenLizardEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GreenLizardEntityRenderer extends GeoEntityRenderer<GreenLizardEntity> {
    public GreenLizardEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<GreenLizardEntity> model) {
        super(renderManager, model);
    }
}
