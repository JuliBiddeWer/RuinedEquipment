package pepjebs.ruined_equipment.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.ruined_equipment.RuinedEquipmentMod;
import pepjebs.ruined_equipment.utils.RuinedEquipmentUtils;

@Mixin(LivingEntity.class)
public class RuinedLivingEntityMixin {

    private LivingEntity livingEntity = ((LivingEntity) (Object) this);

    @Inject(method = "sendEquipmentBreakStatus", at = @At("RETURN"))
    private void onSendEquipmentBreakStatus(Item item, EquipmentSlot slot, CallbackInfo ci) {
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) livingEntity;
            Pair<ItemStack, EquipmentSlot> snapshot =
                    RuinedEquipmentUtils.PENDING_BREAKING.remove(serverPlayer.getUuid());
            ItemStack breakingItemStack;
            EquipmentSlot breakSlot;
            if (snapshot != null) {
                breakingItemStack = snapshot.getLeft();
                breakSlot = snapshot.getRight();
            } else {
                breakingItemStack = serverPlayer.getEquippedStack(slot);
                breakSlot = slot;
            }
            boolean forceSet = breakSlot == EquipmentSlot.MAINHAND || breakSlot == EquipmentSlot.OFFHAND;
            if (RuinedEquipmentMod.CONFIG != null && !RuinedEquipmentMod.CONFIG.enableSetRuinedItemInHand) {
                forceSet = false;
            }
            RuinedEquipmentUtils.onSendEquipmentBreakStatusImpl(serverPlayer, breakingItemStack, forceSet);
        }
    }
}
