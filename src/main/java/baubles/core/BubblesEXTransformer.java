package baubles.core;


import baubles.core.transformers.*;
import net.minecraft.launchwrapper.IClassTransformer;

@SuppressWarnings("unused")
public class BubblesEXTransformer implements IClassTransformer {

    private boolean isRLArtifact = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.startsWith("baubles.core"))
            return basicClass;


        switch (transformedName) {
            // Minecraft - Apply baubles enchantments
            case "net.minecraft.enchantment.Enchantment": return EnchantmentTransformer.transformEnchantment(basicClass);
            case "net.minecraft.item.Item": return EnchantmentTransformer.transformItem(basicClass);
            // Botania - Make slots not hardcoded
            case "vazkii.botania.common.item.equipment.bauble.ItemDivaCharm": return BotaniaTransformer.transformItemDivaCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTiara": return BotaniaTransformer.transformItemTiara(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemGoddessCharm": return BotaniaTransformer.transformItemGoddessCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemHolyCloak": return BotaniaTransformer.transformItemHolyCloak(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemMonocle": return BotaniaTransformer.transformItemMonocle(basicClass);
//            case "vazkii.botania.common.item.equipment.bauble.ItemTravelBelt": return BotaniaTransformer.transformItemTravelBelt(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemWaterRing": return BotaniaTransformer.transformItemWaterRing(basicClass);
//            case "vazkii.botania.common.item.equipment.bauble.ItemBauble": return BotaniaTransformer.transformItemBauble(basicClass);
            // Potion Fingers - Vazkii moment
            case "vazkii.potionfingers.ItemRing": return PotionFingersTransformer.transformItemRing(basicClass);
            // Quality Tools - Make it check if item has bauble capability and make it work with custom bauble types. And also make it work with slot definitions.
            case "com.tmtravlr.qualitytools.baubles.BaublesHandler": return QualityToolsTransformer.transformBaublesHandler(basicClass);
            // Wearable Backpacks - Fix a crash
            case "net.mcft.copy.backpacks.api.BackpackHelper": return WearableBackpacksTransformer.transformBackpackHelper(basicClass);

            //RLArtifacts
            // RLArtifacts - RLCraft moment
            case "artifacts.Artifacts": this.isRLArtifact = RLArtifactsTransformer.checkArtifacts(basicClass); return basicClass;
            case "artifacts.client.model.layer.LayerAmulet": return RLArtifactsTransformer.transformLayerAmulet(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerBelt": return RLArtifactsTransformer.transformLayerBelt(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerCloak": return RLArtifactsTransformer.transformLayerCloak(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerDrinkingHat": return RLArtifactsTransformer.transformLayerDrinkingHat(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerGloves": return RLArtifactsTransformer.transformLayerGloves(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerNightVisionGoggles": return RLArtifactsTransformer.transformLayerNightVisionGoggles(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerSnorkel": return RLArtifactsTransformer.transformLayerSnorkel(basicClass, this.isRLArtifact);
            case "artifacts.common.item.BaubleAmulet": return RLArtifactsTransformer.transformBaubleAmulet(basicClass, this.isRLArtifact);
            case "artifacts.common.item.BaubleBottledCloud": return RLArtifactsTransformer.transformBaubleBottledCloud(basicClass);


            default:
                return basicClass;
        }
    }
}