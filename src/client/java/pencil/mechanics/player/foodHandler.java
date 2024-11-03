package pencil.mechanics.player;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.RainworldMechanicsClient;

public class foodHandler {
    public static void tick(MinecraftClient client) {
        if (client.player.getMainHandStack().getItem().getFoodComponent() != null && RainworldMechanicsClient.eating) {
            if (client.player.getMainHandStack().getItem().getFoodComponent().isSnack()){
                client.player.getHungerManager().setFoodLevel(19);
                ++RainworldMechanicsClient.foodLevel;

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(0);
                ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CLEAR_SLOT_PACKET_ID, buf);
                client.player.getInventory().setStack(0 , ItemStack.EMPTY);
                client.player.getMainHandStack().setCount(0);

                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 1);
            } else {
                client.player.getHungerManager().setFoodLevel(19);
                RainworldMechanicsClient.foodLevel += 2;

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(0);
                ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CLEAR_SLOT_PACKET_ID, buf);
                client.player.getInventory().setStack(0 , ItemStack.EMPTY);
                client.player.getMainHandStack().setCount(0);

                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 1);
            }
            RainworldMechanicsClient.eating = false;
        }
        if (client.player.getOffHandStack().getItem().getFoodComponent()  != null && RainworldMechanicsClient.eating) {
            if (client.player.getOffHandStack().getItem().getFoodComponent().isSnack()) {
                client.player.getHungerManager().setFoodLevel(19);
                ++RainworldMechanicsClient.foodLevel;

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(40);
                ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CLEAR_SLOT_PACKET_ID, buf);
                client.player.getInventory().setStack(40 , ItemStack.EMPTY);
                client.player.getMainHandStack().setCount(0);

                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 1);
            } else {
                client.player.getHungerManager().setFoodLevel(19);
                RainworldMechanicsClient.foodLevel += 2;

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(40);
                ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CLEAR_SLOT_PACKET_ID, buf);
                client.player.getInventory().setStack(40 , ItemStack.EMPTY);
                client.player.getMainHandStack().setCount(0);

                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 1);
            }
            RainworldMechanicsClient.eating = false;
        }
    }
}
