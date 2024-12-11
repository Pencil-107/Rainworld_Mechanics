package pencil.mechanics.gui.screen;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.block.pipes.PipeEntrance;
import pencil.mechanics.init.BlockInit;

public class BlockEditorScreen extends Screen {

    public BlockEditorScreen(Text title) {
        super(title);
    }

    public ButtonWidget button1;
    public ButtonWidget button2;
    public ButtonWidget button3;
    public ButtonWidget button4;
    public ButtonWidget button5;
    public ButtonWidget button6;
    public BlockState blockState;
    public String state1;
    public String state2;
    public String state3;
    public String state4;
    public String state5;
    public String state6;

    @Override
    protected void init() {
        blockState = RainworldMechanicsClient.editorBlock;
        if (blockState.getBlock() == BlockInit.PIPE_ENTRANCE) {
            state1 = blockState.get(PipeEntrance.VARIATION).asString();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        button1 = ButtonWidget.builder(Text.literal(state1+"true"), button -> {
                    if (RainworldMechanicsClient.selectedModel < RainworldMechanicsClient.modelList.length-1) {
                        RainworldMechanicsClient.selectedModel += 1;
                        RainworldMechanicsClient.modelID = RainworldMechanicsClient.modelList[ (int) RainworldMechanicsClient.selectedModel];
                    }
                })
                .dimensions(width / 2 - 205, height/2 + 32, 200, 20)
                .build();
    }
}
