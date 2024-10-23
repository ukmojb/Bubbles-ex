package baubles.api.inv;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;

import java.util.*;

public class SlotTypeDefinition implements SlotDefinition {

    private static final Int2ObjectMap<SlotDefinition> SLOTS = new Int2ObjectOpenHashMap<>();

    private final IBaubleType type;

    public SlotTypeDefinition(int index, IBaubleType type) {
        if (type instanceof BaubleType) ((BaubleType) type).addSlot(index);
        this.type = type;
        SLOTS.put(index, this);
    }

    public String getBackgroundTexture(int id) {
        return type.getBackgroundTexture();
    }

    @Override
    public String getTranslationKey(int id) {
        return type.getTranslationKey();
    }

    @Override
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
