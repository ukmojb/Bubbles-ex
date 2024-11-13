package baubles.api;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, IBaubleType> TYPES = new HashMap<>();

    final String name;
    final String translationKey, backgroundTexture;
    final IntList validSlots = new IntArrayList(1);

    BaubleType(String name, int... validSlots) {
        this.name = name;
        this.translationKey = "baubles.type." + name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nonnull
    @Override
    public String getBackgroundTexture() {
        return backgroundTexture;
    }

    @Override
    public boolean canApplyEnchantment(EnumEnchantmentType type, ItemStack stack) {
        switch (type) {
            case ARMOR_HEAD: if (this == HEAD) return true;
            case ARMOR_CHEST: if (this == AMULET || this == BODY) return true;
            case ARMOR_LEGS: if (this == BELT) return true;
            case BREAKABLE: return stack.isItemStackDamageable();
        }
        return IBaubleType.super.canApplyEnchantment(type, stack);
    }

    public void addSlot(int slot) {
        validSlots.add(slot);
    }

    // Bauble Type Map TODO this sucks
    public static Map<String, IBaubleType> getTypes() {
        return TYPES;
    }

    public static IBaubleType register(IBaubleType type) {
        TYPES.put(type.getName(), type);
        return type;
    }

    @Nullable
    public static IBaubleType getType(String name) {
        return TYPES.get(name);
    }

    public static IBaubleType getOrCreateType(String name) {
        IBaubleType baubleType = TYPES.get(name);
        if (baubleType == null) baubleType = putType(name);
        return baubleType;
    }

    private static IBaubleType putType(String name) {
        IBaubleType type = new BaubleTypeImpl(name);
        TYPES.put(name, type);
        return type;
    }

    static {
        for (BaubleType type : BaubleType.values()) {
            TYPES.put(type.name, type);
        }
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
}
