package baubles.api;

import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.api.inv.SlotDefinitionType;
import baubles.common.Baubles;
import baubles.common.Config;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BaubleTypeImpl implements IBaubleType {

    private final ResourceLocation name;
    private final String translationKey;

    public BaubleTypeImpl(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public BaubleTypeImpl(String name) {
        this(new ResourceLocation(name));
    }

    public BaubleTypeImpl(ResourceLocation name) {
        this.name = name;
        this.translationKey = name.getNamespace() + ".type." + name.getPath();
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

}