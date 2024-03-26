package baubles.api.inv;


import net.minecraft.item.ItemStack;

public interface SlotDefinition {
    String getBackgroundTexture(int id);
    String getTranslationKey(int id);
    boolean canPutItem(int id, ItemStack stack);
    default int getSlotStackLimit() {
        return 64;
    }
}
