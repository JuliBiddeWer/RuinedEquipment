package pepjebs.ruined_equipment.utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.component.ComponentType;
import pepjebs.ruined_equipment.RuinedEquipmentMod;
import pepjebs.ruined_equipment.item.RuinedEquipmentItems;

import java.util.*;
import java.util.stream.Collectors;

public class RuinedEquipmentUtils {

    public static String RUINED_ITEM_KEY_TAG = "ItemKey";

    public static final Map<java.util.UUID, Pair<ItemStack, EquipmentSlot>> PENDING_BREAKING = new HashMap<>();

    public static NbtCompound getCustomData(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        return component != null ? component.copyNbt() : new NbtCompound();
    }

    public static void setCustomData(ItemStack stack, NbtCompound nbt) {
        if (nbt == null || nbt.isEmpty()) {
            stack.remove(DataComponentTypes.CUSTOM_DATA);
        } else {
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }

    public static <T> void copyComponentIfPresent(ItemStack src, ItemStack dst, ComponentType<T> type) {
        T value = src.get(type);
        if (value != null) dst.set(type, value);
    }

    public static boolean ruinedItemHasEnchantment(ItemStack ruinedItem, RegistryKey<Enchantment> enchantmentKey) {
        ItemEnchantmentsComponent enchantments = ruinedItem.getEnchantments();
        if (enchantments.isEmpty()) return false;
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.matchesKey(enchantmentKey)) return true;
        }
        return false;
    }

    public static boolean isRuinedItem(Item item) {
        return Registries.ITEM.getId(item).getNamespace().compareTo(RuinedEquipmentMod.MOD_ID) == 0;
    }

    public static Item getEmpowermentApplicationItem() {
        if (RuinedEquipmentMod.CONFIG != null) {
            try {
                String[] itemId = RuinedEquipmentMod.CONFIG.empowermentSmithingItem.split(":");
                return Registries.ITEM.get(Identifier.of(itemId[0], itemId[1]));
            } catch (Exception e) {
                RuinedEquipmentMod.LOGGER.warn(e.getMessage());
            }
        }
        return Items.NETHERITE_SCRAP;
    }

    public static int compareItemsById(Item i1, Item i2) {
        return Registries.ITEM.getId(i1).compareTo(Registries.ITEM.getId(i2));
    }

    public static int generateAnvilLevelCost(ItemStack repaired, int maxLevel) {
        if (repaired.getMaxDamage() == 0) {
            return 1;
        }
        int targetLevel = maxLevel * (repaired.getMaxDamage() - repaired.getDamage()) / repaired.getMaxDamage();
        return Math.max(targetLevel, 1);
    }

    public static Item getRepairItemForItemStack(ItemStack stack) {
        Item vanillaItem = RuinedEquipmentItems.getVanillaItemMap().get(stack.getItem());
        if (vanillaItem == null) {
            Identifier modItemId = RuinedEquipmentUtils.getItemKeyIdFromItemStack(stack);
            vanillaItem = Registries.ITEM.get(modItemId);
        }
        return vanillaItem;
    }

    public static ItemStack generateRepairedItemForAnvilByFraction(
            ItemStack leftStack,
            double damageFraction) {
        Item vanillaItem = getRepairItemForItemStack(leftStack);
        int maxDamage = new ItemStack(vanillaItem).getMaxDamage();
        return generateRepairedItemForAnvilByDamage(leftStack, (int) (damageFraction * (double) maxDamage));
    }

    public static ItemStack generateRepairedItemForAnvilByDamage(
            ItemStack leftStack,
            int targetDamage){
        NbtCompound custom = getCustomData(leftStack);
        boolean isMaxEnchant = custom.contains(RuinedEquipmentMod.RUINED_MAX_ENCHT_TAG)
                && custom.getBoolean(RuinedEquipmentMod.RUINED_MAX_ENCHT_TAG);

        Item vanillaItem = getRepairItemForItemStack(leftStack);
        ItemStack repaired = new ItemStack(vanillaItem);

        copyComponentIfPresent(leftStack, repaired, DataComponentTypes.CUSTOM_NAME);
        copyComponentIfPresent(leftStack, repaired, DataComponentTypes.LORE);
        copyComponentIfPresent(leftStack, repaired, DataComponentTypes.DYED_COLOR);
        copyComponentIfPresent(leftStack, repaired, DataComponentTypes.BANNER_PATTERNS);
        copyComponentIfPresent(leftStack, repaired, DataComponentTypes.BASE_COLOR);

        custom.remove(RuinedEquipmentMod.RUINED_MAX_ENCHT_TAG);
        custom.remove(RUINED_ITEM_KEY_TAG);
        setCustomData(repaired, custom);

        ItemEnchantmentsComponent enchantments = leftStack.getEnchantments();
        if (!enchantments.isEmpty()) {
            ItemEnchantmentsComponent.Builder builder =
                    new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            for (RegistryEntry<Enchantment> enchant : enchantments.getEnchantments()) {
                int level = isMaxEnchant ? enchant.value().getMaxLevel() : enchantments.getLevel(enchant);
                builder.add(enchant, level);
            }
            repaired.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        }

        repaired.setDamage(targetDamage);
        return repaired;
    }

    public static List<Identifier> getParsedBlocklistForRuinedAshesItems() {
        if (RuinedEquipmentMod.CONFIG == null || RuinedEquipmentMod.CONFIG.blocklistForRuinedAshesItems == null
                || RuinedEquipmentMod.CONFIG.blocklistForRuinedAshesItems.isEmpty())
            return new ArrayList<>();
        try {
            return Arrays.stream(RuinedEquipmentMod.CONFIG.blocklistForRuinedAshesItems
                            .split(";"))
                    .map(p -> {
                        String[] idParts = p.split(":");
                        return Identifier.of(idParts[0], idParts[1]);
                    })
                    .toList();
        } catch(Exception e) {
            RuinedEquipmentMod.LOGGER.warn("Ill-formed Config field blocklistForRuinedAshesItems");
            return new ArrayList<>();
        }
    }

    public static void onSendEquipmentBreakStatusImpl(
            ServerPlayerEntity serverPlayer,
            ItemStack breakingStack,
            boolean forceSet) {
        if (RuinedEquipmentMod.CONFIG != null && RuinedEquipmentMod.CONFIG.skipEmptyNBTEquipmentBreaks) {
            boolean hasCustom = breakingStack.contains(DataComponentTypes.CUSTOM_NAME)
                    || breakingStack.contains(DataComponentTypes.LORE)
                    || breakingStack.contains(DataComponentTypes.CUSTOM_DATA)
                    || breakingStack.contains(DataComponentTypes.ENCHANTMENTS)
                    || breakingStack.contains(DataComponentTypes.DYED_COLOR)
                    || breakingStack.contains(DataComponentTypes.BANNER_PATTERNS);
            if (!hasCustom) return;
        }
        for (Map.Entry<Item, Item> itemMap : RuinedEquipmentItems.getVanillaItemMap().entrySet()) {
            if (isVanillaItemStackBreaking(breakingStack, itemMap.getValue())) {
                ItemStack ruinedStack = new ItemStack(itemMap.getKey());
                processBreakingEquipment(serverPlayer, breakingStack, forceSet, ruinedStack);
                return;
            }
        }
        if (RuinedEquipmentMod.CONFIG.enableRuinedItemsAshesGeneration) {
            ItemStack ruinedStack = new ItemStack(RuinedEquipmentMod.RUINED_ASHES_ITEM);
            if (getParsedBlocklistForRuinedAshesItems().stream()
                    .anyMatch(i -> i.compareTo(Registries.ITEM.getId(breakingStack.getItem())) == 0))
                return;
            processBreakingEquipment(serverPlayer, breakingStack, forceSet, ruinedStack);
        }
    }

    private static void processBreakingEquipment(
            ServerPlayerEntity serverPlayer,
            ItemStack breakingStack,
            boolean forceSet,
            ItemStack ruinedStack
            ) {
        copyComponentIfPresent(breakingStack, ruinedStack, DataComponentTypes.CUSTOM_NAME);
        copyComponentIfPresent(breakingStack, ruinedStack, DataComponentTypes.LORE);
        copyComponentIfPresent(breakingStack, ruinedStack, DataComponentTypes.DYED_COLOR);
        copyComponentIfPresent(breakingStack, ruinedStack, DataComponentTypes.BANNER_PATTERNS);
        copyComponentIfPresent(breakingStack, ruinedStack, DataComponentTypes.BASE_COLOR);
        copyComponentIfPresent(breakingStack, ruinedStack, DataComponentTypes.CUSTOM_DATA);

        ItemEnchantmentsComponent enchantments = breakingStack.getEnchantments();
        if (!enchantments.isEmpty()) {
            ruinedStack.set(DataComponentTypes.ENCHANTMENTS, enchantments);
        }

        if (ruinedStack.getItem() == RuinedEquipmentMod.RUINED_ASHES_ITEM) {
            Identifier breakingId = Registries.ITEM.getId(breakingStack.getItem());
            NbtCompound custom = getCustomData(ruinedStack);
            custom.putString(RUINED_ITEM_KEY_TAG, breakingId.toString());
            setCustomData(ruinedStack, custom);
        }

        // Force set will place the Ruined item in hand
        if (forceSet) {
            int idx = 0;
            if (serverPlayer.getInventory().offHand.get(0).toString().compareTo(breakingStack.toString()) != 0)
                idx = serverPlayer.getInventory().selectedSlot + 1;
            RuinedEquipmentMod.ruinedEquipmentSetter.put(
                    serverPlayer.getName().getString(),
                    new Pair<>(idx, ruinedStack));
        } else {
            serverPlayer.getInventory().offerOrDrop(ruinedStack);
        }
    }

    public static Identifier getItemKeyIdFromItemStack(ItemStack ruinedItemAshes) {
        assert ruinedItemAshes.getItem() == RuinedEquipmentMod.RUINED_ASHES_ITEM;
        NbtCompound custom = getCustomData(ruinedItemAshes);
        if (!custom.contains(RUINED_ITEM_KEY_TAG)) return null;
        return Identifier.of(custom.getString(RUINED_ITEM_KEY_TAG));
    }

    public static Map<Identifier, Identifier> getParsedRuinedItemsAshesRepairItems() {
        if (RuinedEquipmentMod.CONFIG == null
                || RuinedEquipmentMod.CONFIG.ruinedItemsAshesRepairItems == null
                || RuinedEquipmentMod.CONFIG.ruinedItemsAshesRepairItems.isEmpty()) return null;
        String ruinedItemsAshesRepairStr = RuinedEquipmentMod.CONFIG.ruinedItemsAshesRepairItems;
        try {
            return Arrays.stream(ruinedItemsAshesRepairStr.split(";"))
                    .map(s -> {
                        String[] modItem = s.split("/")[0].split(":");
                        String[] modRepair = s.split("/")[1].split(":");
                        Identifier modItemId = Identifier.of(modItem[0], modItem[1]);
                        Identifier modRepairId = Identifier.of(modRepair[0], modRepair[1]);
                        return new Pair<>(modItemId, modRepairId);
                    })
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        } catch (Exception e) {
            RuinedEquipmentMod.LOGGER.warn("Ill-formed Config field ruinedItemsAshesRepairItems");
            return null;
        }
    }

    public static boolean isVanillaItemStackBreaking(ItemStack breakingStack, Item vanillaItem) {
        return breakingStack.isOf(vanillaItem)
            && breakingStack.getMaxDamage() - breakingStack.getDamage() <= 0;
    }
}
