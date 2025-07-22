package pepjebs.ruined_equipment.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import pepjebs.ruined_equipment.item.RuinedEquipmentItems;

import java.util.Map;

public class RuinedEquipmentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        for (Map.Entry<Item, Integer> entry : RuinedEquipmentItems.getVanillaDyeableItemMap().entrySet()) {
            Item item      = entry.getKey();
            int defaultCol = entry.getValue();  // jetzt int statt Item

            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                if (tintIndex == 0) {
                    return DyedColorComponent.getColor(stack, defaultCol);
                }
                return -1;
            }, item);
        }
    }
}
