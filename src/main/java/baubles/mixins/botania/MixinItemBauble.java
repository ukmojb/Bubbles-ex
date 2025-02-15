package baubles.mixins.botania;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;

import java.util.UUID;

@Mixin(value = ItemBauble.class, remap = false)
public class MixinItemBauble {

    @Inject(method = "getBaubleUUID", at = @At(value = "HEAD", target = "Lvazkii/botania/common/item/equipment/bauble/ItemBauble;getBaubleUUID(Lnet/minecraft/item/ItemStack;)Ljava/util/UUID;"), cancellable = true)
    private static void getBaubleUUID(ItemStack stack, CallbackInfoReturnable<UUID> cir) {
        long most = ItemNBTHelper.getLong(stack, "baubleUUIDMost", 0L);
        if (most == 0L) {
            UUID uuid = UUID.randomUUID();
            ItemNBTHelper.setLong(stack, "baubleUUIDMost", uuid.getMostSignificantBits());
            ItemNBTHelper.setLong(stack, "baubleUUIDLeast", uuid.getLeastSignificantBits());
            cir.setReturnValue(uuid);
        } else {
            long least = ItemNBTHelper.getLong(stack, "baubleUUIDLeast", 0L);
            cir.setReturnValue(new UUID(most, least));
        }
    }
}
