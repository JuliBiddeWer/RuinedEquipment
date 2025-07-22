package pepjebs.ruined_equipment.component;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pepjebs.ruined_equipment.RuinedEquipmentMod;

import java.util.function.UnaryOperator;

public class ModDataComponentType {
    public static void registerDataComponentTypes() {

    }
    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(RuinedEquipmentMod.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }
}
