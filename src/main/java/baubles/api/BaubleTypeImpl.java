package baubles.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

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