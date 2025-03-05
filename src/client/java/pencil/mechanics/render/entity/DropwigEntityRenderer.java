package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.DropwigEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DropwigEntityRenderer  extends GeoEntityRenderer<DropwigEntity> {
    public DropwigEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<DropwigEntity> model) {
        super(renderManager, model);
    }
}
