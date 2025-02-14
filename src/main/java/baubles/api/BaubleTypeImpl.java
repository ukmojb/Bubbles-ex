package baubles.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaubleTypeImpl implements IBaubleType {

    private final ResourceLocation name;
    private final String translationKey;
    private final ResourceLocation textureLoc;

    public BaubleTypeImpl(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public BaubleTypeImpl(String name) {
        this(new ResourceLocation(name));
    }

    public BaubleTypeImpl(ResourceLocation name) {
        this.name = name;
        this.translationKey = name.getNamespace() + ".type." + name.getPath();
        this.textureLoc = new ResourceLocation(name.getNamespace(), "gui/slots/" + name.getPath());
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

    @Override
    public int getOrder() {
        return 0;
    }

    @Nullable
    @Override
    public ResourceLocation getBackgroundTexture() {
        return this.textureLoc;
    }
}