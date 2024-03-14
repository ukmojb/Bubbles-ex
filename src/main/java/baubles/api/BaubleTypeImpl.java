package baubles.api;

import baubles.common.Baubles;

public class BaubleTypeImpl implements IBaubleType {

    private final String name, translationKey, backgroundTexture;

    public BaubleTypeImpl(String name) {
        this.name = name;
        this.translationKey = Baubles.MODID + ".type." + name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public String getBackgroundTexture() {
        return backgroundTexture;
    }
}