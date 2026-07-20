package pepjebs.ruined_equipment.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import pepjebs.ruined_equipment.RuinedEquipmentMod;
import pepjebs.ruined_equipment.item.RuinedAshesItem;
import pepjebs.ruined_equipment.item.RuinedEquipmentItems;
import pepjebs.ruined_equipment.utils.RuinedEquipmentUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RuinedEquipmentCraftRepair extends SpecialCraftingRecipe {

    // Default bonus is 0.05
    public static final double REPAIR_MODIFIER = 0.07;

    public RuinedEquipmentCraftRepair(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput inv, World world) {
        if (RuinedEquipmentMod.CONFIG != null && !RuinedEquipmentMod.CONFIG.enableCraftingGridRuinedRepair)
            return false;
        ArrayList<ItemStack> craftingStacks = new ArrayList<>();
        for(int i = 0; i < inv.getSize(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                craftingStacks.add(inv.getStackInSlot(i));
            }
        }
        if (craftingStacks.size() == 2) {
            Set<Item> items = craftingStacks.stream().map(ItemStack::getItem).collect(Collectors.toSet());
            if (items.size() == 1) {
                for (Map.Entry<Item, Item> itemMap : RuinedEquipmentItems.getVanillaItemMap().entrySet()) {
                    if (items.contains(itemMap.getKey())) return true;
                }
                if (items.toArray()[0] == RuinedEquipmentMod.RUINED_ASHES_ITEM) {
                    Identifier one = RuinedEquipmentUtils.getItemKeyIdFromItemStack(craftingStacks.get(0));
                    Identifier two = RuinedEquipmentUtils.getItemKeyIdFromItemStack(craftingStacks.get(1));
                    return one != null && one.equals(two);
                }
            } else {
                for (Map.Entry<Item, Item> itemMap : RuinedEquipmentItems.getVanillaItemMap().entrySet()) {
                    if (items.contains(itemMap.getKey()) && items.contains(itemMap.getValue())) return true;
                }
                ItemStack ruinedItem = craftingStacks.stream().filter(
                        i -> i.getItem() instanceof RuinedAshesItem).findFirst().orElse(null);
                if (ruinedItem == null) return false;
                Identifier repairingItem = RuinedEquipmentUtils.getItemKeyIdFromItemStack(ruinedItem);
                ItemStack moddedRepair = craftingStacks
                        .stream()
                        .filter(i -> Registries.ITEM.getId(i.getItem()).compareTo(repairingItem) == 0)
                        .findFirst()
                        .orElse(null);
                return moddedRepair != null;
            }
        }
        return false;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inv, RegistryWrapper.WrapperLookup registryManager) {
        ItemStack repairingItem = ItemStack.EMPTY;
        ItemStack ruinedItem = ItemStack.EMPTY;
        ItemStack t;
        for(int i = 0; i < inv.getSize(); i++) {
            t = inv.getStackInSlot(i);
            if (!t.isEmpty()) {
                if (RuinedEquipmentUtils.isRuinedItem(t.getItem())) {
                    ruinedItem = t;
                } else {
                    repairingItem = t;
                }
            }
        }
        ItemStack newStack;
        int targetDamage;
        if (ruinedItem.getItem() == RuinedEquipmentMod.RUINED_ASHES_ITEM) {
            Item item = Registries.ITEM.get(RuinedEquipmentUtils.getItemKeyIdFromItemStack(ruinedItem));
            newStack = new ItemStack(item);
            targetDamage = (int) ((1.0 - REPAIR_MODIFIER) * newStack.getMaxDamage());
        } else if (repairingItem != ItemStack.EMPTY) {
            newStack = repairingItem.copy();
            newStack.remove(DataComponentTypes.ENCHANTMENTS);
            targetDamage =
                    newStack.getDamage() - (int)(REPAIR_MODIFIER * newStack.getMaxDamage());
        } else {
            newStack =
                    new ItemStack(RuinedEquipmentItems.getVanillaItemMap().get(ruinedItem.getItem()));
            targetDamage = (int) ((1.0 - REPAIR_MODIFIER) * newStack.getMaxDamage());
        }
        newStack.setDamage(Math.max(targetDamage, 0));
        return newStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RuinedEquipmentMod.RUINED_CRAFT_REPAIR_RECIPE;
    }
}
