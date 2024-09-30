package pencil.mechanics.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pencil.mechanics.RainworldMechanicsClient;

public class PlayerModelScreen extends Screen {



    public PlayerModelScreen(Text title) {
        super(title);
    }

    public ButtonWidget button1;
    public ButtonWidget button2;

    @Override
    protected void init() {

        button1 = ButtonWidget.builder(Text.literal("Next"), button -> {
            if (RainworldMechanicsClient.selectedModel < RainworldMechanicsClient.modelList.length-1) {
                RainworldMechanicsClient.selectedModel += 1;
                RainworldMechanicsClient.modelID = RainworldMechanicsClient.modelList[ (int) RainworldMechanicsClient.selectedModel];
            }
        })
                .dimensions(width / 2 - 205, height/2 + 32, 200, 20)
                .build();
        button2 = ButtonWidget.builder(Text.literal("Back"), button -> {
                    if (RainworldMechanicsClient.selectedModel > 0) {
                        RainworldMechanicsClient.selectedModel -= 1;
                        RainworldMechanicsClient.modelID = RainworldMechanicsClient.modelList[ (int) RainworldMechanicsClient.selectedModel];
                    }
        })
                .dimensions(width / 2 + 5, height/2 + 32, 200, 20)
                .build();

        addDrawableChild(button1);
        addDrawableChild(button2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int scaledWidth = context.getScaledWindowWidth();
        int scaledHeight = context.getScaledWindowHeight();
        super.render(context, mouseX, mouseY, delta);
        context.drawTexture(new Identifier("rw-mechanics", "textures/gui/hud/slugcat_"+(int) RainworldMechanicsClient.selectedModel+".png"), (scaledWidth/2)-16, (scaledHeight/2)-16, 0, 0, 32, 32, 32, 32);
    }
}
