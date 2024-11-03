package pencil.mechanics;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pencil.mechanics.block.pipes.*;
import pencil.mechanics.init.BlockInit;
import pencil.mechanics.init.EntityTypeInit;
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

	public static Block PIPE_BLOCK = BlockInit.PIPE_BLOCK;
	public static BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY;

	public static Block PIPE_ENTRANCE = BlockInit.PIPE_ENTRANCE;

	public static Block TELEPIPE_BLOCK;
	public static BlockEntityType<TelePipeBlockEntity> TELEPIPE_BLOCK_ENTITY;

	@Override
	public void onInitialize() {

		// Crawls
		ServerSidePacketRegistry.INSTANCE.register(CRAWL_PACKET_ID, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> { // Execute on the main thread
				packetContext.getPlayer().setBoundingBox(Box.of(packetContext.getPlayer().getBoundingBox().getCenter(), 0.6f, 0.6f,1.8f));
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
					//commandManager.executeWithPrefix(packetContext.getPlayer().getCommandSource(), "/cpm setskin -f "+packetContext.getPlayer().getEntityName()+" "+ modelID);
				}
			});
		});

		ServerSidePacketRegistry.INSTANCE.register(SLUGCAT_MISC_PACKET_ID, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> { // Execute on the main thread

			});
		});

		itemGroupInit.load();
		BlockInit.load();
		ItemInit.load();
		EntityTypeInit.load();

		// Register the custom item class for PIPE_BLOCK and PIPE_ENTRANCE
		Registry.register(Registries.ITEM, new Identifier("rw-mechanics", "pipe_block"), new PipeBlockItem(PIPE_BLOCK, new FabricItemSettings()));
		Registry.register(Registries.ITEM, new Identifier("rw-mechanics", "pipe_entrance"), new BlockItem(PIPE_ENTRANCE, new FabricItemSettings()));

		PIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("rw-mechanics", "pipe_block_entity"), FabricBlockEntityTypeBuilder.create(PipeBlockEntity::new, PIPE_BLOCK).build(null));

		// Register the TelePipe block and its block entity
		TELEPIPE_BLOCK = Registry.register(Registries.BLOCK, new Identifier("rw-mechanics", "telepipe_block"), new TelePipeBlock(getSetting()));
		Registry.register(Registries.ITEM, new Identifier("rw-mechanics", "telepipe_block"), new BlockItem(TELEPIPE_BLOCK, new FabricItemSettings()));
		TELEPIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("rw-mechanics", "telepipe_block_entity"), FabricBlockEntityTypeBuilder.create(TelePipeBlockEntity::new, TELEPIPE_BLOCK).build(null));

		TransportManager.initialize();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	private static AbstractBlock.Settings getSetting() {
		AbstractBlock.ContextPredicate never = (state, world, pos) -> false;
		return FabricBlockSettings.copyOf(Blocks.GLASS).suffocates(never).blockVision(never);
	}
}