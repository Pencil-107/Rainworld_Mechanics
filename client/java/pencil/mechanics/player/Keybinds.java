package pencil.mechanics.player;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import pencil.mechanics.RainworldMechanicsClient;

public class Keybinds {

    private static final MinecraftClient client = RainworldMechanicsClient.clientPlayer;

    public static KeyBinding crawlKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rw-mechanics.crawl", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_LEFT_SHIFT, // The keycode of the key
            "category.rw-mechanics.rainworld" // The translation key of the keybinding's category.
    ));

    public static KeyBinding grabKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rw-mechanics.grab", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_F, // The keycode of the key
            "category.rw-mechanics.rainworld" // The translation key of the keybinding's category.
    ));

    public static KeyBinding interactKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rw-mechanics.interact", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_E, // The keycode of the key
            "category.rw-mechanics.rainworld" // The translation key of the keybinding's category.
    ));

    public static KeyBinding dropKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rw-mechanics.drop", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_Q, // The keycode of the key
            "category.rw-mechanics.rainworld" // The translation key of the keybinding's category.
    ));

    private static KeyBinding testingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rw-mechanics.test1", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_Y, // The keycode of the key
            "category.rw-mechanics.rainworld" // The translation key of the keybinding's category.
    ));

    public static void main() {
        if (client != null) {
            client.options.inventoryKey.setPressed(false);
            client.options.swapHandsKey.setPressed(false);
            client.options.dropKey.setPressed(false);
            client.options.sneakKey.setPressed(false);
            client.options.chatKey.setPressed(false);
        }
    }

    public static void disable() {
        if (client != null) {
            crawlKey.setPressed(false);
            grabKey.setPressed(false);
            interactKey.setPressed(false);
            dropKey.setPressed(false);
            testingKey.setPressed(false);
        }
    }

}
