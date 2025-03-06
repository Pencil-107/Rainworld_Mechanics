package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.JellyfishEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JellyfishEntityRenderer extends GeoEntityRenderer<JellyfishEntity> {
    public JellyfishEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel model) {
        super(renderManager, model);
    }
}
