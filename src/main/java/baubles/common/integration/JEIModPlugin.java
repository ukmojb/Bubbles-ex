package baubles.common.integration;

import baubles.client.gui.GuiPlayerExpanded;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@JEIPlugin
public class JEIModPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new BubblesGuiHandler());
    }

    public static class BubblesGuiHandler implements IAdvancedGuiHandler<GuiPlayerExpanded> {

        @Nonnull
        @Override
        public Class<GuiPlayerExpanded> getGuiContainerClass() {
            return GuiPlayerExpanded.class;
        }

        @Nullable
        @Override
        public List<Rectangle> getGuiExtraAreas(@Nonnull GuiPlayerExpanded gui) {
            List<Rectangle> rectangleList = new ArrayList<>();
            int add = gui.getBaubleSlots() > gui.getActualMaxBaubleSlots() ? -9 : 0;
            int add2 = gui.getBaubleSlots() > gui.getActualMaxBaubleSlots() ? 18 : 0;

            //potion
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                EntityPlayer player = Minecraft.getMinecraft().player;
                int height = 33;
                Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
                rectangleList.add(new Rectangle(0, gui.getGuiTop(), 32, height * Math.min(7, activePotionEffects.size())));
            }

            rectangleList.add(new Rectangle(gui.getGuiLeft() - 28, gui.getGuiTop() + add, 28, gui.getMaxY() + 9 + add2));

            return rectangleList;
        }
    }
}
