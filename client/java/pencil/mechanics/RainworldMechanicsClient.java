package pencil.mechanics;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pencil.mechanics.gui.screen.KarmaScreen;
import pencil.mechanics.gui.screen.PlayerModelScreen;
import pencil.mechanics.init.BlockInit;
import pencil.mechanics.player.Keybinds;
import pencil.mechanics.render.block.PipeBlockEntityRenderer;

import java.util.Objects;

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
	public static boolean crawlFrame = false;
	public static boolean climbJumping = false;
	public static boolean miscSet = false;

	// stored item variables
	public static ItemStack lastStoredItem = ItemStack.EMPTY;
	public static ItemStack storedItem = ItemStack.EMPTY;
	public static float storeTime = 40;
	public static float storeTimeMax = 40;
	public static float lastTime = storeTime;

	private static KeyBinding crawlKey = Keybinds.crawlKey;
	private static KeyBinding grabKey = Keybinds.grabKey;
	private static KeyBinding interactKey = Keybinds.interactKey;
	private static KeyBinding dropKey = Keybinds.dropKey;

	private static KeyBinding testingKey1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.slugcatmovement.test1", // The translation key of the keybinding's name
			InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
			GLFW.GLFW_KEY_Y, // The keycode of the key
			"category.slugcatmovement.rainworld" // The translation key of the keybinding's category.
	));
	private static KeyBinding testingKey2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.slugcatmovement.test2", // The translation key of the keybinding's name
			InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
			GLFW.GLFW_KEY_U, // The keycode of the key
			"category.slugcatmovement.rainworld" // The translation key of the keybinding's category.
	));

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			clientPlayer = client;
			//crawlKey = client.options.sneakKey;

			if (client.player != null && !client.player.isCreative() && !client.player.isSpectator()) {


				if (testingKey1.wasPressed() && !cycling) {
					cycling = true;
				}

				if (cycling == true) {
					if (!karmaScren) {
						onHibernated();
						client.setScreen(new KarmaScreen(Text.empty()));
						karmaScren = true;
					}
				}
				if (testingKey2.wasPressed()) {
					client.setScreen(new PlayerModelScreen(Text.empty()));
				}

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
				client.options.inventoryKey.setBoundKey(client.options.inventoryKey.getDefaultKey());
			}
		});

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererRegistry.register(RainworldMechanics.PIPE_BLOCK_ENTITY, PipeBlockEntityRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockInit.PIPE_BLOCK);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockInit.PIPE_ENTRANCE);

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> view != null && view.getBlockEntityRenderData(pos) instanceof Integer integer ? integer : 0x1f1f1f, BlockInit.PIPE_BLOCK);
	}

	public static void onHibernated() {
		if (foodLevel >= foodRequired && survived && !starving) {
			foodLevel = foodLevel-foodRequired;
			if (karmaLevel < maxKarmaLevel) {
				++karmaLevel;
			}
			lastStoredItem = storedItem;
			lastFoodLevel = foodLevel;
		} else if (survived && foodLevel < foodRequired && !starving) {
			foodLevel = 0;
			lastFoodLevel = foodLevel;
			lastStoredItem = storedItem;
			starving = true;
		} else if (survived && foodLevel >= maxFoodLevel && starving) {
			foodLevel = 0;
			lastFoodLevel = foodLevel;
			lastStoredItem = storedItem;
			starving = false;
		} else if (survived && foodLevel <= maxFoodLevel && starving) {
			if (karmaLevel > 0) {
				--karmaLevel;
			}
			survived = false;
		}
		if (!survived) {
			foodLevel = lastFoodLevel;
			if (karmaLevel > 0 && !karmaShielded) {
				--karmaLevel;
			} else {
				karmaShielded = false;
			}
		}
	}

	public static void playerDeath() {
		survived = false;
	}
}