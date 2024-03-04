package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotBauble extends SlotItemHandler {

    private final IBaublesItemHandler baublesHandler;

    private final EntityPlayer player;

    public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int par4, int par5) {
        super(itemHandler, slot, par4, par5);
        this.baublesHandler = itemHandler;
        this.player = player;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble != null && baublesHandler.isItemValidForSlot(slotNumber, stack, player);
    }

    @Override
    public boolean canTakeStack(@Nonnull EntityPlayer player) {
        ItemStack stack = getStack();
        if (stack.isEmpty()) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble == null || bauble.canUnequip(stack, player);
    }

    @Nonnull
    @Override
    public ItemStack onTake(@Nonnull EntityPlayer playerIn, @Nonnull ItemStack stack) {
        if (!stack.isEmpty() && !baublesHandler.isEventBlocked()) {
            IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble != null) bauble.onUnequipped(stack, playerIn);
        }

        super.onTake(playerIn, stack);
        return stack;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        ItemStack slotStack = getStack();

        if (!slotStack.isEmpty()) {
            if (baublesHandler.isEventBlocked()) return;
            IBauble bauble = slotStack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble != null) bauble.onUnequipped(slotStack, player);
        }

        super.putStack(stack);

        ItemStack slotStackNew = getStack();

        if (!slotStackNew.isEmpty() && !ItemStack.areItemStacksEqual(slotStack, slotStackNew) && !baublesHandler.isEventBlocked()) {
            IBauble bauble = slotStackNew.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble != null) bauble.onEquipped(slotStackNew, player);
        }
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }
}
