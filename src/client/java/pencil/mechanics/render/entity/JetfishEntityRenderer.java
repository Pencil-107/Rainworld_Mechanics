package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.GreenLizardEntity;
import pencil.mechanics.entity.JetfishEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JetfishEntityRenderer extends GeoEntityRenderer<JetfishEntity> {
    public JetfishEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<JetfishEntity> model) {
        super(renderManager, model);
    }
}
