package pencil.mechanics;

import com.sun.jna.platform.win32.WinNT;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pencil.mechanics.block.pipes.*;
import pencil.mechanics.commands.MechanicsCommands;
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
	public static final Identifier STUN_PLAYER_PACKET_ID = Identifier.of("rw-mechanics", "stun_player");
	public static final Identifier EDIT_BLOCK_ID = Identifier.of("rw-mechanics", "edit_block");

	public static Block PIPE_BLOCK = BlockInit.PIPE_BLOCK;
	public static BlockEntityType<PipeBlockEntity> PIPE_BLOCK_ENTITY;

	public static Block PIPE_ENTRANCE = BlockInit.PIPE_ENTRANCE;

	public static Block TELEPIPE_BLOCK;
	public static BlockEntityType<TelePipeBlockEntity> TELEPIPE_BLOCK_ENTITY;

	// Sounds
	public static final Identifier PIPE_LOOP_ID = Identifier.of("rw-mechanics", "pipe_loop");
	public static SoundEvent PIPE_LOOP_EVENT = SoundEvent.of(PIPE_LOOP_ID);
	public static final Identifier JUMP_ID = Identifier.of("rw-mechanics", "player_jump");
	public static SoundEvent JUMP_EVENT = SoundEvent.of(JUMP_ID);
	public static final Identifier WALL_JUMP_ID = Identifier.of("rw-mechanics", "player_wall_jump");
	public static SoundEvent WALL_JUMP_EVENT = SoundEvent.of(WALL_JUMP_ID);
	public static final Identifier WALL_SLIDE_ID = Identifier.of("rw-mechanics", "player_wall_slide");
	public static SoundEvent WALL_SLIDE_EVENT = SoundEvent.of(WALL_SLIDE_ID);


	private static final double LETHAL_VELOCITY = 60; // Threshold for lethal impact velocity in m/s
	private static final double STUN_VELOCITY = 35; // Threshold for stun velocity in m/s

	@Override
	public void onInitialize() {

		Registry.register(Registries.SOUND_EVENT, RainworldMechanics.PIPE_LOOP_ID, PIPE_LOOP_EVENT);
		Registry.register(Registries.SOUND_EVENT, RainworldMechanics.JUMP_ID, JUMP_EVENT);
		Registry.register(Registries.SOUND_EVENT, RainworldMechanics.WALL_JUMP_ID, WALL_JUMP_EVENT);
		Registry.register(Registries.SOUND_EVENT, RainworldMechanics.WALL_SLIDE_ID, WALL_SLIDE_EVENT);

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

		ServerSidePacketRegistry.INSTANCE.register(EDIT_BLOCK_ID, (packetContext, attachedData) -> {
			BlockPos pos = attachedData.readBlockPos();
			int state = attachedData.readInt();
			packetContext.getTaskQueue().execute(() -> {
				BlockState blockState = packetContext.getPlayer().getWorld().getBlockState(pos);

				if (state == 0) {
					if (blockState.get(PipeEntrance.VARIATION).getValue() == 0) {
						packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.VARIATION, PipeEntrance.Variation.TWO));
					} else if (blockState.get(PipeEntrance.VARIATION).getValue() == 1) {
						packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.VARIATION, PipeEntrance.Variation.THREE));
					} else if (blockState.get(PipeEntrance.VARIATION).getValue() == 2) {
						packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.VARIATION, PipeEntrance.Variation.FOUR));
					} else if (blockState.get(PipeEntrance.VARIATION).getValue() == 3) {
						packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.VARIATION, PipeEntrance.Variation.FIVE));
					} else if (blockState.get(PipeEntrance.VARIATION).getValue() == 4) {
						packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.VARIATION, PipeEntrance.Variation.ONE));
					}
				}

				if (state == 1) {
					packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.LIGHT_ONE, !blockState.get(PipeEntrance.LIGHT_ONE)));
				}
				if (state == 2) {
					packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.LIGHT_TWO, !blockState.get(PipeEntrance.LIGHT_TWO)));
				}
				if (state == 3) {
					packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.LIGHT_THREE, !blockState.get(PipeEntrance.LIGHT_THREE)));
				}
				if (state == 4) {
					packetContext.getPlayer().getWorld().setBlockState(pos, blockState.with(PipeEntrance.LIGHT_FOUR, !blockState.get(PipeEntrance.LIGHT_FOUR)));
				}
			});
		});

		ServerSidePacketRegistry.INSTANCE.register(SLUGCAT_MISC_PACKET_ID, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> { // Execute on the main thread
				PacketByteBuf buf = PacketByteBufs.create(); // Create Packet
				buf.writeBoolean(true);
				ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), RainworldMechanics.SLUGCAT_MISC_PACKET_ID, buf);
			});
		});

		itemGroupInit.load();
		BlockInit.load();
		ItemInit.load();
		EntityTypeInit.load();
		MechanicsCommands.main();

		// Register the custom item class for PIPE_BLOCK and PIPE_ENTRANCE
		Registry.register(Registries.ITEM, new Identifier("rw-mechanics", "pipe_block"), new PipeBlockItem(PIPE_BLOCK, new FabricItemSettings()));
		Registry.register(Registries.ITEM, new Identifier("rw-mechanics", "pipe_entrance"), new BlockItem(PIPE_ENTRANCE, new FabricItemSettings()));

		PIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("rw-mechanics", "pipe_block_entity"), FabricBlockEntityTypeBuilder.create(PipeBlockEntity::new, PIPE_BLOCK).build(null));

		// Register the TelePipe block and its block entity
		TELEPIPE_BLOCK = Registry.register(Registries.BLOCK, new Identifier("rw-mechanics", "telepipe_block"), new TelePipeBlock(getSetting()));
		Registry.register(Registries.ITEM, new Identifier("rw-mechanics", "telepipe_block"), new BlockItem(TELEPIPE_BLOCK, new FabricItemSettings()));
		TELEPIPE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("rw-mechanics", "telepipe_block_entity"), FabricBlockEntityTypeBuilder.create(TelePipeBlockEntity::new, TELEPIPE_BLOCK).build(null));

		TransportManager.initialize();

		ServerPlayNetworking.registerGlobalReceiver(VelocityFallPacket.ID, (server, player, handler, buf, responseSender) -> {
			VelocityFallPacket packet = VelocityFallPacket.fromBytes(buf);

			server.execute(() -> {
				double clientVelocityY = packet.getVelocityY();

				// convert from 20 ticks per second to 40 ticks per second

				double impactVelocity = Math.abs(clientVelocityY * 20);

				// Handle fall damage based on calculated impact velocity
				applyFallDamage(player, impactVelocity);
			});
		});
	}

	private void applyFallDamage(ServerPlayerEntity player, double impactVelocity) {
		// Rolling check, using a hypothetical method to check if rolling (you might need a custom flag on the player)
		boolean isRolling = isPlayerRolling(player);

		if (impactVelocity > LETHAL_VELOCITY) {
			// Death condition
			player.damage(player.getWorld().getDamageSources().fall(), Float.MAX_VALUE);
			//LOGGER.info("Player died due to high impact velocity.");
		} else if (impactVelocity > STUN_VELOCITY) {
			// Stun condition
			float stunDuration = lerpMap(impactVelocity, STUN_VELOCITY, LETHAL_VELOCITY, 40f, 140f) / 2;
			PacketByteBuf buf = PacketByteBufs.create(); // Create Packet
			buf.writeFloat(stunDuration);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, RainworldMechanics.STUN_PLAYER_PACKET_ID, buf);
			player.sendMessage(Text.of("Stunned for "+stunDuration), true);
			//LOGGER.info("Player stunned for " + stunDuration + " ticks due to fall.");
		} else if (isRolling) {
			//LOGGER.info("Player avoided damage due to rolling.");
		} else {
			//LOGGER.info("Minor fall impact for player.");
		}
	}

	private boolean isPlayerRolling(ServerPlayerEntity player) {
		return false;
	}

	// Linear interpolation method to map value between two ranges
	private static float lerpMap(double value, double min1, double max1, double min2, double max2) {

		value = Math.max(min1, Math.min(max1, value));
		return (float) ((value - min1) / (max1 - min1) * (max2 - min2) + min2);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	private static AbstractBlock.Settings getSetting() {
		AbstractBlock.ContextPredicate never = (state, world, pos) -> false;
		return FabricBlockSettings.copyOf(Blocks.GLASS).suffocates(never).blockVision(never);
	}
}