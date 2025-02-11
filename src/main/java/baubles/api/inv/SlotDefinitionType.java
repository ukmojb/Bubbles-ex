package baubles.api.inv;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.annotations.UnstableApi;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.init.SlotDefinitions;
import baubles.core.transformers.QualityToolsTransformer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Default implementation of {@link SlotDefinition}
 * Uses {@link IBaubleType}s for implementing methods.
 * Don't use this methods anywhere except for implementing a new {@link IBaublesItemHandler}
 **/
public class SlotDefinitionType implements SlotDefinition {

    private final int order;
    private final IBaubleType type;
    private final ResourceLocation backgroundTexture;

    public SlotDefinitionType(int order, IBaubleType type, ResourceLocation backgroundTexture) {
        this.order = order;
        this.type = type;
        this.backgroundTexture = backgroundTexture;
    }

    public SlotDefinitionType(int order, IBaubleType type) {
        this(order, type, new ResourceLocation(type.getRegistryName().getNamespace(), "gui/slots/" + type.getRegistryName().getPath()));
    }

    public SlotDefinitionType init(int slotIndex) {
        if (this.type instanceof BaubleType) ((BaubleType) this.type).addSlot(slotIndex);
        return this;
    }

    /**
     * Used for registering background textures
     * See {@link SlotDefinitions#registerTextures(TextureStitchEvent.Pre)}
     **/
    public ResourceLocation getBackgroundTexture() {
        return backgroundTexture;
    }

    /**
     * This is only here because of {@link QualityToolsTransformer#$getBaublesNameForSlot(IBaublesItemHandler, int)}.
     * For checking stacks use {@link SlotDefinition#canPutItem(int, ItemStack)}.
     * Use it at your own risk.
     **/
    @UnstableApi
    public boolean canPutType(IBaubleType type) {
        return this.type == BaubleType.TRINKET || type == BaubleType.TRINKET || this.type.equals(type);
    }

    /**
     * @see SlotDefinition#getRegistryName()
     */
    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return this.type.getRegistryName();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * @see SlotDefinition#getBackgroundTexture(int)
     **/
    @Nullable
    @Override
    public ResourceLocation getBackgroundTexture(int slotIndex) {
        return this.backgroundTexture;
    }

    /**
     * @see SlotDefinition#getTranslationKey(int)
     **/
    @Nonnull
    @Override
    public String getTranslationKey(int slotIndex) {
        return type.getTranslationKey();
    }

    /**
     * @see SlotDefinition#canPutItem(int, ItemStack)
     **/
    @Override
    public boolean canPutItem(int slotIndex, ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        IBaubleType type = Objects.requireNonNull(bauble).getType(stack);
        return this.canPutType(type);
    }
}