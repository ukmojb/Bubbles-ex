package baubles.api;

import baubles.common.Baubles;

public enum BaubleType implements IBaubleType {
    AMULET("amulet", 0),
    RING("ring", 1, 2),
    BELT("belt", 3),
    TRINKET("trinket", 0, 1, 2, 3, 4, 5, 6),
    HEAD("head", 4),
    BODY("body", 5),
    CHARM("charm", 6);

    final String name;
    final String backgroundTexture;
    final int[] validSlots;

    BaubleType(String name, int... validSlots) {
        this.name = name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
        this.validSlots = validSlots;
    }

    @Override
    public String getTranslationKey() {
        return Baubles.MODID + ".type." + name;
    }

    @Override
    public String getBackgroundTexture() {
        return backgroundTexture;
    }

    public boolean hasSlot(int slot) {
        for (int s : validSlots) {
            if (s == slot) return true;
        }
        return false;
    }

    public int[] getValidSlots() {
        return validSlots;
    }
}
