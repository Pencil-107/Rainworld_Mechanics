package pencil.mechanics.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import pencil.mechanics.RainworldMechanicsClient;

public class KarmaScreen extends Screen implements NamedScreenHandlerFactory {

    private int scaledHeight;
    private int scaledWidth;
    private int prevKarma;
    private int karmaLevel;
    private int karmaGuiX = 50;
    private int karmaGuiY = 0;
    private int curListY;

    private boolean listTargetSet = false;
    private boolean listEase1 = false;
    private boolean listEase2 = false;
    private float listAnimationTimeMax = 10;
    private float listAnimationTime = listAnimationTimeMax;
    private int listAnimationStep = (1);
    private int listEasing = 10;

    private float timeMax = 200;
    private float time = timeMax;

    public KarmaScreen(Text title) {
        super(title);
        time = timeMax;
        prevKarma = RainworldMechanicsClient.lastKarmaLevel;
        karmaLevel = RainworldMechanicsClient.karmaLevel;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            prevKarma = RainworldMechanicsClient.lastKarmaLevel;
            karmaLevel = RainworldMechanicsClient.karmaLevel;
            super.render(context, mouseX, mouseY, delta);
            if (RainworldMechanicsClient.cycling && RainworldMechanicsClient.survived || RainworldMechanicsClient.cycling && RainworldMechanicsClient.karmaShielded) {
                boolean glowed = false;
                int karmaGuiX = 50;
                int karmaGuiY = 0;
                int foodLevel = RainworldMechanicsClient.foodLevel;
                this.scaledWidth = context.getScaledWindowWidth();
                this.scaledHeight = context.getScaledWindowHeight();
                RenderSystem.enableBlend();
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/background_screen_black.png"), 0, 0, 0, 0, this.scaledWidth, this.scaledHeight, 16, 16);
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/background_karma_screen.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/trim_karma_screen.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                if (karmaLevel == prevKarma) {
                    context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/karma_list.png"), karmaGuiX, karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (karmaLevel)), 0, 0, (int) (this.scaledHeight / 1.8f), (int) (this.scaledHeight * 2.53f), (int) (this.scaledHeight / 1.8f), (int) (this.scaledHeight * 2.53f));
                    listEase1 = false;
                    listEase2 = false;
                    listTargetSet = false;
                } else if (karmaLevel > prevKarma) {
                    int targetListY = (karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (karmaLevel)));
                    int targetEaseY;

                    if (!listTargetSet) {
                        curListY = (karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (prevKarma)));
                        listTargetSet = true;
                    }

                    if (targetListY > curListY) {
                        curListY += listAnimationStep;
                    }



                    context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/karma_list.png"), karmaGuiX,
                            curListY,
                            0, 0,
                            (int) (this.scaledHeight / 1.8f),
                            (int) (this.scaledHeight * 2.53f),
                            (int) (this.scaledHeight / 1.8f),
                            (int) (this.scaledHeight * 2.53f));
                }
                if (RainworldMechanicsClient.karmaShielded) {
                    context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/karma_shield.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                }
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/shadows_karma_screen.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/player_karma_screen.png"), (scaledWidth - ((this.scaledHeight/3)*2)), this.scaledHeight/12, 0, 0, (this.scaledHeight/3)*2, (this.scaledHeight/3)*2, (this.scaledHeight/3)*2, (this.scaledHeight/3)*2);
            } else {
                boolean glowed = false;
                int karmaGuiX = 50;
                int karmaGuiY = 0;
                int foodLevel = RainworldMechanicsClient.foodLevel;
                this.scaledWidth = context.getScaledWindowWidth();
                this.scaledHeight = context.getScaledWindowHeight();
                RenderSystem.enableBlend();
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/background_screen_black.png"), 0, 0, 0, 0, this.scaledWidth, this.scaledHeight, 16, 16);
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/background_karma_screen.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/trim_karma_screen.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                if (karmaLevel == prevKarma) {
                    context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/karma_list.png"), karmaGuiX, karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (karmaLevel)), 0, 0, (int) (this.scaledHeight / 1.8f), (int) (this.scaledHeight * 2.53f), (int) (this.scaledHeight / 1.8f), (int) (this.scaledHeight * 2.53f));
                    listEase1 = false;
                    listEase2 = false;
                    listTargetSet = false;
                } else if (karmaLevel < prevKarma) {
                    int targetListY = (karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (karmaLevel)));

                    if (!listTargetSet) {
                        curListY = (karmaGuiY - ((((((this.scaledHeight / 2)) + (((int) ((this.scaledHeight * 2.536) / 2) - (int) (((int) (this.scaledHeight * 2.536) / 9.1)) + ((int) (this.scaledHeight * 2.536) / 36)))))) + (int) ((this.scaledHeight * 2.536) / 9) - (int) ((this.scaledHeight * 2.536) / 10) * (prevKarma)));
                        listTargetSet = true;
                    }

                    if (targetListY < curListY) {
                        curListY -= listAnimationStep;
                    }



                    context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/karma_list.png"), karmaGuiX,
                            curListY,
                            0, 0,
                            (int) (this.scaledHeight / 1.8f),
                            (int) (this.scaledHeight * 2.53f),
                            (int) (this.scaledHeight / 1.8f),
                            (int) (this.scaledHeight * 2.53f));
                }
                if (RainworldMechanicsClient.karmaShielded) {
                    context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/karma_shield.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                }
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/shadows_karma_screen.png"), karmaGuiX, karmaGuiY, 0, 0, (int) (this.scaledHeight / 1.8f), this.scaledHeight, (int) (this.scaledHeight / 1.8f), this.scaledHeight);
                context.drawTexture(new Identifier("rw-mechanics", "textures/gui/karma/karma_screen/player_karma_screen_death.png"), (scaledWidth - ((this.scaledHeight/3)*2)), this.scaledHeight/12, 0, 0, (this.scaledHeight/3)*2, (this.scaledHeight/3)*2, (this.scaledHeight/3)*2, (this.scaledHeight/3)*2);
            }
        if (time > 0) {
            time--;
        } else if (time <= 0 && karmaLevel >= prevKarma) {
            RainworldMechanicsClient.cycling = false;
            RainworldMechanicsClient.karmaScren = false;
            RainworldMechanicsClient.lastKarmaLevel = RainworldMechanicsClient.karmaLevel;
            client.currentScreen.close();
        }
    }

    @Override
    public Text getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }
}
