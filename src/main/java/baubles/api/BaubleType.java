package baubles.api;

import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.api.inv.SlotDefinitionType;
import baubles.common.Baubles;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default bauble types
 **/
public enum BaubleType implements IBaubleType {

    AMULET("amulet"),
    RING("ring"),
    BELT("belt"),
    TRINKET("trinket"),
    HEAD("head"),
    BODY("body"),
    CHARM("charm");

    final ResourceLocation name;
    final String translationKey, backgroundTexture;
    final IntList validSlots = new IntArrayList(1);

    BaubleType(String name, int... validSlots) {
        this.name = new ResourceLocation(Baubles.MODID, name);
        this.translationKey = "baubles.type." + name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public boolean canApplyEnchantment(Enchantment enchantment, ItemStack stack) {
        if (enchantment.type == null) return false;
        if (IBaubleType.super.canApplyEnchantment(enchantment, stack)) return true;
        switch (enchantment.type) {
            case ARMOR_HEAD: if (this == HEAD) return true;
            case ARMOR_CHEST: if (this == AMULET || this == BODY) return true;
            case ARMOR_LEGS: if (this == BELT) return true;
            case BREAKABLE: return stack.isItemStackDamageable();
        }
        return false;
    }

    public void addSlot(int slot) {
        validSlots.add(slot);
    }

    // Deprecated
    @Deprecated
    public boolean hasSlot(int slot) {
        switch (slot) {
            default: return false;
            case 0: return this == AMULET || this == TRINKET;
            case 1: case 2: return this == RING || this == TRINKET;
            case 3: return this == BELT || this == TRINKET;
            case 4: return this == HEAD || this == TRINKET;
            case 5: return this == BODY || this == TRINKET;
            case 6: return this == CHARM || this == TRINKET;
        }
    }

    /*
     * Please do not use this method!! If used, you will not be able to get the added bauble slot
     */
    @Deprecated
    public int[] getValidSlots() {
        int[] array;
        if (this == RING) {
            array = new int[2];
            array[1] = -1;
        }
        else array = new int[1];
        array[0] = -1;
        return validSlots.toArray(array);
    }

    /*
     * Please use this method!! This method allows you to get the added bauble slot
     */
    public int[] getValidSlotsArrays(EntityPlayer player) {
        List<Integer> list = getValidSlots(player);
        int[] array = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }


    /*
     * Please use this method!! This method allows you to get the added bauble slot
     */
    public List<Integer> getValidSlots(EntityPlayer player) {
        List<Integer> array = new ArrayList<Integer>();
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < handler.getSlots(); i++) {
            SlotDefinition slotDefinition = handler.getRealSlot(i);
            if (slotDefinition == null) continue;
            SlotDefinitionType slotDefinitionType = (SlotDefinitionType) slotDefinition;
            boolean pass = slotDefinitionType.canPutType(this);
            if (pass) {
                array.add(i);
            }
        }

        return array;
    }



    public static IBaubleType getType(String typeStr){
        return BaubleType.valueOf(typeStr);
    }
}
