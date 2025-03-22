package baubles.api;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.BaublesInventoryWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;

import java.util.Objects;

/**
 * @author Azanor
 */
public class BaublesApi {
    /**
     * Retrieves the baubles item handler capability handler for the supplied player
     */
    public static IBaublesItemHandler getBaublesHandler(EntityPlayer player) {
        IBaublesItemHandler handler = Objects.requireNonNull(player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null));
        return handler;
    }

    // TODO Remove it once sure.
    /**
     * Retrieves the baubles capability handler wrapped as a IInventory for the supplied player
     */
    @Deprecated
    public static IInventory getBaubles(EntityPlayer player) {
        IBaublesItemHandler handler = Objects.requireNonNull(player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null));
        return new BaublesInventoryWrapper(handler, player);
    }

    /**
     * Returns if the passed in item is equipped in a bauble slot. Will return the first slot found
     *
     * @return -1 if not found and slot number if it is found
     */
    public static int isBaubleEquipped(EntityPlayer player, Item bauble) {
        for (int i = 0; i < getBaublesHandler(player).getSlots(); i++) {
            if (BaublesApi.getBaublesHandler(player).getRealSlot(i) != null) {
                if (getBaublesHandler(player).getStackInSlot(i).getItem() == bauble) {
                    return i;
                }
            }
        }
        return -1;
    }
}
