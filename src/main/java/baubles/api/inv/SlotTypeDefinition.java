package baubles.api.inv;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Default implementation of {@link SlotDefinition}
 * Uses {@link IBaubleType}s for implementing methods.
 * Don't use this methods anywhere except for implementing a new {@link IBaublesItemHandler}
 **/
public class SlotTypeDefinition implements SlotDefinition {

    private static final Int2ObjectMap<SlotDefinition> SLOTS = new Int2ObjectOpenHashMap<>();

    private final IBaubleType type;
    private final ResourceLocation backgroundTexture;

    public SlotTypeDefinition(int index, IBaubleType type, ResourceLocation backgroundTexture) {
        if (type instanceof BaubleType) ((BaubleType) type).addSlot(index);
        this.type = type;
        this.backgroundTexture = backgroundTexture;
        SLOTS.put(index, this);
    }

    public SlotTypeDefinition(int index, IBaubleType type) {
        this(index, type, new ResourceLocation(type.getRegistryName().getNamespace(), "gui/slots/" + type.getRegistryName().getPath() + ".png"));
    }

    @Nullable
    @Override
    public ResourceLocation getBackgroundTexture(int slotIndex) {
        return this.backgroundTexture;
    }

    @Nonnull
    @Override
    public String getTranslationKey(int id) {
        return type.getTranslationKey();
    }

    public boolean canPutType(IBaubleType type) {
        return this.type == BaubleType.TRINKET || type == BaubleType.TRINKET || this.type.equals(type);
    }

    @Override
    public boolean canPutItem(int id, ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        IBaubleType type = Objects.requireNonNull(bauble).getType(stack);
        return this.canPutType(type);
    }

    public static SlotDefinition getSlot(int id, IBaubleType type) {
        SlotDefinition slot = SLOTS.get(id);
        if (slot == null) slot = new SlotTypeDefinition(id, type);
        return slot;
    }


}
