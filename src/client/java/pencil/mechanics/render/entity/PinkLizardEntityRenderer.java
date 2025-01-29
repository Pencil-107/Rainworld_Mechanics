package pencil.mechanics.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import pencil.mechanics.entity.GreenLizardEntity;
import pencil.mechanics.entity.PinkLizardEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PinkLizardEntityRenderer extends GeoEntityRenderer<PinkLizardEntity> {
    public PinkLizardEntityRenderer(EntityRendererFactory.Context renderManager, GeoModel<PinkLizardEntity> model) {
        super(renderManager, model);
    }
}
