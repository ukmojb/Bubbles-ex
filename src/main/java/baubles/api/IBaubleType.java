package baubles.api;

public interface IBaubleType {

    /**
     * Translation key of the bauble type. A bit more extensible version of "baubles.AMULET"
     **/
    String getTranslationKey();

    /**
     * The texture used for slots background shape
     **/
    String getBackgroundTexture();
}