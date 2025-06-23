package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.common.integration.ModCompatibility;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * An implementation of {@link IBauble} to inject to items that normally aren't baubles.
 * It's encouraged to implement your own {@link IBauble} classes.
 * Used in {@link ModCompatibility#getBaubleToInject(ItemStack)}
 **/
public class InjectableBauble implements IBauble {

    private final Item item;
    private final IBaubleType type;

    private final boolean passive; // Don't apply effects as if it's hold in hand
    private final int armor; // 0-2   0 = Only inventory update | 1 = Only armor update | 2 = both

    public InjectableBauble(Item item, IBaubleType type, boolean passive, int armor) {
        this.item = item;
        this.type = type;
        this.passive = passive;
        this.armor = armor;
    }

    @Nonnull
    @Override
    public IBaubleType getType(ItemStack itemStack) {
        return this.type;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (this.armor == -1) return;
        if (this.armor != 1) item.onUpdate(itemstack, player.world, player, 0, !passive);
        if (this.armor != 0 && player instanceof EntityPlayer) item.onArmorTick(player.world, (EntityPlayer) player, itemstack);
    }
}
