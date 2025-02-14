package baubles.api.cap;

public interface PlayerBaubleHandler extends IBaublesItemHandler {
    int getSlotByOffset(int slotIndex);

    void setOffset(int offset);

    void resetOffset();
}
