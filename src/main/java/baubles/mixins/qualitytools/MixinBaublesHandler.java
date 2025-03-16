package baubles.mixins.qualitytools;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.api.inv.SlotDefinitionType;
import baubles.common.Baubles;
import baubles.common.init.BaubleTypes;
import com.google.common.collect.Multimap;
import com.tmtravlr.qualitytools.QualityToolsHelper;
import com.tmtravlr.qualitytools.baubles.BaublesHandler;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Pseudo
@Mixin(value = BaublesHandler.class, remap = false)
public abstract class MixinBaublesHandler {

    @Shadow
    private boolean baublesExists;


    @Inject(method = "applyAttributesFromBaubles", at = @At(value = "HEAD", target = "Lcom/tmtravlr/qualitytools/baubles/BaublesHandler;applyAttributesFromBaubles(Lnet/minecraft/entity/player/EntityPlayer;Lcom/google/common/collect/Multimap;)V"), cancellable = true)
    public void applyAttributesFromBaubles(EntityPlayer player, Multimap<String, AttributeModifier> modifiersToRemove, CallbackInfo ci) {
        if (this.baublesExists) {
            IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(player);

            for (int i = 0; i < baublesHandler.getSlots(); ++i) {
                List<String> slotNames = this.getBaublesNameForSlot(baublesHandler, i);

                for (String slotName : slotNames) {

                    QualityToolsHelper.applyAttributesForSlot(player, baublesHandler.getStackInSlotAdaptability(i), slotName, modifiersToRemove);
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "canEquipBauble", at = @At(value = "HEAD", target = "Lcom/tmtravlr/qualitytools/baubles/BaublesHandler;canEquipBauble(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)Z"), cancellable = true)
    public void canEquipBauble(ItemStack stack, String slotName, CallbackInfoReturnable<Boolean> cir) {
        if (this.baublesExists && slotName.startsWith("baubles_")) {

            IBauble bauble0 = (IBauble) stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble0 != null && bauble0.getType(stack) == BaubleType.TRINKET) {
                cir.setReturnValue(true);
            }

            try {
                BaubleType baubleType = BaubleType.valueOf(slotName.substring("baubles_".length()).toUpperCase());
                if (baubleType != null && stack.getItem() instanceof IBauble) {
                    IBauble bauble = (IBauble)stack.getItem();
                    if (baubleType == BaubleType.TRINKET || bauble.getBaubleType(stack) == BaubleType.TRINKET || bauble.getBaubleType(stack) == baubleType) {
                        cir.setReturnValue(true);
                    }
                }
            } catch (IllegalArgumentException var5) {
            }
        }

        cir.setReturnValue(false);
    }


    private ArrayList<String> getBaublesNameForSlot(IBaublesItemHandler handler, int slot) {
        ArrayList<String> list = new ArrayList<>();
        SlotDefinition definition = handler.getRealSlot(slot);
        if (definition instanceof SlotDefinitionType) {
            for (Map.Entry<ResourceLocation, IBaubleType> type : BaubleTypes.getRegistryMap().entrySet()) {
                if (((SlotDefinitionType) definition).canPutType(type.getValue())) {
                    String name = type.getKey().getNamespace().equals(Baubles.MODID) ? type.getKey().getPath() : type.getKey().toString();
                    list.add("baubles_" + name);
                }
            }
        }
        return list;
    }
}
