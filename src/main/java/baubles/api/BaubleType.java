package baubles.api;

public enum BaubleType implements IBaubleType {
    AMULET("amulet", 0),
    RING("ring", 1, 2),
    BELT("belt", 3),
    TRINKET("trinket", 0, 1, 2, 3, 4, 5, 6),
    HEAD("head", 4),
    BODY("body", 5),
    CHARM("charm", 6);

    final String backgroundTexture;
    final int[] validSlots;

    BaubleType(String backgroundTexture, int... validSlots) {
        this.backgroundTexture = "baubles:gui/slots/" + backgroundTexture;
        this.validSlots = validSlots;
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
