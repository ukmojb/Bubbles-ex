package baubles.api.cap;

import baubles.api.inv.SlotDefinition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import baubles.api.IBauble;

/**
 * An item handler specific for baubles
 * It will always be applied to entity, because I can't find any other reason for it to be not to
 * It also has additional methods for syncing and blocking events
 **/
public interface IBaublesItemHandler extends IItemHandlerModifiable {

    /**
     * Entity that has the baubles inventory
     * Can be used for checking stuff
     **/
    EntityLivingBase getEntity();

    /**
     * Get the slot definition for item insertion check, naming and background texture
     **/
    SlotDefinition getSlot(int slotIndex);

    /**
     * {@link IItemHandler#isItemValid(int, ItemStack)} but with entity.
     * @param slotIndex index of slot to put
     * @param stack stack to try to put. It doesn't need to be {@link IBauble}
     * @param entity entity that has inventory for baubles. It doesn't need to be {@link EntityPlayer}
     * @return true if given stack can be put to the specific slot
     **/
    boolean isItemValidForSlot(int slotIndex, ItemStack stack, EntityLivingBase entity);

    // TODO Is this needed? Used for updating IBaubles with willAutoSync true
    /**
     * Used internally for syncing. Indicates if the inventory has changed since last sync
     */
    boolean isChanged(int slot);

    void setChanged(int slot, boolean changed);
}