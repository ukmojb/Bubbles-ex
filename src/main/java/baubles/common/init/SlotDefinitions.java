package baubles.common.init;

import baubles.api.IBaubleType;
import baubles.api.inv.SlotDefinition;
import baubles.api.inv.SlotDefinitionType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SlotDefinitions {

    private static final Map<ResourceLocation, SlotDefinition> REGISTRY_MAP = new HashMap<>();

    protected static void register(SlotDefinition definition) {
        REGISTRY_MAP.put(definition.getRegistryName(), definition);
    }

    public static void registerDefaults() {
//        for (IBaubleType type : BaubleTypes.getRegistryMap().values()) {
//            register(new SlotDefinitionType(type));
//        }
        for (IBaubleType type : BaubleTypes.getRegistryMap().values()) {
            register(new SlotDefinitionType(type));
        }
    }

    @Nullable
    public static SlotDefinition get(ResourceLocation location) {
        return REGISTRY_MAP.get(location);
    }

    @SideOnly(Side.CLIENT)
    public static void registerTextures(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (SlotDefinition definition : REGISTRY_MAP.values()) {
            if (definition instanceof SlotDefinitionType) {
                map.registerSprite(((SlotDefinitionType) definition).getBackgroundTexture());
            }
        }
    }

    /**
     * An event for registering {@link SlotDefinition}s
     * It's an event that can be subscribed (subscribe with {@link SubscribeEvent})
     **/
    @SuppressWarnings("unused")
    public static class Register extends Event {
        public void register(SlotDefinition type) {
            assert type.getRegistryName() != null;
            SlotDefinitions.register(type);
        }
    }
}
