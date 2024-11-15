package pencil.mechanics;

import com.mojang.datafixers.types.Type;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.io.File;

public class ConfigValues {
    public static float crawlJumpXMultiplier = 1.3f;
    public static float crawlJumpYMultiplier = 0.6f;
    public static float poleJumpXMultiplier = 0.5f;
    public static float poleJumpYMultiplier = 0.6f;
    public static float rainTimer = 500.0f;
    public static boolean rainEnabled = false;
}
