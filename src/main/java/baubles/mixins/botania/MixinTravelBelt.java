package baubles.mixins.botania;

import baubles.api.BaublesApi;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;
import vazkii.botania.common.item.equipment.bauble.ItemTravelBelt;

import java.util.List;


@Pseudo
@Mixin(value = ItemTravelBelt.class, remap = false)
public class MixinTravelBelt extends ItemBauble {



    @Shadow
    @Final
    public static List<String> playersWithStepup;

    public MixinTravelBelt(String name) {
        super(name);
    }


    @Inject(method = "updatePlayerStepStatus", at = @At(value = "HEAD", target = "Lvazkii/botania/common/item/equipment/bauble/ItemTravelBelt;updatePlayerStepStatus(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)V"), cancellable = true)
    public void updatePlayerStepStatus(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            String s = MixinPlayerStr(player);

            ItemStack belt = getTrueBelt(player);

            if (belt.isEmpty()) return;

            if (playersWithStepup.contains(s)) {
                if (shouldPlayerHaveStepup(player)) {
                    ItemTravelBelt beltItem = (ItemTravelBelt)belt.getItem();
                    if (player.world.isRemote) {
                        if ((player.onGround || player.capabilities.isFlying) && player.moveForward > 0.0F && !player.isInsideOfMaterial(Material.WATER)) {
                            float speed = beltItem.getSpeed(belt);
                            player.moveRelative(0.0F, 0.0F, 1.0F, speed);
                            beltItem.onMovedTick(belt, player);
                            if (player.ticksExisted % 10 == 0) {
                                ManaItemHandler.requestManaExact(belt, player, 1, true);
                            }
                        } else {
                            beltItem.onNotMovingTick(belt, player);
                        }
                    }

                    if (player.isSneaking()) {
                        player.stepHeight = 0.60001F;
                    } else {
                        player.stepHeight = 1.25F;
                    }
                } else {
                    player.stepHeight = 0.6F;
                    playersWithStepup.remove(s);
                }
            } else if (shouldPlayerHaveStepup(player)) {
                playersWithStepup.add(s);
                player.stepHeight = 1.25F;
            }
        }
        ci.cancel();
    }

    @Inject(method = "onPlayerJump", at = @At(value = "HEAD", target = "Lvazkii/botania/common/item/equipment/bauble/ItemTravelBelt;onPlayerJump(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingJumpEvent;)V"), cancellable = true)
    public void onPlayerJump(LivingEvent.LivingJumpEvent event, CallbackInfo ci) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            ItemStack belt = getTrueBelt(player);
            if (!belt.isEmpty() && belt.getItem() instanceof ItemTravelBelt && ManaItemHandler.requestManaExact(belt, player, 1, false)) {
                player.motionY += (double)((ItemTravelBelt)belt.getItem()).jump;
                player.fallDistance = -((ItemTravelBelt)belt.getItem()).fallBuffer;
            }
        }
        ci.cancel();
    }

    @Inject(method = "shouldPlayerHaveStepup", at = @At(value = "HEAD", target = "Lvazkii/botania/common/item/equipment/bauble/ItemTravelBelt;shouldPlayerHaveStepup(Lnet/minecraft/entity/player/EntityPlayer;)Z"), cancellable = true)
    public void InjectshouldPlayerHaveStepup(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack armor = getTrueBelt(player);
        if (armor.isEmpty()) cir.setReturnValue(false);
        cir.setReturnValue(!armor.isEmpty() && armor.getItem() instanceof ItemTravelBelt && ManaItemHandler.requestManaExact(armor, player, 1, false));
    }

    private ItemStack getTrueBelt(EntityPlayer player) {
        ItemStack belt = BaublesApi.getBaublesHandler(player).getStackInSlotAdaptability(BaublesApi.isBaubleEquipped(player, this));

        if (belt.isEmpty()) {
            belt = BaublesApi.getBaublesHandler(player).getStackInSlotAdaptability(BaublesApi.isBaubleEquipped(player, ModItems.superTravelBelt));
        }
        if (belt.isEmpty()) {
            belt = BaublesApi.getBaublesHandler(player).getStackInSlotAdaptability(BaublesApi.isBaubleEquipped(player, ModItems.speedUpBelt));
        }
        return belt;
    }

    public String MixinPlayerStr(EntityPlayer player) {
        return player.getGameProfile().getName() + ":" + player.world.isRemote;
    }

    private boolean shouldPlayerHaveStepup(EntityPlayer player) {
        ItemStack armor = getTrueBelt(player);
        if (armor.isEmpty()) return false;
        return !armor.isEmpty() && armor.getItem() instanceof ItemTravelBelt && ManaItemHandler.requestManaExact(armor, player, 1, false);
    }
}
