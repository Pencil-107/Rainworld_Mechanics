package pencil.mechanics.player.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import pencil.mechanics.ConfigValues;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.init.BlockInit;

public class Crawling {
    public static float heldTime = 0;
    public static final float heldTimeMax = 35;
    public static boolean pressed = false;
    public static float crawlJumpXMultiplier = ConfigValues.crawlJumpXMultiplier;
    public static float crawlJumpYMultiplier = ConfigValues.crawlJumpYMultiplier;
    public static boolean soundPlayed = false;

    public static void main(MinecraftClient client) {
        if (RainworldMechanicsClient.clientPlayer != null) {
            if (crawlJumpXMultiplier != ConfigValues.crawlJumpXMultiplier || crawlJumpYMultiplier != ConfigValues.crawlJumpYMultiplier) {
                crawlJumpXMultiplier = ConfigValues.crawlJumpXMultiplier;
                crawlJumpYMultiplier = ConfigValues.crawlJumpYMultiplier;
            }
            if (client.options.jumpKey.isPressed() && !client.options.forwardKey.isPressed()) {
                RainworldMechanicsClient.jumpHeld = true;
                if (heldTime < heldTimeMax) {
                    ++heldTime;
                }
                pressed = true;
            }
            if (heldTime >= heldTimeMax && !soundPlayed){
                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "block.note_block.bell")), 1, 1);
                soundPlayed = true;
            }
            if (!client.options.jumpKey.isPressed() && pressed && client.player.isOnGround()) {
                if (heldTime >= heldTimeMax && !client.options.forwardKey.isPressed()) {
                    client.player.addVelocity(client.player.getRotationVector().getX()* crawlJumpXMultiplier, crawlJumpYMultiplier, client.player.getRotationVector().getZ()* crawlJumpXMultiplier);
                    RainworldMechanicsClient.jumpHeld = false;
                    heldTime = 0;
                    soundPlayed = false;
                    pressed = false;
                } else if (client.player.isOnGround()){
                    RainworldMechanicsClient.jumpHeld = false;
                    //client.player.addVelocity(0, 0.6, 0);
                    heldTime = 0;
                    soundPlayed = false;
                    pressed = false;
                }
            }
            if (client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.CRAWL_FRAME || client.player.getWorld().getBlockState(client.player.getBlockPos()).getBlock() == BlockInit.PIPE_ENTRANCE) {
                RainworldMechanicsClient.crawlFrame = true;
                client.player.setNoGravity(true);
                if (client.options.forwardKey.isPressed()) {
                    client.player.setVelocity(client.player.getRotationVector().getX()* 0.1, client.player.getRotationVector().getY()* 0.1, client.player.getRotationVector().getZ()* 0.1);
                } else {
                    client.player.setVelocity(0, 0, 0);
                }
            } else {
                client.player.setNoGravity(false);
                RainworldMechanicsClient.crawlFrame = false;
            }
        } else {
            heldTime = 0;
            pressed = false;
            soundPlayed = false;
            client.player.setNoGravity(false);
            RainworldMechanicsClient.crawlFrame = false;
        }
    }
}
