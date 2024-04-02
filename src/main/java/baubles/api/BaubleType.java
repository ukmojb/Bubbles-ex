package baubles.api;

import baubles.common.Baubles;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

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
    final IntList validSlots = new IntArrayList(7);

    BaubleType(String name, int... validSlots) {
        this.name = name;
        this.translationKey = Baubles.MODID + ".type." + name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public String getBackgroundTexture() {
        return backgroundTexture;
    }

    public void addSlot(int slot) {
        validSlots.add(slot);
    }

    // Bauble Type Map
    public static Map<String, IBaubleType> getTypes() {
        return TYPES;
    }

    public static IBaubleType putType(String name) {
        IBaubleType type = new BaubleTypeImpl(name);
        TYPES.put(name, type);
        return type;
    }

    public static IBaubleType getType(String name) {
        return TYPES.get(name);
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
        if (validSlots.size() < 2 && this == RING) {
            array = new int[2];
            array[1] = -1;
        }
        else array = new int[1];

        array[0] = -1;

        return validSlots.toArray(array);
    }
}
