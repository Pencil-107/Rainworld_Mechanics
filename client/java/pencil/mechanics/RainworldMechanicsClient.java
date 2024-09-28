package pencil.mechanics;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.PacketByteBuf;
import pencil.mechanics.init.BlockInit;
import pencil.mechanics.player.Keybinds;

public class RainworldMechanicsClient implements ClientModInitializer {

	public static MinecraftClient clientPlayer = null;
	public static ClientPlayerEntity playerEntity;

	public static float moveSpeed = 0.1f;

	// Food variables
	public static int foodLevel = 0;
	public static int foodRequired = 8;
	public static int lastFoodLevel = 0;
	public static int maxFoodLevel = 14;
	public static boolean eating = false;
	public static boolean starving = false;

	public static boolean cycling = false;
	// has slugcat survived this cycle so far
	public static boolean survived = true;

	// Karma variables
	public static int karmaLevel = 0;
	public static int lastKarmaLevel = 0;
	public static int maxKarmaLevel = 9;
	public static boolean karmaShielded = false;
	public static boolean karmaScren = false;

	// Player Model Variables
	public static String[] modelList = { "UwsBAQQjInA6MDllZmM2NzA3MGM5NDQ4MDhkYzQ1ZWJjOWU1Y2Q4MjkAAAnW", "UwsBAQQjInA6Mzk2NGJkOWFhYmNhNDYzNDhmN2IzMTc2Yjg5Nzg5MWIAAAmk", "UwsBAQQjInA6OWI2ZDdkZmIwN2E4NDRmMjhmM2U4OTFiM2FlMDJiMjYAAAn3" };
	public static float selectedModel = 0;
	public static String modelID = modelList[0];
	public static String lastModelID = modelID;

	public static boolean jumpHeld = false;
	public static boolean crawling = false;
	public static boolean climbJumping = false;
	public static boolean miscSet = false;

	private static KeyBinding crawlKey = Keybinds.crawlKey;
	private static KeyBinding grabKey = Keybinds.grabKey;
	private static KeyBinding interactKey = Keybinds.interactKey;
	private static KeyBinding dropKey = Keybinds.dropKey;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			clientPlayer = client;
			//crawlKey = client.options.sneakKey;
			if (client.player != null && !client.player.isCreative() && !client.player.isSpectator()) {

				playerEntity = MinecraftClient.getInstance().player;
				Keybinds.main();
				if (!crawlKey.isPressed() && !client.player.isSubmergedInWater()) {
					pencil.mechanics.player.movement.WallMovement.main();
					pencil.mechanics.player.movement.PoleClimbing.main(client, grabKey);
					crawling = false;
					client.player.setNoGravity(false);
				} else {
					if (crawlKey.isPressed() && !client.player.isSubmergedInWater()) {
						crawling = true;
						pencil.mechanics.player.movement.Crawling.main(client);
						//client.player.setPose(EntityPose.SWIMMING);
						ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.CRAWL_PACKET_ID, PacketByteBufs.empty());
					}
				}
				if (modelID != lastModelID) {
					PacketByteBuf bufModel = PacketByteBufs.create();

					bufModel.writeString(modelID);

					// Misc Packet Handler
					// disables normal fall damage
					ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.PLAYER_MODEL_PACKET_ID, bufModel);
					lastModelID = modelID;
				}
				if (!miscSet) {
					PacketByteBuf buf = PacketByteBufs.create();

					// Misc Packet Handler
					// disables normal fall damage
					ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.SLUGCAT_MISC_PACKET_ID, buf);

					PacketByteBuf bufModel = PacketByteBufs.create();

					bufModel.writeString(modelID);

					// Misc Packet Handler
					// disables normal fall damage
					ClientSidePacketRegistry.INSTANCE.sendToServer(RainworldMechanics.PLAYER_MODEL_PACKET_ID, bufModel);
					miscSet = true;
				}
			} else if (client.player != null){
				Keybinds.disable();
			}
		});

	}
}