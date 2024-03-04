package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.inv.SlotDefinition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class BaublesContainer implements IBaublesItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {

    private final ItemStack[] stacks;
    private final SlotDefinition[] slots;

    private int offset;
    private boolean[] changed;
    private boolean blockEvents = false;
    private EntityLivingBase player;

    public BaublesContainer() {
        this.slots = getDefaultSlots(this);
        this.stacks = new ItemStack[slots.length];
        this.changed = new boolean[slots.length];
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
        if (stack == null || stack.isEmpty()) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble != null && bauble.canEquip(stack, player) && bauble.getType(stack) == getSlot(slot).getType();
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValidForSlot(slot, stack, player)) {
            stacks[offset + slot] = stack;
        }
    }

    @Override
    public int getSlots() {
        return this.stacks.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        ItemStack stack = this.getStack(slot);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.isItemValidForSlot(slot, stack, player)) return stack;
        if (stack.isEmpty()) return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = getStack(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;
        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) this.stacks[offset + slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
            else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }

            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return null;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean isEventBlocked() {
        return blockEvents;
    }

    @Override
    public void setEventBlock(boolean blockEvents) {
        this.blockEvents = blockEvents;
    }

    protected void onContentsChanged(int slot) {
        setChanged(slot, true);
    }

    @Override
    public boolean isChanged(int slot) {
        if (changed == null) {
            changed = new boolean[this.getSlots()];
        }
        return changed[slot];
    }

    @Override
    public void setChanged(int slot, boolean change) {
        if (changed == null) {
            changed = new boolean[this.getSlots()];
        }
        this.changed[slot] = change;
    }

    @Override
    public void setPlayer(EntityLivingBase player) {
        this.player = player;
    }

    public static SlotDefinition[] getDefaultSlots(IBaublesItemHandler baublesHandler) {
        return new SlotDefinition[] {
                new SlotDefinition(baublesHandler, 0, BaubleType.AMULET),
                new SlotDefinition(baublesHandler, 1, BaubleType.RING),
                new SlotDefinition(baublesHandler, 2, BaubleType.RING),
                new SlotDefinition(baublesHandler, 3, BaubleType.BELT),
                new SlotDefinition(baublesHandler, 4, BaubleType.HEAD),
                new SlotDefinition(baublesHandler, 5, BaubleType.BODY),
                new SlotDefinition(baublesHandler, 6, BaubleType.CHARM),
        };
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = getStack(i);
            if (stack == null || stack.isEmpty()) continue;
            NBTTagCompound stackTag = new NBTTagCompound();
            stackTag.setInteger("Slot", i);
            stack.writeToNBT(stackTag);
            list.appendTag(stackTag);
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Items", list);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(stackTag);
            stacks[offset + stackTag.getInteger("Slot")] = stack;
        }
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.length + ")");
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    private ItemStack getStack(int slot) {
        return stacks[offset + slot];
    }

    private SlotDefinition getSlot(int slot) {
        return slots[offset + slot];
    }
}
