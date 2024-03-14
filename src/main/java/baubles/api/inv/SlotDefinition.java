package baubles.api.inv;

import baubles.api.IBaubleType;

public class SlotDefinition {

    private final int id;
    private final IBaubleType type;

    public SlotDefinition(int id, IBaubleType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getBackgroundTexture() {
        return type.getBackgroundTexture();
    }

    public IBaubleType getType() {
        return type;
    }
}
