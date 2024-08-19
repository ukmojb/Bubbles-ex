package baubles.client;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import baubles.client.gui.GuiBaublesButton;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Objects;

@SuppressWarnings("unused") // gets used by Forge event handler
public class ClientEventHandler {

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            IBauble bauble = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            IBaubleType bt = Objects.requireNonNull(bauble).getType(event.getItemStack());
            event.getToolTip().add(TextFormatting.GOLD + I18n.format("baubles.item.desc", I18n.format(bt.getTranslationKey())));
        }
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (String type : BaubleType.getTypes().keySet()) {
            map.registerSprite(new ResourceLocation(Baubles.MODID, "gui/slots/" + type));
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().inGameHasFocus && ClientProxy.KEY_BAUBLES.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
        }
    }

    @SubscribeEvent
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && BaublesApi.getBaublesHandler(player).getSlots() > 0) {
            if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiPlayerExpanded) {
                GuiContainer gui = (GuiContainer) event.getGui();
                event.getButtonList().add(new GuiBaublesButton(55, gui, 64, 9, 10, 10,
                        I18n.format((event.getGui() instanceof GuiInventory) ? "button.baubles" : "button.normal")));
            }
        }
    }
}
