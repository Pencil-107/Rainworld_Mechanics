package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.GreenLizardEntity;
import pencil.mechanics.entity.NoodleflyEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NoodleflyEntityRenderer extends GeoEntityRenderer<NoodleflyEntity> {
    public NoodleflyEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<NoodleflyEntity> model) {
        super(renderManager, model);
    }
}