package baubles.api.inv;

import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO Ordering
/**
 * An interface used by handlers that implement {@link IBaublesItemHandler} for defining how a slot should behave.
 * Handles item insertion, slot limit and clientside values like background texture and translation key to show slot name.
 * New SlotDefinitions needs to be registered in preInit.
 *
 * @see SlotDefinitionType
 */
public interface SlotDefinition {

    /**
     * Registry name of the slot type
     */
    @Nonnull
    ResourceLocation getRegistryName();

    /**
     * Translation key for bauble slots
     * Used for hovering text that shows the slots name.
     */
    @Nonnull
    String getTranslationKey(int slotIndex);

    /**
     * Used for checking if stack can be inserted in this slot.
     *
     * @param slotIndex Index of the slot
     * @param stack The stack to check
     *
     * @return true if item can be inserted.
     */
    boolean canPutItem(int slotIndex, ItemStack stack);

    /**
     * Get stack limit of the slot.
     */
    default int getSlotStackLimit() {
        return 64;
    }

    /**
     * Background icon for slots.
     * Use {@link TextureStitchEvent.Pre} for registering textures.
     * See {@link Slot#getBackgroundSprite()} for more information.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    ResourceLocation getBackgroundTexture(int slotIndex);
}
