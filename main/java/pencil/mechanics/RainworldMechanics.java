package pencil.mechanics;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pencil.mechanics.init.BlockInit;
import pencil.mechanics.init.ItemInit;
import pencil.mechanics.init.itemGroupInit;

public class RainworldMechanics implements ModInitializer {
	public static final String MOD_ID = "rw-mechanics";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier SLUGCAT_MISC_PACKET_ID = Identifier.of("rw-mechanics", "slugcat_misc_player");
	public static final Identifier PLAYER_MODEL_PACKET_ID = Identifier.of("rw-mechanics", "player_model");
	public static final Identifier CRAWL_PACKET_ID = Identifier.of("rw-mechanics", "crawl_player");
	public static final Identifier DROP_PACKET_ID = Identifier.of("rw-mechanics", "drop_item");
	public static final Identifier CLEAR_SLOT_PACKET_ID = Identifier.of("rw-mechanics", "clear_slot");

	@Override
	public void onInitialize() {
		// Crawls
		ServerSidePacketRegistry.INSTANCE.register(CRAWL_PACKET_ID, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> { // Execute on the main thread
				pencil.mechanics.player.movement.CrawlingServer.main(packetContext.getPlayer());
			});
		});

		// Clears a specified slot
		ServerSidePacketRegistry.INSTANCE.register(CLEAR_SLOT_PACKET_ID, (packetContext, attachedData) -> {
			float slot = attachedData.readFloat();
			packetContext.getTaskQueue().execute(() -> {
				packetContext.getPlayer().getInventory().setStack((int) slot, ItemStack.EMPTY);
				if (slot == 0) {
					packetContext.getPlayer().getMainHandStack().setCount(0);
				} else if (slot == 40) {
					packetContext.getPlayer().getOffHandStack().setCount(0);
				}
			});
		});

		// Player Model Selector
		ServerSidePacketRegistry.INSTANCE.register(PLAYER_MODEL_PACKET_ID, (packetContext, attachedData) -> {
			String modelID = attachedData.readString();
			packetContext.getTaskQueue().execute(() -> { // Execute on the main thread
				if(packetContext.getPlayer() != null){
					CommandManager commandManager = packetContext.getPlayer().getServer().getCommandManager();
					commandManager.executeWithPrefix(packetContext.getPlayer().getCommandSource(), "/cpm setskin -f "+packetContext.getPlayer().getEntityName()+" "+ modelID);
				}
			});
		});

		ServerSidePacketRegistry.INSTANCE.register(SLUGCAT_MISC_PACKET_ID, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> { // Execute on the main thread
				if(packetContext.getPlayer() != null){
					CommandManager commandManager = packetContext.getPlayer().getServer().getCommandManager();
					commandManager.executeWithPrefix(packetContext.getPlayer().getCommandSource(), "/gamerule fallDamage false");
					commandManager.executeWithPrefix(packetContext.getPlayer().getCommandSource(), "/gamerule sendCommandFeedback false");
					commandManager.executeWithPrefix(packetContext.getPlayer().getCommandSource(), "/gamerule sendCommandFeedback false");
				}
			});
		});

		itemGroupInit.load();
		BlockInit.load();
		ItemInit.load();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}