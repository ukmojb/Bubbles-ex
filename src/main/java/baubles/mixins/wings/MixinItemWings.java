package baubles.mixins.wings;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import me.paulf.wings.server.item.ItemWings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemWings.class, remap = false)
public class MixinItemWings {

    @Inject(method = "onItemRightClick", at = @At(value = "HEAD", target = "Lme/paulf/wings/server/item/ItemWings;onItemRightClick(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    public void onItemRightClick(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        boolean canRight = true;
        ItemStack handStack = player.getHeldItem(hand);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlotAdaptability(i);
            if (stack.getItem() instanceof ItemWings) {
                canRight = false;
                break;
            }
        }

        if (!canRight) cir.setReturnValue(new ActionResult(EnumActionResult.SUCCESS, handStack));
    }
}
