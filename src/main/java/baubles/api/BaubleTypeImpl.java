package baubles.api;

import javax.annotation.Nonnull;

public class BaubleTypeImpl implements IBaubleType {

    private final String name, translationKey, backgroundTexture;

    public BaubleTypeImpl(String name) {
        this.name = name;
        this.translationKey = "baubles.type." + name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nonnull
    @Override
    public String getBackgroundTexture() {
        return backgroundTexture;
    }
}