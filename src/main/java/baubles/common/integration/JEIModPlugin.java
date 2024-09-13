package baubles.common.integration;

import baubles.client.gui.GuiPlayerExpanded;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
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
            int add = gui.getBaubleSlots() > gui.getActualMaxBaubleSlots() ? -9 : 0;
            int add2 = gui.getBaubleSlots() > gui.getActualMaxBaubleSlots() ? 18 : 0;
            return Collections.singletonList(new Rectangle(gui.getGuiLeft() - 26, gui.getGuiTop() + add, 26, gui.getMaxY() + add2));
        }
    }
}
