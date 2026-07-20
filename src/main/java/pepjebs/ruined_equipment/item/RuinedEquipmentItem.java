package pepjebs.ruined_equipment.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;
import pepjebs.ruined_equipment.RuinedEquipmentMod;
import pepjebs.ruined_equipment.utils.RuinedEquipmentUtils;

import java.util.List;

public class RuinedEquipmentItem extends Item {

    public RuinedEquipmentItem(Settings settings) {
        super(settings);
    }

    @Override
    public String getTranslationKey() {
        return "item.ruined_equipment.ruined_prefix";
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        appendRuinedTooltip(stack, tooltip);
    }

    public static void appendRuinedTooltip(
            ItemStack stack,
            List<Text> tooltip) {
        NbtCompound custom = RuinedEquipmentUtils.getCustomData(stack);
        if (custom.contains(RuinedEquipmentMod.RUINED_MAX_ENCHT_TAG)
                && custom.getBoolean(RuinedEquipmentMod.RUINED_MAX_ENCHT_TAG)) {
            tooltip.add(Text.translatable("item.ruined_equipment.ruined_upgrading").formatted(Formatting.GRAY));
        }
        if (stack.getItem() == Registries.ITEM.get(Identifier.of(RuinedEquipmentMod.MOD_ID, "ruined_shield"))
                && stack.contains(DataComponentTypes.BANNER_PATTERNS)) {
            tooltip.add(Text.translatable("item.ruined_equipment.ruined_shield.banner")
                    .formatted(Formatting.GRAY)
                    .formatted(Formatting.ITALIC));
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getName() {
        // Get existing text
        MutableText supered = super.getName().copyContentOnly();
        // Append vanilla item's name
        Item vanillaItem = RuinedEquipmentItems.getVanillaItemMap().get(this);
        supered = supered.append(Text.translatable(vanillaItem.getTranslationKey()));
        return supered;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getName(ItemStack stack) {
        // Get existing text
        MutableText supered = super.getName().copyContentOnly();
        // Append vanilla item's name
        Item vanillaItem = RuinedEquipmentItems.getVanillaItemMap().get(this);
        supered = supered.append(Text.translatable(vanillaItem.getTranslationKey()));
        // Add the Aqua text if it has a glint
        if (hasGlint(stack)) {
            supered = supered.formatted(Formatting.AQUA);
        }
        return supered;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasRuinedGlint(stack);
    }

    public static boolean hasRuinedGlint(ItemStack stack) {
        return stack != null && !stack.getEnchantments().isEmpty();
    }

    public static Rarity getRuinedRarity(ItemStack stack) {
        return hasRuinedGlint(stack) ? Rarity.RARE : Rarity.COMMON;
    }
}