package baubles.api.inv;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.item.ItemStack;

import java.util.*;

public class SlotTypeDefinition implements SlotDefinition {

    public static final Map<String, SlotDefinition> SLOTS = new HashMap<>();

    private final IBaubleType type;

    public SlotTypeDefinition(int index, IBaubleType type) {
        if (type instanceof BaubleType) ((BaubleType) type).addSlot(index);
        this.type = type;
    }

    public IBaubleType getType() {
        return type;
    }

    public String getBackgroundTexture(int id) {
        return type.getBackgroundTexture();
    }

    @Override
    public String getTranslationKey(int id) {
        return type.getTranslationKey();
    }

    @Override
    public boolean canPutItem(int id, ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        IBaubleType type = Objects.requireNonNull(bauble).getType(stack);
        return this.type == BaubleType.TRINKET || type == BaubleType.TRINKET || this.type == type;
    }

    public static SlotDefinition getSlot(int id, IBaubleType type) {
        SlotDefinition slot = SLOTS.get(type.getName());
        if (slot == null) slot = new SlotTypeDefinition(id, type);
        return slot;
    }
}
