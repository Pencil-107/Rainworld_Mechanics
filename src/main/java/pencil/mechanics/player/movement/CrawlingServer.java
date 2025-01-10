package pencil.mechanics.player.movement;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class CrawlingServer {
    public static void main(PlayerEntity client) {
        client.setPose(EntityPose.SWIMMING);
        client.setVelocity(client.getVelocity().getX(), -100, client.getVelocity().getZ());
    }
}
