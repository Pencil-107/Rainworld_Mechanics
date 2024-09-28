package pencil.mechanics.player.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import pencil.mechanics.RainworldMechanicsClient;

public class WallMovement {

    public static MinecraftClient client = RainworldMechanicsClient.clientPlayer;

    private static final double jumpHeight = 0.5;
    private static final double jumpDistance = 1.4;

    private static final double wallslideSpeed = -0.1;

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

    public static void main() {
        if(client.options.jumpKey.wasPressed() && !client.player.isOnGround()) {
            Vec3d wallVector = wallCheckPos(true);
            BlockHitResult hit = client.world.raycast( // Raycast Shooter
                    new RaycastContext(
                            // raycast shoots thin box from center of player torso in the direction of Second Corner
                            new Vec3d(wallVector.getX()-0.3, client.player.getBoundingBox().minY+0.3, wallVector.getZ()-0.3), // First Corner
                            new Vec3d(wallVector.getX()+0.3, client.player.getBoundingBox().maxY, wallVector.getZ()+0.3), // Second Corner
                            RaycastContext.ShapeType.COLLIDER, // ShapeType
                            RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
            if (hit.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
                BlockHitResult blockHit = (BlockHitResult) hit; // sets detected block
                if (blockHit.getSide() != Direction.UP || blockHit.getSide() != Direction.DOWN) { // checks for side of block
                    Vec3d v3 = new Vec3d((client.player.getPos().getX()-wallVector.getX())*jumpDistance, jumpHeight, (client.player.getPos().getZ()-wallVector.getZ())*jumpDistance); // the Velocity Values
                    client.player.setVelocity(v3); // changes Velocity
                }
            }
        }

        if (!client.player.isOnGround() && client.player.getVelocity().getY() <= 0) {
            Vec3d wallVector = wallCheckPos(false);
            BlockHitResult hit = client.world.raycast( // Raycast Shooter
                    new RaycastContext(
                            // raycast shoots thin box from center of player torso in the direction of Second Corner
                            new Vec3d(wallVector.getX()-0.3, client.player.getBoundingBox().minY+0.3, wallVector.getZ()-0.3), // First Corner
                            new Vec3d(wallVector.getX()+0.3, client.player.getBoundingBox().maxY, wallVector.getZ()+0.3), // Second Corner
                            RaycastContext.ShapeType.COLLIDER, // Shape Type
                            RaycastContext.FluidHandling.NONE, client.player)); // extra Variables
            if (hit.getType() == HitResult.Type.BLOCK) { // check if the detected thing is a block
                BlockHitResult blockHit = hit; // sets detected block
                if (blockHit.getSide() != Direction.UP || blockHit.getSide() != Direction.DOWN) { // checks for side of block
                    if (client.options.forwardKey.isPressed()) {
                        client.player.setMovementSpeed(0.01f); // movement speed modifier
                        client.player.setVelocity(client.player.getVelocity().getX(), wallslideSpeed, client.player.getVelocity().getZ() ); // Slowing down fall with velocity
                    }
                }
            }
        }
    }
}