package baubles.client;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientEventHandler {
    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == Phase.START) {
            if (ClientProxy.KEY_BAUBLES.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
                PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
            }
        }
    }

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            IBauble bauble = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            BaubleType bt = bauble.getBaubleType(event.getItemStack());
            event.getToolTip().add(TextFormatting.GOLD + I18n.format("name." + bt));
        }
    }
}
