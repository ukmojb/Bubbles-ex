package baubles.api.inv;


import baubles.api.IBaubleType;
import net.minecraft.item.ItemStack;

public interface SlotDefinition {
    String getBackgroundTexture(int id);
    String getTranslationKey(int id);
    boolean canPutType(IBaubleType type);
    boolean canPutItem(int id, ItemStack stack);
    default int getSlotStackLimit() {
        return 64;
    }
}
