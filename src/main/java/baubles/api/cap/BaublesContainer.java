package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.inv.SlotDefinition;
import baubles.common.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Default implementation of {@link IBaublesItemHandler}
 **/
// TODO Don't use offset in isItemValid and such.
public class BaublesContainer implements PlayerBaubleHandler, INBTSerializable<NBTTagCompound> {

    private final ItemStack[] stacks;
    private final SlotDefinition[] slots;

    private int offset = 0; // Can't be higher than getSlots()
    private boolean[] changed;

    /**
     * Entity which has the baubles inventory
     **/
    private final EntityLivingBase entity;

    /**
     * Items to drop when slots get updated
     **/
    private ItemStack[] itemsToPuke = null;

    /**
     * Only for internal use. Do not use it anywhere else.
     * Used for factory parameter of {@link CapabilityManager#register(Class, Capability.IStorage, Callable)}
     **/
    public BaublesContainer() {
        this(null);
    }

    public BaublesContainer(EntityLivingBase entity) {
        this.slots = getDefaultSlots();
        this.stacks = new ItemStack[slots.length];
        this.changed = new boolean[slots.length];
        this.entity = entity;
    }

    @Override
    public EntityLivingBase getEntity() {
        return this.entity;
    }

    @Override
    public SlotDefinition getSlot(int slot) {
        return this.slots[getSlotIndex(slot)];
    }

    public SlotDefinition getSlot_Workaround(int slot) {
        return this.slots[slot];
    }

    // TODO Find a way to use without casting.
    public int getOffset() {
        return offset;
    }

    // TODO Find a way to use without casting.
    public void incrOffset(int offset) {
        this.offset += offset;
        int slots = getSlots();
        this.offset %= slots;
        if (this.offset < 0) this.offset += slots;
        else if (this.offset >= slots) this.offset -= slots;
    }

    // TODO Find a way to use without casting.
    public void changeOffsetBasedOnSlot(int slot) {
        if (this.getSlots() > 8) {
            this.offset = slot;
        }
    }

    @Override
    public void changeOffset(int offset) {

    }

    // TODO Find a way to use without casting.
    @Override
    public void resetOffset() {
        this.offset = 0;
    }

    protected void onContentsChanged(int slot) {
        setChanged(slot, true);
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase entity) {
        if (stack == null || stack.isEmpty()) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        if (bauble != null) {
            return bauble.canEquip(stack, entity) && this.getSlot(slot).canPutItem(this.getSlotIndex(slot), stack);
        }
        return false;
    }

    public boolean isItemValidForSlot_Workaround(int slot, ItemStack stack, EntityLivingBase entity) {
        if (stack == null || stack.isEmpty()) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        if (bauble != null) {
            return bauble.canEquip(stack, entity) && this.slots[slot].canPutItem(slot, stack);
        }
        return false;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValidForSlot(slot, stack, this.entity)) {
            setStack(slot, stack);
            setChanged(slot, true);
        }
    }

    public void setStackInSlot_Workaround(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValidForSlot_Workaround(slot, stack, this.entity)) {
            this.stacks[slot] = stack;
            this.setChanged(slot, true);
        }
    }

    @Override
    public int getSlots() {
        return this.stacks.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        slot = validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;
        ItemStack stack = this.getStack(slot);
        if (stack == null) stack = ItemStack.EMPTY;
        return stack;
    }

    public ItemStack getStackInSlot_Workaround(int slot) {
        slot = validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;
        ItemStack stack = this.stacks[slot];
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.isItemValidForSlot(slot, stack, this.entity)) return stack;
        if (stack.isEmpty()) return ItemStack.EMPTY;

        slot = validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;

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
        slot = validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;

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
        return this.getSlot(slot).getSlotStackLimit();
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

    public void pukeItems(World world, double x, double y, double z) {
        if (this.itemsToPuke != null) {
            for (ItemStack stack : this.itemsToPuke) {
                EntityItem eItem = new EntityItem(world, x, y, z, stack);
                world.spawnEntity(eItem);
            }
            this.itemsToPuke = null;
        }
    }

    public void pukeItems(Entity entity) {
        this.pukeItems(entity.world, entity.posX, entity.posY, entity.posZ);
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
        List<ItemStack> itemsToPuke = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            int slot = stackTag.getInteger("Slot");
            if (!stackTag.hasKey("id", 8)) continue;
            Item item = Item.getByNameOrId(stackTag.getString("id"));
            if (item == null || item == Items.AIR) continue;
            ItemStack stack = new ItemStack(stackTag);
            if (slot < getSlots()) {
                if (this.slots[slot].canPutItem(slot, stack)) this.stacks[slot] = stack;
                else itemsToPuke.add(stack);
            }
            else itemsToPuke.add(new ItemStack(stackTag));
        }
        if (!itemsToPuke.isEmpty()) this.itemsToPuke = itemsToPuke.toArray(new ItemStack[0]);
    }

    private static SlotDefinition[] getDefaultSlots() {
        return Config.getSlots();
    }

    /**
     * Use {@link BaublesContainer#getStackInSlot(int)}
     **/
    private ItemStack getStack(int slot) {
        if (slot == -1) return ItemStack.EMPTY;
        ItemStack stack = this.stacks[getSlotIndex(slot)];
        return stack == null ? ItemStack.EMPTY : stack;
    }

    /**
     * Use {@link BaublesContainer#setStackInSlot(int, ItemStack)}
     **/
    private void setStack(int slot, ItemStack stack) {
        this.stacks[getSlotIndex(slot)] = stack;
    }

    private int validateSlotIndex(int slot) {
        if (slot < 0 || slot >= slots.length) {
            return -1;
        }
        return slot;
    }

    private int getSlotIndex(int slot) {
        int slotGet = offset + slot;
        if (slotGet >= getSlots()) slotGet %= this.getSlots();
        return slotGet;
    }
}