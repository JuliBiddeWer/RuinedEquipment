package pepjebs.ruined_equipment.item;

import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import pepjebs.ruined_equipment.RuinedEquipmentMod;

import java.util.HashMap;
import java.util.Map;

public class RuinedEquipmentItems {

    public static Item[] SUPPORTED_VANILLA_ITEMS = {
            Items.DIAMOND_PICKAXE,
            Items.GOLDEN_PICKAXE,
            Items.IRON_PICKAXE,
            Items.NETHERITE_PICKAXE,
            Items.STONE_PICKAXE,
            Items.WOODEN_PICKAXE,
            Items.DIAMOND_SWORD,
            Items.GOLDEN_SWORD,
            Items.IRON_SWORD,
            Items.NETHERITE_SWORD,
            Items.STONE_SWORD,
            Items.WOODEN_SWORD,
            Items.DIAMOND_SHOVEL,
            Items.GOLDEN_SHOVEL,
            Items.IRON_SHOVEL,
            Items.NETHERITE_SHOVEL,
            Items.STONE_SHOVEL,
            Items.WOODEN_SHOVEL,
            Items.DIAMOND_AXE,
            Items.GOLDEN_AXE,
            Items.IRON_AXE,
            Items.NETHERITE_AXE,
            Items.STONE_AXE,
            Items.WOODEN_AXE,
            Items.DIAMOND_HOE,
            Items.GOLDEN_HOE,
            Items.IRON_HOE,
            Items.NETHERITE_HOE,
            Items.STONE_HOE,
            Items.WOODEN_HOE,
            Items.BOW,
            Items.FISHING_ROD,
            Items.SHEARS,
            Items.TRIDENT,
            Items.FLINT_AND_STEEL,
            Items.CROSSBOW,
            Items.SHIELD,
            Items.DIAMOND_HELMET,
            Items.GOLDEN_HELMET,
            Items.IRON_HELMET,
            Items.NETHERITE_HELMET,
            Items.CHAINMAIL_HELMET,
            Items.TURTLE_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.GOLDEN_CHESTPLATE,
            Items.IRON_CHESTPLATE,
            Items.NETHERITE_CHESTPLATE,
            Items.CHAINMAIL_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.GOLDEN_LEGGINGS,
            Items.IRON_LEGGINGS,
            Items.NETHERITE_LEGGINGS,
            Items.CHAINMAIL_LEGGINGS,
            Items.DIAMOND_BOOTS,
            Items.GOLDEN_BOOTS,
            Items.IRON_BOOTS,
            Items.NETHERITE_BOOTS,
            Items.CHAINMAIL_BOOTS,
            Items.LEATHER_HELMET,
            Items.LEATHER_CHESTPLATE,
            Items.LEATHER_LEGGINGS,
            Items.LEATHER_BOOTS
    };

    public static Map<Item, Item> getVanillaItemMap() {
        Map<Item, Item> vanillaItemMap = new HashMap<>();
        for (Item i : RuinedEquipmentItems.SUPPORTED_VANILLA_ITEMS) {
            vanillaItemMap.put(
                    i,
                    Registries.ITEM.get(
                            Identifier.of(
                                    RuinedEquipmentMod.MOD_ID,
                                    RuinedEquipmentMod.RUINED_PREFIX + Registries.ITEM.getId(i).getPath()
                            )
                    )
            );
        }
        return vanillaItemMap;
    }

    public static Map<Item, Integer> getVanillaDyeableItemMap() {
        Map<Item, Integer> vanillaDyeableMap = new HashMap<>();

        for (Map.Entry<Item, Item> entry : getVanillaItemMap().entrySet()) {
            Item keyItem   = entry.getKey();
            Item valueItem = entry.getValue();
            String path    = Registries.ITEM.getId(valueItem).getPath();

            if (path.contains("leather")) {
                vanillaDyeableMap.put(keyItem, DyedColorComponent.DEFAULT_COLOR);
            }
        }

        return vanillaDyeableMap;
    }


}
