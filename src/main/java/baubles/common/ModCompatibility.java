package baubles.common;

import mod.acgaming.universaltweaks.config.UTConfigTweaks;
import net.minecraftforge.fml.common.Loader;

public class ModCompatibility {

    public static final String
            NRB = "norecipebook",
            UT = "universaltweaks";

    public static boolean noRecipeBookDisabled() {
        boolean disabled = Loader.isModLoaded(NRB);
        if (!disabled && Loader.isModLoaded(UT)) {
            disabled = UTConfigTweaks.MISC.utRecipeBookToggle;
        }
        return disabled;
    }
}
