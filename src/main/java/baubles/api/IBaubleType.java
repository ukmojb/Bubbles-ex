package baubles.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IBaubleType {

    /**
     * Name of the bauble type.
     **/
    @Nonnull
    String getName();

    /**
     * Translation key of the bauble type. A bit more extensible version of "baubles.AMULET"
     **/
    @Nonnull
    String getTranslationKey();

    /**
     * The texture used for slots background shape
     **/
    @Nonnull
    String getBackgroundTexture();

    /**
     * Use this if you want to add type to the list of {@link Enchantment#getEntityEquipment(EntityLivingBase)}.
     * You should use {@link Item#canApplyAtEnchantingTable(ItemStack, Enchantment)} for specific items.
     **/
    default boolean canApplyEnchantment(Enchantment enchantment, ItemStack stack) {
        if (enchantment.type == null) return false;
        EnumEnchantmentType type = enchantment.type;
        return type == EnumEnchantmentType.ALL || type == EnumEnchantmentType.WEARABLE;
    }
}