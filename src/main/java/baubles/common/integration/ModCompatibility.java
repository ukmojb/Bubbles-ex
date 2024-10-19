package baubles.common.integration;

import baubles.api.BaubleType;
import baubles.api.cap.InjectableBauble;
import de.ellpeck.actuallyadditions.mod.items.ItemBattery;
import de.ellpeck.actuallyadditions.mod.items.ItemMagnetRing;
import de.ellpeck.actuallyadditions.mod.items.ItemPotionRing;
import lain.mods.cos.client.GuiCosArmorInventory;
import mod.acgaming.universaltweaks.config.UTConfigTweaks;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import yalter.mousetweaks.MTConfig;

public class ModCompatibility {

    public static final String
            MT = "mousetweaks",

            // No Recipe Book
            NRB = "norecipebook",
            UT = "universaltweaks",

            // Cosmetic Armor
            CA = "cosmeticarmorreworked",

            // Wings
            WINGS = "wings",

            // Compatibility
            AA = "actuallyadditions";

    // Mouse Tweaks scrolling
    public static boolean isMouseTweaksScrollingEnabled() {
        return Loader.isModLoaded(MT) && MTConfig.wheelTweak;
    }

    // No Recipe Book
    public static boolean isRecipeBookDisabled() {
        boolean disabled = Loader.isModLoaded(NRB);
        if (!disabled && Loader.isModLoaded(UT)) {
            disabled = UTConfigTweaks.MISC.utRecipeBookToggle;
        }
        return disabled;
    }

    // Cosmetic Armor
    public static boolean isCAInventory(Gui gui) {
        return Loader.isModLoaded(CA) && gui instanceof GuiCosArmorInventory;
    }

    // Wings
    public static void applyEvents() {
        if (Loader.isModLoaded(WINGS)) {
            MinecraftForge.EVENT_BUS.register(StupidWingsEvents.class);
        }
    }

    // Compatibility
    public static InjectableBauble getBaubleToInject(ItemStack stack) {
        Item item = stack.getItem();
        ResourceLocation loc = item.getRegistryName();
        if (loc == null) return null;
        if (loc.getNamespace().equals(AA)) {
            if (item instanceof ItemMagnetRing) {
                return new InjectableBauble(item, BaubleType.RING, true, 0);
            }
            else if (item instanceof ItemBattery) {
                return new InjectableBauble(item, BaubleType.TRINKET, true, 0);
            }
            else if (item instanceof ItemPotionRing && loc.getPath().endsWith("advanced")) {
                return new InjectableBauble(item, BaubleType.RING, true, 0);
            }
        }
        return null;
    }


}