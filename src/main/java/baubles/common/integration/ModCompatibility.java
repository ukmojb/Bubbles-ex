package baubles.common.integration;

import lain.mods.cos.client.GuiCosArmorInventory;
import mod.acgaming.universaltweaks.config.UTConfigTweaks;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class ModCompatibility {

    public static final String
            NRB = "norecipebook",
            UT = "universaltweaks",
            CA = "cosmeticarmorreworked",
            WINGS = "wings";

    public static boolean isRecipeBookDisabled() {
        boolean disabled = Loader.isModLoaded(NRB);
        if (!disabled && Loader.isModLoaded(UT)) {
            disabled = UTConfigTweaks.MISC.utRecipeBookToggle;
        }
        return disabled;
    }

    public static boolean isCAInventory(Gui gui) {
        return Loader.isModLoaded(CA) && gui instanceof GuiCosArmorInventory;
    }

    public static void applyEvents() {
        if (Loader.isModLoaded(WINGS)) {
            MinecraftForge.EVENT_BUS.register(StupidWingsEvents.class);
        }
    }
}
