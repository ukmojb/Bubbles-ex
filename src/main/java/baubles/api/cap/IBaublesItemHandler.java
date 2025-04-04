package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.inv.SlotDefinition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

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

    SlotDefinition getRealSlot(int slotIndex);

    void addSlot(SlotDefinition addSlotDefinition);
    void removeSlot(SlotDefinition addSlotDefinition);

    void setSlot(int slot, SlotDefinition setSlotDefinition);

    /**
     * {@link IItemHandler#isItemValid(int, ItemStack)} but with entity.
     * @param slotIndex index of slot to put
     * @param stack stack to try to put. It doesn't need to be {@link IBauble}
     * @param entity entity that has inventory for baubles. It doesn't need to be {@link EntityPlayer}
     * @return true if given stack can be put to the specific slot
     **/
    boolean isItemValidForSlot(int slotIndex, ItemStack stack, EntityLivingBase entity);

    ItemStack getStackInSlotNA(int slot);

//    boolean isEventBlocked();
//    void setEventBlock(boolean blockEvents);

    void onContentsChanged(int slotIndex);
    /**
     * Used internally for syncing. Indicates if the inventory has changed since last sync
     */
    boolean isChanged(int slot);

    void setChanged(int slot, boolean changed);

    ItemStack[] getStacks();

    @Deprecated
    default void setPlayer(EntityLivingBase player) {}

}
