package baubles.core;

import baubles.core.transformers.*;
import net.minecraft.launchwrapper.IClassTransformer;

@SuppressWarnings("unused")
public class BubblesTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            // Minecraft - Apply baubles enchantments
            case "net.minecraft.enchantment.Enchantment": return EnchantmentTransformer.transformEnchantment(basicClass);
            // Botania - Make slots not hardcoded
            case "vazkii.botania.common.item.equipment.bauble.ItemDivaCharm": return BotaniaTransformer.transformItemDivaCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTiara": return BotaniaTransformer.transformItemTiara(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemGoddessCharm": return BotaniaTransformer.transformItemGoddessCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemHolyCloak": return BotaniaTransformer.transformItemHolyCloak(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemMonocle": return BotaniaTransformer.transformItemMonocle(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTravelBelt": return BotaniaTransformer.transformItemTravelBelt(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemWaterRing": return BotaniaTransformer.transformItemWaterRing(basicClass);
            // Potion Fingers - Vazkii moment
            case "vazkii.potionfingers.ItemRing": return PotionFingersTransformer.transformItemRing(basicClass);
            // Quality Tools - Make it check if item has bauble capability and make it work with custom bauble types. And also make it work with slot definitions.
            case "com.tmtravlr.qualitytools.baubles.BaublesHandler": return QualityToolsTransformer.transformBaublesHandler(basicClass);
            // Wearable Backpacks - Fix a crash
            case "net.mcft.copy.backpacks.api.BackpackHelper": return WearableBackpacksTransformer.transformBackpackHelper(basicClass);
            default: return basicClass;
        }
    }
}
