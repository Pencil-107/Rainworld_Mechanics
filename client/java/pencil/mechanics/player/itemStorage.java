package pencil.mechanics.player;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pencil.mechanics.RainworldMechanics;

import java.util.Arrays;

public class itemStorage {
    public static ItemStack storedItem = ItemStack.EMPTY;
    public static float storeTime = 40;
    public static float storeTimeMax = 40;
    public static float lastTime = storeTime;

    public static void tick(MinecraftClient client) {
        storeTime--;
        client.player.getInventory().selectedSlot = 1;
        client.player.getInventory().selectedSlot = 0;
        if (storeTime<=0) {
            if (!client.player.getInventory().getStack(0).isEmpty() && storedItem.isEmpty() && !client.player.getInventory().getStack(0).isFood()) {
                    client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 1);
                    storedItem = client.player.getInventory().getStack(0);
                    client.player.sendMessage(Text.literal("Stored From Mainhand "+storedItem), false);
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeFloat(0);
                    ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CLEAR_SLOT_PACKET_ID, buf);
                    client.player.getInventory().setStack(0 , ItemStack.EMPTY);
                    client.player.getMainHandStack().setCount(0);
            } else if (!client.player.getInventory().getStack(40).isEmpty() && storedItem.isEmpty() && !client.player.getInventory().getStack(40).isFood()) {
                    client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 1);
                    storedItem = client.player.getInventory().getStack(40);
                    client.player.sendMessage(Text.literal("Stored From Offhand"+storedItem), false);
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeFloat(40);
                    ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CLEAR_SLOT_PACKET_ID, buf);
                    client.player.getInventory().setStack(40, ItemStack.EMPTY);
                    client.player.getOffHandStack().setCount(0);
            } else if (client.player.getInventory().getStack(0).isEmpty() && !storedItem.isEmpty()) {
                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 4);
                client.player.getInventory().setStack(0, storedItem);
                storedItem = ItemStack.EMPTY;
                client.player.sendMessage(Text.literal("Withdrew To Mainhand "+client.player.getInventory().getStack(0)), false);
            } else if (client.player.getInventory().getStack(40).isEmpty() && !storedItem.isEmpty()) {
                client.player.playSound(SoundEvent.of(Identifier.of("minecraft", "entity.player.burp")), 1, 4);
                client.player.getInventory().setStack(40, storedItem);
                storedItem = ItemStack.EMPTY;
                client.player.sendMessage(Text.literal("Withdrew To Offhand "+client.player.getInventory().getStack(40)), false);
            } else if (!storedItem.isEmpty()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(storedItem);
                buf.writeBoolean(false);
                buf.writeBoolean(false);
                ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.DROP_PACKET_ID, buf);

                client.player.sendMessage(Text.literal("Withdrew To Ground "+storedItem), false);

                storedItem = ItemStack.EMPTY;
            }
            storeTime = storeTimeMax;
        }
    }
}
