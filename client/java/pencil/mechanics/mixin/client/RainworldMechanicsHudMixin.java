package pencil.mechanics.mixin.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.player.movement.Crawling;
import pencil.mechanics.player.movement.PoleClimbing;

@Mixin(InGameHud.class)
public class RainworldMechanicsHudMixin implements HudRenderCallback {

    @Shadow
    private int scaledHeight;
    @Shadow private int scaledWidth;
    @Shadow @Final
    private MinecraftClient client;
    private int timerLevel = 0;
    private int timerMax = 25;
    private int guiY = 74;
    private int guiX = 0;
    private int lastScaleY = 0;
    private boolean iterating = false;
    private float iterations = 200f;
    private int karmaGuiX = 50;
    private int karmaGuiY = 0;
    private int karmaLevel = RainworldMechanicsClient.karmaLevel;
    private boolean setPos = false;
    private int listYPos = karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (karmaLevel));

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    public void onRender (DrawContext context, float tickDelta, CallbackInfo ci) {
        this.scaledWidth = context.getScaledWindowWidth();
        this.scaledHeight = context.getScaledWindowHeight();
        karmaLevel = RainworldMechanicsClient.karmaLevel;
        //float cyclePercent = CycleTimer.getCycleTimer(client.player.getWorld().getRegistryKey()).getTimePercentage();
        //if (cyclePercent > 0 && cyclePercent < 1) {
        //    timerLevel = timerMax - Math.round(cyclePercent * timerMax);
        //}
    }


    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    public void onRenderHotbar (float tickDelta, DrawContext context, CallbackInfo ci) {
        if (!client.player.isCreative()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void onRenderStatusEffect (DrawContext context, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    public void onRenderHealth (DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    public void onRenderXP (DrawContext context, int x, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    public void onRenderStatus (DrawContext context, CallbackInfo ci) {
        if (!client.player.isCreative()) {
            int foodLevel = RainworldMechanicsClient.foodLevel;
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/background_edge.png"), 0, this.scaledHeight-guiY, 0, 0, 307, 74, 307, 74);
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/background_corner.png"), 0, this.scaledHeight - guiY, 0, 0, 307, 74, 307, 74);
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hunger/hunger"+foodLevel+".png"), guiX+47, this.scaledHeight-(18+(guiY/2)), 0, 0, 256, 38, 256, 38);
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/timer/timer"+timerLevel+".png"), guiX-8, this.scaledHeight-(guiY + 18), 1, 0, 108, 109, 108, 109);
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma"+RainworldMechanicsClient.karmaLevel+".png"), guiX-8, this.scaledHeight-(guiY + 18), 1, 0, 108, 109, 108, 109);
            if (RainworldMechanicsClient.crawling && Crawling.pressed) {
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/crawl_charge.png"), ((guiX)+8-64) + (int) (64*(Crawling.heldTime*(1/Crawling.heldTimeMax))), this.scaledHeight-(guiY+38), 0+(64*(Crawling.heldTime*(1/Crawling.heldTimeMax))), 0, 64, 32, 128, 32);
            } else if (RainworldMechanicsClient.crawling) {
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/crawl_charge.png"), ((guiX)+8), this.scaledHeight-(guiY+38), 64, 0, 64, 32, 128, 32);
            } else if (PoleClimbing.climbing) {
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/state_horizontal_climbing.png"), guiX-5, this.scaledHeight-(guiY+54), 0, 0, 64, 64, 64, 64);
            } else {
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/state_standing.png"), guiX-5, this.scaledHeight-(guiY+54), 0, 0, 64, 64, 64, 64);
            }
            ci.cancel();
        }
    }

}
