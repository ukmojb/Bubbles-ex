package baubles.api.inv;

import baubles.api.IBaubleType;
import baubles.api.cap.IBaublesItemHandler;

public class SlotDefinition {

    private final IBaublesItemHandler baublesHandler;

    private final int id;
    private final IBaubleType type;

    public SlotDefinition(IBaublesItemHandler baublesHandler, int id, IBaubleType type) {
        this.baublesHandler = baublesHandler;
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public IBaubleType getType() {
        return type;
    }
}
