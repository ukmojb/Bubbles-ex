package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.common.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A basic example for a bauble item.
 * You're free to use this as a template.
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

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (!Config.rightClickEquipped) return super.onItemRightClick(worldIn, playerIn, handIn);

        boolean check = false;
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(playerIn);
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.isItemValidForSlot(i, stack, playerIn)) {
                check = true;
                if (!worldIn.isRemote) {
                    handler.setStackInSlot(i, stack.copy());
                    stack.shrink(stack.getCount());
                }
                break;
            }
        }
        this.onEquipped(stack, playerIn);
        if (check) return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        else return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
