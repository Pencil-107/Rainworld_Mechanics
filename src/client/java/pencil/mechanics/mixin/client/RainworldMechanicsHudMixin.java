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
        if (!client.player.isCreative() && !client.player.isSpectator()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void onRenderStatusEffect (DrawContext context, CallbackInfo ci) {
        if (!client.player.isCreative() && !client.player.isSpectator()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    public void onRenderHealth (DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        if (!client.player.isCreative() && !client.player.isSpectator()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    public void onRenderXP (DrawContext context, int x, CallbackInfo ci) {
        if (!client.player.isCreative() && !client.player.isSpectator()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    public void onRenderStatus (DrawContext context, CallbackInfo ci) {
        if (client.player != null && !client.player.isCreative() && !client.player.isSpectator()) {
            int foodLevel = RainworldMechanicsClient.foodLevel;
            int timeLevel = RainworldMechanicsClient.cycleTime;
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/hunger/hunger_count"+foodLevel+".png"), (scaledWidth/3) , this.scaledHeight-(int) ((scaledWidth/3)/2.78), 0, 0, scaledWidth/3,  (int) ((scaledWidth/3)/2.78), scaledWidth/3,  (int) ((scaledWidth/3)/2.78));
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/karma/karma_symbol.png"), (scaledWidth/3) , this.scaledHeight-(int) ((scaledWidth/3)/2.78), 0, 0, scaledWidth/3,  (int) ((scaledWidth/3)/2.78), scaledWidth/3,  (int) ((scaledWidth/3)/2.78));
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/timer/rain_timer"+timeLevel+".png"), (scaledWidth/3) , this.scaledHeight-(int) ((scaledWidth/3)/2.78), 0, 0, scaledWidth/3,  (int) ((scaledWidth/3)/2.78), scaledWidth/3,  (int) ((scaledWidth/3)/2.78));
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/slugcat/slugcat_symbol.png"), (scaledWidth/3) , this.scaledHeight-(int) ((scaledWidth/3)/2.78), 0, 0, scaledWidth/3,  (int) ((scaledWidth/3)/2.78), scaledWidth/3,  (int) ((scaledWidth/3)/2.78));
            context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/inventory_slots.png"), (scaledWidth/3) , this.scaledHeight-(int) ((scaledWidth/3)/2.78), 0, 0, scaledWidth/3,  (int) ((scaledWidth/3)/2.78), scaledWidth/3,  (int) ((scaledWidth/3)/2.78));
            if (RainworldMechanicsClient.stunned) {
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/black_vignette.png"), 0 , 0, 0, 0, scaledWidth,  scaledHeight, scaledWidth,  scaledHeight);
            }
            ci.cancel();
        }
    }

}
