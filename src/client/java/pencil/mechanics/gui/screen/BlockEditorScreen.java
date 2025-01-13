package pencil.mechanics.gui.screen;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IconButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.block.pipes.PipeEntrance;
import pencil.mechanics.init.BlockInit;

public class BlockEditorScreen extends Screen {

    public BlockEditorScreen(Text title) {
        super(title);
    }

    public ButtonWidget button1;
    public IconButtonWidget buttonTest;
    public ButtonWidget button2;
    public ButtonWidget button3;
    public ButtonWidget button4;
    public ButtonWidget button5;
    public ButtonWidget button6;
    public BlockState blockState;
    public BlockPos pos;
    public String state1;
    public Boolean state2;
    public Boolean state3;
    public Boolean state4;
    public Boolean state5;
    public Boolean state6;

    @Override
    protected void init() {

        blockState = RainworldMechanicsClient.editorBlock;
        pos = RainworldMechanicsClient.editorPos;
        if (blockState.getBlock() == BlockInit.PIPE_ENTRANCE) {
            state1 = blockState.get(PipeEntrance.VARIATION).asString();
            state2 = blockState.get(PipeEntrance.LIGHT_ONE);
            state3 = blockState.get(PipeEntrance.LIGHT_TWO);
            state4 = blockState.get(PipeEntrance.LIGHT_THREE);
            state5 = blockState.get(PipeEntrance.LIGHT_FOUR);
        }

        buttonTest = IconButtonWidget.builder(Text.literal(""),
                new Identifier("rw-mechanics", "textures/gui/screens/blockeditor/light0/light_0_left.png"),
                button -> {

                })
                .textureSize(20, 20)
                .iconSize(20, 20)

                .build();

        buttonTest.setWidth(20);

        button1 = ButtonWidget.builder(Text.literal("Light Type"), button -> {
                    if (pos != null) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBlockPos(pos);
                        buf.writeInt(0);
                        ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.EDIT_BLOCK_ID, buf);
                    }
                })
                .dimensions(width - 70, height/2 + 50, 70, 20)
                .build();
        button2 = ButtonWidget.builder(Text.literal("Light 1"), button -> {
                    if (pos != null) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBlockPos(pos);
                        buf.writeInt(1);
                        ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.EDIT_BLOCK_ID, buf);
                    }
                })
                .dimensions(width - 70, height/2 + 30, 70, 20)
                .build();
        button3 = ButtonWidget.builder(Text.literal("Light 2"), button -> {
                    if (pos != null) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBlockPos(pos);
                        buf.writeInt(2);
                        ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.EDIT_BLOCK_ID, buf);
                    }
                })
                .dimensions(width - 70, height/2 + 10, 70, 20)
                .build();
        button4 = ButtonWidget.builder(Text.literal("Light 3"), button -> {
                    if (pos != null) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBlockPos(pos);
                        buf.writeInt(3);
                        ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.EDIT_BLOCK_ID, buf);
                    }
                })
                .dimensions(width - 70, height/2 - 10, 70, 20)
                .build();
        button5 = ButtonWidget.builder(Text.literal("Light 4"), button -> {
                    if (pos != null) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeBlockPos(pos);
                        buf.writeInt(4);
                        ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.EDIT_BLOCK_ID, buf);
                    }
                })
                .dimensions(width - 70, height/2 - 30, 70, 20)
                .build();

        addDrawableChild(button1);
        addDrawableChild(button2);
        addDrawableChild(button3);
        addDrawableChild(button4);
        addDrawableChild(button5);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
