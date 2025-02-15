package baubles.common.init;

import baubles.api.BaubleType;
import baubles.api.IBaubleType;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BaubleTypes {

    private static final Map<ResourceLocation, IBaubleType> REGISTRY_MAP = new HashMap<>();

    protected static void register(IBaubleType type) {
        REGISTRY_MAP.put(type.getRegistryName(), type);
    }

    public static void registerDefaults() {
        register(BaubleType.AMULET);
        register(BaubleType.RING);
        register(BaubleType.BELT);
        register(BaubleType.TRINKET);
        register(BaubleType.HEAD);
        register(BaubleType.BODY);
        register(BaubleType.CHARM);
    }

    @Nullable
    public static IBaubleType get(ResourceLocation location) {
        return REGISTRY_MAP.get(location);
    }

    public static Map<ResourceLocation, IBaubleType> getRegistryMap() {
        return new ImmutableMap.Builder<ResourceLocation, IBaubleType>().putAll(REGISTRY_MAP).build();
    }

    /**
     * An event for registering {@link IBaubleType}s
     * It's an event that can be subscribed (subscribe with {@link SubscribeEvent})
     **/
    @SuppressWarnings("unused")
    public static class Register extends Event {
        public void register(IBaubleType type) {
            assert type.getRegistryName() != null;
            BaubleTypes.register(type);
        }
    }
}
