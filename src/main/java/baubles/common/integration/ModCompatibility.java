package baubles.common.integration;

import baubles.api.BaubleType;
import baubles.api.cap.InjectableBauble;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Baubles;
import com.google.common.collect.ImmutableMap;
import de.ellpeck.actuallyadditions.mod.items.ItemBattery;
import de.ellpeck.actuallyadditions.mod.items.ItemMagnetRing;
import de.ellpeck.actuallyadditions.mod.items.ItemPotionRing;
import lain.mods.cos.client.GuiCosArmorInventory;
import mod.acgaming.universaltweaks.config.UTConfigTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.lwjgl.input.Mouse;
import snownee.minieffects.handlers.InjectedMiniEffects;
import snownee.minieffects.handlers.MiniEffectsOffsets;
import yalter.mousetweaks.MTConfig;

import java.lang.reflect.Field;
import java.util.Map;

public class ModCompatibility {

    private static Field f_OFFSETS = null;
    private static Field f_miniEffects = null;

    public static final String
            // Scrolling check
            MT = "mousetweaks",

            // No Recipe Book
            NRB = "norecipebook",
            UT = "universaltweaks",

            // Cosmetic Armor
            CA = "cosmeticarmorreworked",

            // Wings
            WINGS = "wings",

            // Mini Effects
            ME = "minieffects",

            // Compatibility
            AA = "actuallyadditions"
    ;

    // Mouse Tweaks scrolling
    public static boolean MT$shouldScroll(Slot slot) {
        if (!Loader.isModLoaded(MT)) return true;
        boolean wheelTweaksEnabled = MTConfig.wheelTweak;
        return !wheelTweaksEnabled || slot == null || !slot.getHasStack();
    }

    // No Recipe Book
    public static boolean RecipeBook$isDisabled() {
        boolean disabled = Loader.isModLoaded(NRB);
        if (!disabled && Loader.isModLoaded(UT)) {
            disabled = UTConfigTweaks.MISC.utRecipeBookToggle;
        }
        return disabled;
    }

    // Cosmetic Armor
    public static boolean CA$isCAInventory(Gui gui) {
        return Loader.isModLoaded(CA) && gui instanceof GuiCosArmorInventory;
    }

    // Wings
    public static void Wings$applyEvents() {
        if (Loader.isModLoaded(WINGS)) {
            MinecraftForge.EVENT_BUS.register(StupidWingsEvents.class);
        }
    }

    // Mini Effects
    public static boolean ME$shouldMoveLeft(InventoryEffectRenderer gui) {
        if (!Loader.isModLoaded(ME)) return true;
        if (!ME$checkMiniEffectIsLegacy()) return true;
        if (f_miniEffects == null) {
            try {
                f_miniEffects = InventoryEffectRenderer.class.getDeclaredField("mini$effects");
                f_miniEffects.setAccessible(true);
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            InjectedMiniEffects miniEffects = (InjectedMiniEffects) f_miniEffects.get(gui);
            return miniEffects.shouldExpand(Minecraft.getMinecraft(), Mouse.getX(), Mouse.getY());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ME$applyOffset() {
        if (!Loader.isModLoaded(ModCompatibility.ME)) return;
        if (!ME$checkMiniEffectIsLegacy()) return;
        if (MiniEffectsOffsets.get(GuiPlayerExpanded.class) != null) return;
        if (f_OFFSETS == null) {
            try {
                f_OFFSETS = MiniEffectsOffsets.class.getDeclaredField("OFFSETS");
                f_OFFSETS.setAccessible(true);
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            ImmutableMap.Builder<Class<?>, MiniEffectsOffsets.Vec2i> builder = new ImmutableMap.Builder<>();
            builder.putAll((Map<Class<?>, MiniEffectsOffsets.Vec2i>) f_OFFSETS.get(null));
            builder.put(GuiPlayerExpanded.class, new MiniEffectsOffsets.Vec2i(-28, 0));
            f_OFFSETS.set(null, builder.build());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean ME$checkMiniEffectIsLegacy(){
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if (mod.getModId().equals(ME)) {
                if (mod.getName().equals("Mini Effects - Legacy Extended Life")) {
                    return true;
                } else if (mod.getName().equals("Mini Effects")){
                    Baubles.log.fatal("Baubles needs MiniEffects-Legacy Extended Life, not MiniEffects. Download link https://www.curseforge.com/minecraft/mc-mods/minieffects-legacy-extended-life");
                    Baubles.log.fatal("饰品栏需要的是MiniEffects-Legacy Extended Life不是Mini Effects，下载链接https://www.curseforge.com/minecraft/mc-mods/minieffects-legacy-extended-life");
                }
            }
        }
        return false;
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