package baubles.api;

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
}