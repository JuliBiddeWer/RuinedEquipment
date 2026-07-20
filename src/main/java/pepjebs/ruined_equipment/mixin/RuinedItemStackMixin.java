package pepjebs.ruined_equipment.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.ruined_equipment.utils.RuinedEquipmentUtils;

@Mixin(ItemStack.class)
public abstract class RuinedItemStackMixin {

    @Inject(
            method = "damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V",
            at = @At("HEAD"))
    private void ruined$captureBreakingStack(
            int amount, LivingEntity entity, EquipmentSlot slot, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        if (!self.isDamageable() || amount <= 0) return;
        // Unbreaking can only reduce the applied damage, so a break can only happen
        // when the pre-damage durability plus the raw amount reaches max damage.
        // Skip the snapshot otherwise to avoid storing one per damaging hit.
        if (self.getDamage() + amount < self.getMaxDamage()) return;
        ItemStack snapshot = self.copy();
        // Mark the snapshot as fully broken so isVanillaItemStackBreaking matches it.
        snapshot.setDamage(snapshot.getMaxDamage());
        RuinedEquipmentUtils.PENDING_BREAKING.put(
                entity.getUuid(), new Pair<>(snapshot, slot));
    }
}