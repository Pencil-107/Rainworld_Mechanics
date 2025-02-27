package pencil.mechanics.render.block;

import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import pencil.mechanics.block.pipes.PipeBlockEntity;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class PipeBlockEntityRenderer implements BlockEntityRenderer<PipeBlockEntity> {

    public PipeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(PipeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

        // Retrieve the block state from the block entity
        BlockState state = entity.getBaseBlock();
        if (state == null) {
            return;
        }

        // Get the original baked model
        BakedModel originalModel = blockRenderManager.getModel(state);

        BakedModel culledModel = new CulledBakedModel(originalModel, entity.getCachedState());


        // Render the culled model at the entity's position
        blockRenderManager.getModelRenderer().render(
            entity.getWorld(), culledModel, state, entity.getPos(), matrices,
            vertexConsumers.getBuffer(RenderLayer.getSolid()),
            false, Random.create(), state.getRenderingSeed(entity.getPos()),
            OverlayTexture.DEFAULT_UV
        );
    }

    @Override
    public int getRenderDistance() {
        return 200;
    }
}
