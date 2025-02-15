package baubles.mixins.rlartifacts;

import artifacts.client.model.ModelNightVisionGoggles;
import artifacts.client.model.ModelSnorkel;
import artifacts.client.model.layer.*;
import artifacts.common.init.ModItems;
import artifacts.common.util.RenderHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/*
* Fuck you RL team!
*/
@Mixin(value = LayerBelt.class, remap = false)
abstract class MixinLayerBELT {

    @Shadow @Nullable protected abstract ResourceLocation getTextures(ItemStack stack);

    @Shadow @Final private static ModelBase SKULL_MODEL;

    @Shadow @Final private static ModelBase BOTTLE_MODEL;

    @Shadow @Final private static ModelBase ANTIDOTE_MODEL;

    @Shadow @Final private static ModelBase BUBBLE_MODEL;

    @Inject(method = "setTexturesGetModel", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerBelt;setTexturesGetModel(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/client/model/ModelBase;"), cancellable = true)
    private void setTexturesGetModel(EntityPlayer player, CallbackInfoReturnable<ModelBase> cir) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < BaubleType.BELT.getValidSlots(player).length; i++) {
            ItemStack stack = handler.getStackInSlot(BaubleType.BELT.getValidSlots()[i]);
            if (RenderHelper.shouldItemStackRender(player, stack)) {
                ResourceLocation textures = getTextures(stack);
                if (textures != null) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
                    if (stack.getItem() == ModItems.ANTIDOTE_VESSEL) {
                        cir.setReturnValue(ANTIDOTE_MODEL);
                    } else if (stack.getItem() == ModItems.BUBBLE_WRAP) {
                        cir.setReturnValue(BUBBLE_MODEL);
                    } else {
                        cir.setReturnValue(stack.getItem() == ModItems.OBSIDIAN_SKULL ? SKULL_MODEL : BOTTLE_MODEL);
                    }
                }
            }
        }

        cir.setReturnValue(null);
    }
}

@Mixin(value = LayerAmulet.class, remap = false)
abstract class MixinLayerAmulet {

    @Shadow
    @Nullable
    protected abstract ResourceLocation getTextures(ItemStack stack);

    @Shadow
    @Final
    private static ModelBase PANIC_MODEL;

    @Shadow
    @Final
    private static ModelBase ULTIMATE_MODEL;

    @Shadow
    @Final
    private static ModelBase AMULET_MODEL;

    @Inject(method = "setTexturesGetModel", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerAmulet;setTexturesGetModel(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/client/model/ModelBase;"), cancellable = true)
    private void setTexturesGetModel(EntityPlayer player, CallbackInfoReturnable<ModelBase> cir) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < BaubleType.AMULET.getValidSlots(player).length; i++) {
            ItemStack stack = handler.getStackInSlot(BaubleType.AMULET.getValidSlots(player)[i]);
            if (RenderHelper.shouldItemStackRender(player, stack)) {
                ResourceLocation textures = getTextures(stack);
                if (textures != null) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
                    if (stack.getItem() == ModItems.PANIC_NECKLACE) {
                        cir.setReturnValue(PANIC_MODEL);
                    } else {
                        cir.setReturnValue(stack.getItem() == ModItems.ULTIMATE_PENDANT ? ULTIMATE_MODEL : AMULET_MODEL);
                    }
                }
            }
        }

        cir.setReturnValue(null);
    }
}

@Mixin(value = LayerGloves.class, remap = false)
abstract class MixinLayerGloves {


    @Shadow @Nullable protected abstract ResourceLocation getTextures(ItemStack stack);

    @Shadow @Nullable protected abstract ResourceLocation getOverlayTextures(ItemStack stack);

    @Inject(method = "setTextures", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerGloves;setTextures(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHandSide;Z)Z"), cancellable = true)
    private void setTextures(EntityPlayer player, EnumHandSide hand, boolean overlay, CallbackInfoReturnable<Boolean> cir) {
//        ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.RING.getValidSlots(player)[hand == EnumHandSide.LEFT ? 0 : 1]);

        if (BaubleType.RING.getValidSlots(player).length > 2) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 2; i < BaubleType.RING.getValidSlots(player).length; i++) {
                ItemStack stack = handler.getStackInSlot(BaubleType.RING.getValidSlots(player)[i]);
//                if (player.world.getTotalWorldTime() % 10 == 0) System.out.println(stack.getDisplayName() + "--" + BaubleType.RING.getValidSlots(player)[i]);
                if (!RenderHelper.shouldItemStackRender(player, stack)) {
                    cir.setReturnValue(false);
                } else {
                    ResourceLocation textures = overlay ? this.getOverlayTextures(stack) : this.getTextures(stack);
                    if (textures != null) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
                        cir.setReturnValue(true);
                    } else {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}

@Mixin(value = LayerDrinkingHat.class, remap = false)
abstract class MixinLayerDrinkingHat extends LayerBauble {


    @Shadow
    @Final
    private static ModelBase hat;


    public MixinLayerDrinkingHat(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Inject(method = "renderLayer", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerDrinkingHat;renderLayer(Lnet/minecraft/entity/player/EntityPlayer;FFFFFFF)V"), cancellable = true)
    protected void renderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (BaublesApi.isBaubleEquipped(player, ModItems.DRINKING_HAT) != -1 && RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.HEAD)) {

            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            ItemStack stack = handler.getStackInSlot(BaublesApi.isBaubleEquipped(player, ModItems.DRINKING_HAT));
            if (stack.getItem() == ModItems.DRINKING_HAT && RenderHelper.shouldItemStackRender(player, stack)) {
                if (player.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                this.modelPlayer.bipedHead.postRender(scale);
                hat.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
        }
        ci.cancel();
    }
}


@Mixin(value = LayerNightVisionGoggles.class, remap = false)
abstract class MixinLayerNightVisionGoggles extends LayerBauble {

    @Shadow
    @Final
    private static ModelNightVisionGoggles goggles;

    public MixinLayerNightVisionGoggles(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Inject(method = "renderLayer", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerNightVisionGoggles;renderLayer(Lnet/minecraft/entity/player/EntityPlayer;FFFFFFF)V"), cancellable = true)
    protected void renderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (BaublesApi.isBaubleEquipped(player, ModItems.NIGHT_VISION_GOGGLES) != -1 && RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.HEAD)) {

            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            ItemStack stack = handler.getStackInSlot(BaublesApi.isBaubleEquipped(player, ModItems.NIGHT_VISION_GOGGLES));
            if (stack.getItem() == ModItems.NIGHT_VISION_GOGGLES && RenderHelper.shouldItemStackRender(player, stack)) {
                if (player.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                this.modelPlayer.bipedHead.postRender(scale);
                goggles.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            }
        }
        ci.cancel();
    }
}

@Mixin(value = LayerSnorkel.class, remap = false)
abstract class MixinLayerSnorkel extends LayerBauble {


    @Shadow
    @Final
    private static ModelSnorkel snorkel;

    public MixinLayerSnorkel(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Inject(method = "renderLayer", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerNightVisionGoggles;renderLayer(Lnet/minecraft/entity/player/EntityPlayer;FFFFFFF)V"), cancellable = true)
    protected void renderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (BaublesApi.isBaubleEquipped(player, ModItems.SNORKEL) != -1) {
            boolean renderFull = RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.HEAD);
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            ItemStack stack = handler.getStackInSlot(BaublesApi.isBaubleEquipped(player, ModItems.SNORKEL));
            if (stack.getItem() == ModItems.SNORKEL && RenderHelper.shouldItemStackRender(player, stack)) {
                if (player.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                this.modelPlayer.bipedHead.postRender(scale);
                snorkel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, renderFull);
            }
        }
        ci.cancel();
    }
}


@Mixin(value = LayerCloak.class, remap = false)
abstract class MixinLayerCloak {


    @Shadow
    protected abstract void renderBody(EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean hoodUp);

    @Shadow
    protected abstract void renderHead(EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean hoodUp);

    @Shadow
    @Final
    private static ResourceLocation CLOAK_NORMAL;

    @Shadow
    @Final
    private static ResourceLocation CLOAK_OVERLAY;

    @Inject(method = "renderChest", at = @At(value = "HEAD", target = "Lartifacts/client/model/layer/LayerCloak;renderChest(Lnet/minecraft/entity/player/EntityPlayer;FFFFFF)V"), cancellable = true)
    private void renderChest(EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (BaublesApi.isBaubleEquipped(player, ModItems.STAR_CLOAK) != -1 && RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.CHEST)) {

            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            ItemStack stack = handler.getStackInSlot(BaublesApi.isBaubleEquipped(player, ModItems.STAR_CLOAK));
            if (stack.getItem() == ModItems.STAR_CLOAK && RenderHelper.shouldItemStackRender(player, stack)) {
                if (player.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                }

                boolean hoodUp = RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.HEAD) && (BaublesApi.isBaubleEquipped(player, ModItems.DRINKING_HAT) == -1 || !RenderHelper.shouldItemStackRender(player, BaublesApi.getBaublesHandler(player).getStackInSlot(BaublesApi.isBaubleEquipped(player, ModItems.STAR_CLOAK))));
                Minecraft.getMinecraft().getTextureManager().bindTexture(CLOAK_NORMAL);
                this.renderBody(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
                this.renderHead(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
                float lastLightmapX = OpenGlHelper.lastBrightnessX;
                float lastLightmapY = OpenGlHelper.lastBrightnessY;
                int light = 15728880;
                int lightmapX = light % 65536;
                int lightmapY = light / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX, (float)lightmapY);
                Minecraft.getMinecraft().getTextureManager().bindTexture(CLOAK_OVERLAY);
                this.renderBody(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
                this.renderHead(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);

            }

        }
        ci.cancel();
    }
}