package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Pretty basic implementation of a bauble item
 **/
public class BaubleItem extends Item implements IBauble {
    private final BaubleType baubleType;

    public BaubleItem(BaubleType type) {
        baubleType = type;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return baubleType;
    }
}
