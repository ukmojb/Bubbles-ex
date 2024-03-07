package baubles.client;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.common.Baubles;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            IBauble bauble = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            BaubleType bt = bauble.getBaubleType(event.getItemStack());
            event.getToolTip().add(TextFormatting.GOLD + I18n.format("name." + bt));
        }
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/amulet"));
        map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/belt"));
        map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/body"));
        map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/charm"));
        map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/head"));
        map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/ring"));
    }
}
