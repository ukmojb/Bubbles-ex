package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.cap.InjectableBauble;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class SlotBauble extends SlotItemHandler {

    private final int slotIndex;

    private final IBaublesItemHandler baublesHandler;
    private final EntityPlayer player;

    public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int par4, int par5) {
        super(itemHandler, slot, par4, par5);
        this.baublesHandler = itemHandler;
        this.player = player;
        this.slotIndex = slot;
    }

    public IBaublesItemHandler getBaublesHandler() {
        return this.baublesHandler;
    }

    @Deprecated
    @Override
    public IItemHandler getItemHandler() {
        return super.getItemHandler();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);

        return bauble != null && baublesHandler.isItemValidForSlot(slotIndex, stack, player);
    }

    @Override
    public boolean canTakeStack(@Nonnull EntityPlayer player) {
        ItemStack stack = getStack();
        if (stack.isEmpty()) return false;
        int binding = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        if (!player.isCreative() && binding > 0) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble == null || bauble.canUnequip(stack, player);
    }

    @Nonnull
    @Override
    public ItemStack onTake(@Nonnull EntityPlayer playerIn, @Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble != null) bauble.onUnequipped(stack, playerIn);
        }

        return super.onTake(playerIn, stack);
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if (getHasStack() && !ItemStack.areItemStacksEqual(stack, getStack()) &&
                getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(getStack(), player);
        }


        ItemStack oldstack = getStack().copy();
        super.putStack(stack);

        if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack, getStack()) &&
                getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            Objects.requireNonNull(getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)).onEquipped(getStack(), player);
        }
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Nullable
    @Override
    public String getSlotTexture() {
        ResourceLocation bg;
        if (this.getBaublesHandler().getSlot(this.slotIndex) == null) {
            bg = null;
        } else {
            bg = this.getBaublesHandler().getSlot(this.slotIndex).getBackgroundTexture(this.slotIndex);
        }

        return bg == null ? null : bg.toString();
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        return this.baublesHandler.getStackInSlot(this.slotIndex);
    }

}
