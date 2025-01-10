package pencil.mechanics.player.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.RainworldMechanicsClient;

public class WallMovement {

    public static MinecraftClient client = RainworldMechanicsClient.clientPlayer;

    private static final double jumpHeight = 0.5;
    private static final double jumpDistance = 1.4;
    private static boolean wall1 = false;
    private static boolean wall2 = false;

    private static final double wallslideSpeed = -0.1;

    private static boolean boosted = false;
    private static boolean pressed = false;

    public static Vec3d wallCheckPos(Boolean reverse) {
        if (client.player == null) {
            return null;
        }
        if (reverse) {
            return client.player.getPos().offset(client.player.getHorizontalFacing().getOpposite(), 0.3);
        } else {
            return client.player.getPos().offset(client.player.getHorizontalFacing(), 0.3);
        }
    }

    private static BlockHitResult blockHit = null;
    private static BlockHitResult blockHit2 = null;

    public static void main() {

        Vec3d wallVector = client.player.getPos().offset(client.player.getHorizontalFacing().rotateYClockwise().getOpposite(), 0.3);
        BlockHitResult hit = client.world.raycast( // Raycast Shooter
                new RaycastContext(
                        // raycast shoots thin box from center of player torso in the direction of Second Corner
                        new Vec3d(wallVector.getX()-0.3, client.player.getBoundingBox().minY+0.3, wallVector.getZ()-0.3), // First Corner
                        new Vec3d(wallVector.getX()+0.3, client.player.getBoundingBox().maxY, wallVector.getZ()+0.3), // Second Corner
                        RaycastContext.ShapeType.COLLIDER, // ShapeType
                        RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
        if (hit.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
            blockHit = hit; // sets detected block
            wall1 = true;
        } else {
            wall1 = false;
        }
        Vec3d wallVector2 = client.player.getPos().offset(client.player.getHorizontalFacing().rotateYClockwise(), 0.3);
        BlockHitResult Hit2 = client.world.raycast( // Raycast Shooter
                new RaycastContext(
                        // raycast shoots thin box from center of player torso in the direction of Second Corner
                        new Vec3d(wallVector2.getX()-0.3, client.player.getBoundingBox().minY+0.3, wallVector2.getZ()-0.3), // First Corner
                        new Vec3d(wallVector2.getX()+0.3, client.player.getBoundingBox().maxY, wallVector2.getZ()+0.3), // Second Corner
                        RaycastContext.ShapeType.COLLIDER, // ShapeType
                        RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
        if (Hit2.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
            blockHit2 = Hit2; // sets detected block
            wall2 = true;
        } else {
            wall2 = false;
        }

        if(client.options.jumpKey.isPressed() && !client.player.isOnGround() && !pressed) {

            if (blockHit != null && wall1 && !wall2 && blockHit.getSide() != Direction.UP) { // checks for side of block
                Vec3d v3 = new Vec3d((client.player.getPos().getX()-wallVector.getX())*jumpDistance, jumpHeight, (client.player.getPos().getZ()-wallVector.getZ())*jumpDistance); // the Velocity Values
                client.player.setVelocity(v3); // changes Velocity
                client.player.playSound(RainworldMechanics.WALL_JUMP_EVENT, SoundCategory.PLAYERS, 1f, 0f);
                wall1 = false;
                wall2 = false;
            }
            if (blockHit2 != null && wall2 && !wall1 && blockHit2.getSide() != Direction.UP) { // checks for side of block
                Vec3d v3 = new Vec3d((client.player.getPos().getX()-wallVector2.getX())*jumpDistance, jumpHeight, (client.player.getPos().getZ()-wallVector2.getZ())*jumpDistance); // the Velocity Values
                client.player.setVelocity(v3); // changes Velocity
                client.player.playSound(RainworldMechanics.WALL_JUMP_EVENT, SoundCategory.PLAYERS, 1f, 0f);
                wall2 = false;
                wall1 = false;
            }
            pressed = true;
        } else if (pressed && !client.options.jumpKey.isPressed()) {
            pressed = false;
            wall2 = false;
            wall1 = false;
        } else if (!pressed && client.options.jumpKey.isPressed() && client.player.isOnGround()) {
            pressed = true;
        }

        if (!client.player.isOnGround() && client.player.getVelocity().getY() <= 0) {
            Vec3d wallVector3 = wallCheckPos(false);
            BlockHitResult Hit3 = client.world.raycast( // Raycast Shooter
                    new RaycastContext(
                            // raycast shoots thin box from center of player torso in the direction of Second Corner
                            new Vec3d(wallVector3.getX()-0.3, client.player.getBoundingBox().minY+0.3, wallVector3.getZ()-0.3), // First Corner
                            new Vec3d(wallVector3.getX()+0.3, client.player.getBoundingBox().maxY, wallVector3.getZ()+0.3), // Second Corner
                            RaycastContext.ShapeType.COLLIDER, // Shape Type
                            RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
            if (Hit3.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
                BlockHitResult blockHit3 = Hit3; // sets detected block
                if (blockHit3.getSide() != Direction.UP) { // checks for side of block
                    if (client.options.forwardKey.isPressed()) {
                        client.player.setMovementSpeed(0.01f); // movement speed modifier
                        client.player.setVelocity(client.player.getVelocity().getX(), wallslideSpeed, client.player.getVelocity().getZ() ); // Slowing down fall with velocity
                    }
                }
            }
        }
        if (client.player.getVelocity().getY() >= 0 && !boosted) {
            Vec3d wallVector3 = wallCheckPos(false);
            BlockHitResult Hit3 = client.world.raycast( // Raycast Shooter
                    new RaycastContext(
                            // raycast shoots thin box from center of player torso in the direction of Second Corner
                            new Vec3d(wallVector3.getX()-0.3, client.player.getBoundingBox().minY+0.3, wallVector3.getZ()-0.3), // First Corner
                            new Vec3d(wallVector3.getX()+0.3, client.player.getBoundingBox().maxY, wallVector3.getZ()+0.3), // Second Corner
                            RaycastContext.ShapeType.COLLIDER, // Shape Type
                            RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
            if (Hit3.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
                BlockHitResult blockHit3 = Hit3; // sets detected block
                if (blockHit3.getSide() != Direction.UP) { // checks for side of block
                    if (client.options.forwardKey.isPressed()) {
                        client.player.addVelocity(client.player.getVelocity().getX(), 0.6, client.player.getVelocity().getZ() ); // Slowing down fall with velocity
                        boosted = true;
                    }
                }
            }
        }

        if (client.player.isOnGround()) {
            boosted = false;
        }
    }
}