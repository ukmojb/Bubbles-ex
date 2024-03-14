package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.inv.SlotDefinition;
import baubles.common.Config;
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

    private int offset = 0; // Can't be higher than getSlots()
    private boolean[] changed;
    private boolean blockEvents = false;
    private EntityLivingBase player;

    public BaublesContainer() {
        this.slots = getDefaultSlots();
        this.stacks = new ItemStack[slots.length];
        this.changed = new boolean[slots.length];
    }

    public ItemStack getStack(int slot) {
        int slotGet = offset + slot;
        if (slotGet >= getSlots()) slotGet %= getSlots();
        ItemStack stack = this.stacks[slotGet];
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public void setStack(int slot, ItemStack stack) {
        int slotGet = offset + slot;
        if (slotGet >= getSlots()) slotGet %= getSlots();
        this.stacks[slotGet] = stack;
    }

    public SlotDefinition getSlot(int slot) {
        int slotGet = offset + slot;
        if (slotGet >= getSlots()) slotGet %= getSlots();
        return this.slots[slotGet];
    }

    public void incrOffset(int offset) {
        this.offset += offset;
        int slots = getSlots();
        this.offset %= slots;
        if (this.offset < 0) this.offset += slots;
        else if (this.offset >= slots) this.offset -= slots;
    }

    public void resetOffset() {
        this.offset = 0;
    }

    protected void onContentsChanged(int slot) {
        setChanged(slot, true);
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= slots.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots.length + ")");
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
        if (stack == null || stack.isEmpty()) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        if (bauble != null) {
            IBaubleType stackType = bauble.getType(stack);
            IBaubleType slotType = getSlot(slot).getType();
            return bauble.canEquip(stack, player) && (slotType == BaubleType.TRINKET || stackType == BaubleType.TRINKET || stackType == slotType);
        }
        return false;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValidForSlot(slot, stack, player)) {
            setStack(slot, stack);
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
        if (stack == null) stack = ItemStack.EMPTY;
        return stack;
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
            if (existing.isEmpty())
                setStack(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }

            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;
        validateSlotIndex(slot);

        ItemStack existing = this.getStack(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setStack(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                this.setStack(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isEventBlocked() {
        return blockEvents;
    }

    @Override
    public void setEventBlock(boolean blockEvents) {
        this.blockEvents = blockEvents;
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

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < slots.length; i++) {
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
            int slot = stackTag.getInteger("Slot");
            if (slot < getSlots()) {
                ItemStack stack = new ItemStack(stackTag);
                stacks[stackTag.getInteger("Slot")] = stack;
            }
        }
    }

    private static SlotDefinition[] getDefaultSlots() {
        return Config.getSlots();
    }
}