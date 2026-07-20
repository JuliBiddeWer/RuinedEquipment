package pepjebs.ruined_equipment.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;
import pepjebs.ruined_equipment.utils.RuinedEquipmentUtils;

import java.util.List;

public class RuinedAshesItem extends Item {
    public RuinedAshesItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        Identifier id = RuinedEquipmentUtils.getItemKeyIdFromItemStack(stack);
        if (id == null) return;
        Item item = Registries.ITEM.get(id);
        if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            tooltip.add(Text.translatable(this.getTranslationKey()).formatted(Formatting.GRAY));
        }
        tooltip.add(Text.translatable(item.getTranslationKey(stack)).formatted(Formatting.GRAY));
        RuinedEquipmentItem.appendRuinedTooltip(stack, tooltip);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return RuinedEquipmentItem.hasRuinedGlint(stack);
    }
}