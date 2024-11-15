package pencil.mechanics;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class VelocityFallPacket {
    public static final Identifier ID = new Identifier("modid", "fall_velocity_packet");

    private final double velocityY;

    public VelocityFallPacket(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getVelocityY() {
        return velocityY;
    }

    // Converts the packet data to a PacketByteBuf
    public PacketByteBuf toBytes() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(velocityY);
        return buf;
    }

    // Reads the packet data from a PacketByteBuf
    public static VelocityFallPacket fromBytes(PacketByteBuf buf) {
        return new VelocityFallPacket(buf.readDouble());
    }
}
