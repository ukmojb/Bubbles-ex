package baubles.core;

import baubles.core.transformers.BotaniaTransformer;
import baubles.core.transformers.QualityToolsTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class BubblesTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            // Botania
            case "vazkii.botania.common.item.equipment.bauble.ItemDivaCharm": return BotaniaTransformer.transformItemDivaCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTiara": return BotaniaTransformer.transformItemTiara(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemGoddessCharm": return BotaniaTransformer.transformItemGoddessCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemHolyCloak": return BotaniaTransformer.transformItemHolyCloak(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemMonocle": return BotaniaTransformer.transformItemMonocle(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTravelBelt": return BotaniaTransformer.transformItemTravelBelt(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemWaterRing": return BotaniaTransformer.transformItemWaterRing(basicClass);
            // Quality Tools
            case "com.tmtravlr.qualitytools.baubles.BaublesHandler": return QualityToolsTransformer.transformBaublesHandler(basicClass);
            default: return basicClass;
        }
    }
}
