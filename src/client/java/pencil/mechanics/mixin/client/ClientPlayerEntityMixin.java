package pencil.mechanics.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pencil.mechanics.RainworldMechanicsClient;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Shadow @Final protected MinecraftClient client;
    private double dynamicRunSpeed = 0.1; // Base speed
    private double jumpBoost = 0; // Jump boost value
    private double adrenaline = 0.5; // Example adrenaline level (0 to 1)
    private boolean onSlope = false; // Example placeholder for slope detection
    private boolean isWallSliding = false; // Placeholder for wall sliding detection
    private boolean canJump = true; // Check if the player can jump

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void modifyMovementSpeed(CallbackInfo ci) {

        if (RainworldMechanicsClient.playerEntity != null) {
            // Adjust dynamic run speed based on adrenaline
            dynamicRunSpeed = 0.1 * (1 + adrenaline * 0.5); // Increase base speed by adrenaline factor

            // Apply surface friction when not moving
            if (RainworldMechanicsClient.playerEntity.input.movementForward == 0 && RainworldMechanicsClient.playerEntity.input.movementSideways == 0) {
                dynamicRunSpeed *= 0.9; // Apply frictional reduction
            }

            if (RainworldMechanicsClient.crawling) {
                dynamicRunSpeed *= 2;
            }

            // Example of slope handling (simple placeholder)
            if (onSlope) {
                dynamicRunSpeed *= 1.2; // Boost speed slightly on slopes
            }

            // Check wall sliding to slow down horizontal speed
            if (isWallSliding) {
                dynamicRunSpeed *= 0.7; // Reduce speed when sliding down a wall
            }

            if (RainworldMechanicsClient.playerEntity != null) {
                // Apply dynamic run speed to the player's movement speed attribute
                RainworldMechanicsClient.playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(dynamicRunSpeed);
            }

            // Jump boost logic
            if (RainworldMechanicsClient.playerEntity != null && RainworldMechanicsClient.playerEntity.input.jumping && canJump) {
                jumpBoost = 0.3 + adrenaline * 0.1; // Adjust jump boost based on adrenaline
                Vec3d currentVelocity = RainworldMechanicsClient.playerEntity.getVelocity();
                RainworldMechanicsClient.playerEntity.setVelocity(currentVelocity.x, jumpBoost, currentVelocity.z);
                canJump = false; // Disable jump until grounded
            }

            // Reset jump when grounded (simple check)
            if (RainworldMechanicsClient.playerEntity != null && RainworldMechanicsClient.playerEntity.isOnGround()) {
                canJump = true;
            }
        }
    }
}
