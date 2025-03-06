package pencil.mechanics.player.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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

    private static boolean wall3 = false;
    private static boolean wall4 = false;

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
                if (heldTime >= heldTimeMax && !client.options.forwardKey.isPressed() && RainworldMechanicsClient.crawling) {
                    client.player.addVelocity(client.player.getRotationVector().getX()* crawlJumpXMultiplier, crawlJumpYMultiplier, client.player.getRotationVector().getZ()* crawlJumpXMultiplier);
                    RainworldMechanicsClient.jumpHeld = false;
                    heldTime = 0;
                    soundPlayed = false;
                    pressed = false;
                } else if (client.player.isOnGround()){
                    RainworldMechanicsClient.jumpHeld = false;
                    client.player.addVelocity(0, 0.6, 0);
                    heldTime = 0;
                    soundPlayed = false;
                    pressed = false;
                }
            }
        } else {
            heldTime = 0;
            pressed = false;
            soundPlayed = false;
            client.player.setNoGravity(false);
            RainworldMechanicsClient.crawlFrame = false;
        }

        Vec3d wallVector3 = client.player.getPos().offset(client.player.getHorizontalFacing().rotateYClockwise().getOpposite()  , 0.3);
        Vec3d wallVector4 = client.player.getPos().offset(client.player.getHorizontalFacing().rotateYClockwise(), 0.3);

        BlockHitResult hit3 = client.world.raycast( // Raycast Shooter
                new RaycastContext(
                        // raycast shoots thin box from center of player torso in the direction of Second Corner
                        new Vec3d(wallVector3.getX()-0.6, client.player.getBoundingBox().minY, wallVector3.getZ()-0.6), // First Corner
                        new Vec3d(wallVector3.getX()+0.6, client.player.getBoundingBox().maxY, wallVector3.getZ()+0.6), // Second Corner
                        RaycastContext.ShapeType.COLLIDER, // ShapeType
                        RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
        if (hit3.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
            BlockHitResult blockhit3 = hit3; // sets detected block
            wall3 = true;
        } else {
            wall3 = false;
        }
        BlockHitResult hit4 = client.world.raycast( // Raycast Shooter
                new RaycastContext(
                        // raycast shoots thin box from center of player torso in the direction of Second Corner
                        new Vec3d(wallVector4.getX()-0.6, client.player.getBoundingBox().minY, wallVector4.getZ()-0.6), // First Corner
                        new Vec3d(wallVector4.getX()+0.6, client.player.getBoundingBox().maxY, wallVector4.getZ()+0.6), // Second Corner
                        RaycastContext.ShapeType.COLLIDER, // ShapeType
                        RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
        if (hit4.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
            BlockHitResult blockhit4 = hit4; // sets detected block
            wall4 = true;
        } else {
            wall4 = false;
        }

        if (RainworldMechanicsClient.crawling) {
            client.player.sendMessage(Text.of("Wall3: "+wall3+" Wall4: "+wall4), true);
        }

        if (wall3 && wall4 && RainworldMechanicsClient.crawling) {
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
    }
}
