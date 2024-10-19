package baubles.core;

import baubles.core.transformers.QualityToolsTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class BubblesTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            // Quality Tools
            case "com.tmtravlr.qualitytools.baubles.BaublesHandler": return QualityToolsTransformer.transformBaublesHandler(basicClass);
            default: return basicClass;
        }
    }
}
