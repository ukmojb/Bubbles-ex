package baubles.api.inv;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO Order???
/**
 * An interface used by handlers that implement {@link IBaublesItemHandler} for defining how a slot should behave.
 * Handles item insertion, slot limit and clientside values like background texture and translation key to show slot name.
 * See {@link SlotTypeDefinition} for how to implement a slot definition.
 **/
public interface SlotDefinition {

    /**
     * Translation key for bauble slots
     * Used for hovering text that shows the slots name.
     **/
    @Nonnull
    String getTranslationKey(int slotIndex);

    default DropResult shouldDrop(int slotIndex, ItemStack stack, EntityLivingBase living) {
        return DropResult.DEFAULT;
    }

    /**
     * Background icon for slots.
     * You might need to register textures with {@link TextureStitchEvent.Pre}
     * See {@link Slot#getBackgroundSprite()}
     **/
    @Nullable
    @SideOnly(Side.CLIENT)
    ResourceLocation getBackgroundTexture(int slotIndex);

    boolean canPutItem(int slotIndex, ItemStack stack);

    default int getSlotStackLimit() {
        return 64;
    }

    enum DropResult {
        DEFAULT,
        ALWAYS_KEEP,
        ALWAYS_DROP,
        DESTROY
    }
}
