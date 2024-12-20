package pencil.mechanics.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pencil.mechanics.RainworldMechanics;
import pencil.mechanics.RainworldMechanicsClient;
import pencil.mechanics.player.movement.Crawling;

@Mixin(LivingEntity.class)
public abstract class RainworldMechanicsPlayerMixin extends Entity {

    @Shadow public abstract @Nullable EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Shadow public abstract ItemStack eatFood(World world, ItemStack stack);

    public RainworldMechanicsPlayerMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.isPlayer()) {
            if (RainworldMechanicsClient.clientPlayer != null && this.isCrawling()) {
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(RainworldMechanicsClient.moveSpeed*2);
            } else {
                this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(RainworldMechanicsClient.moveSpeed);
            }
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void jump(CallbackInfo ci) {
        if (Crawling.pressed) {
            ci.cancel();
        }
    }

    @Inject(method = "getJumpVelocity", at = @At("HEAD"), cancellable = true)
    private void jumpVelocity(CallbackInfoReturnable<Float> cir) {
        if (Crawling.pressed) {
            RainworldMechanicsClient.playerEntity.sendMessage(Text.of("jumped"), true);
            cir.setReturnValue(0f);
            cir.cancel();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (this.isPlayer()) {
            nbt.putInt("food_level", RainworldMechanicsClient.lastFoodLevel);
            nbt.putInt("karma_level", RainworldMechanicsClient.lastKarmaLevel);
            nbt.putBoolean("starving", RainworldMechanicsClient.starving);
            nbt.putBoolean("shielded", RainworldMechanicsClient.karmaShielded);
            nbt.putInt("stored_item", Item.getRawId(RainworldMechanicsClient.lastStoredItem.getItem()));
        }
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (this.isPlayer()) {
            RainworldMechanicsClient.foodLevel = nbt.getInt("food_level");
            RainworldMechanicsClient.lastFoodLevel = nbt.getInt("food_level");
            RainworldMechanicsClient.karmaLevel = nbt.getInt("karma_level");
            RainworldMechanicsClient.lastKarmaLevel = nbt.getInt("karma_level");
            RainworldMechanicsClient.starving = nbt.getBoolean("starving");
            RainworldMechanicsClient.karmaShielded = nbt.getBoolean("shielded");
            RainworldMechanicsClient.lastStoredItem = Item.byRawId(nbt.getInt("stored_item")).getDefaultStack();
        }
    }
}
