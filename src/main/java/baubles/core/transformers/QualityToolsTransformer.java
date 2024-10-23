package baubles.core.transformers;

import baubles.api.BaubleType;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class QualityToolsTransformer extends BaseTransformer {

    /**
     * Quality Tools checks if item is IBauble instead of checking capabilities.
     * Change it to check capabilities instead. Fixes issues with Wings and EbWizardry
     **/
    public static byte[] transformBaublesHandler(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        Iterator<MethodNode> mIterator = cls.methods.iterator();
        while (mIterator.hasNext()) {
            MethodNode method = mIterator.next();
            if (method.name.equals("canEquipBauble") || method.name.equals("getBaublesNamesForSlot")) mIterator.remove();
            else if (method.name.equals("applyAttributesFromBaubles")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESPECIAL) {
                        ((MethodInsnNode) node).desc = "(Lbaubles/api/cap/IBaublesItemHandler;I)Ljava/util/ArrayList;";
                        node = node.getPrevious();
                        method.instructions.insertBefore(node, new VarInsnNode(ALOAD, 3));
                        break;
                    }
                }
            }
        }
        { // canEquipBauble
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "canEquipBauble", "(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)Z", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, cls.name, "baublesExists", "Z");
            Label l_con1 = new Label();
            m.visitJumpInsn(IFEQ, l_con1);
            m.visitLabel(new Label());

            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(GETSTATIC, "baubles/api/cap/BaublesCapabilities", "CAPABILITY_ITEM_BAUBLE", "Lnet/minecraftforge/common/capabilities/Capability;");
            m.visitInsn(ACONST_NULL);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "getCapability", "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;", false);
            m.visitTypeInsn(CHECKCAST, "baubles/api/IBauble");
            m.visitVarInsn(ASTORE, 3); // Capability

            m.visitVarInsn(ALOAD, 3);
            Label l_con_cap_check = new Label();
            m.visitJumpInsn(IFNULL, l_con_cap_check);
            m.visitLabel(new Label());
            m.visitVarInsn(ALOAD, 3);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKEINTERFACE, "baubles/api/IBauble", "getType", "(Lnet/minecraft/item/ItemStack;)Lbaubles/api/IBaubleType;", true);
            m.visitVarInsn(ASTORE, 4); // Capabilities type

            m.visitVarInsn(ALOAD, 4);
            m.visitFieldInsn(GETSTATIC, "baubles/api/BaubleType", "TRINKET", "Lbaubles/api/BaubleType;");
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
            Label l_con_captrinket_check = new Label();
            m.visitJumpInsn(IFEQ, l_con_captrinket_check);
            m.visitLabel(new Label());
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);

            m.visitLabel(l_con_captrinket_check);
            m.visitFrame(F_APPEND, 2, new Object[] { "baubles/api/IBauble", "baubles/api/IBaubleType" }, 0, null);
            m.visitVarInsn(ALOAD, 2);
            m.visitLdcInsn("baubles_");
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
            Label l_con_swith = new Label();
            m.visitJumpInsn(IFEQ, l_con_swith);
            m.visitLabel(new Label());

            m.visitVarInsn(ALOAD, 2);
            m.visitIntInsn(BIPUSH, 8);
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
            m.visitMethodInsn(INVOKESTATIC, "baubles/api/BaubleType", "getType", "(Ljava/lang/String;)Lbaubles/api/IBaubleType;", false);
            m.visitVarInsn(ASTORE, 5); // IBaubleType

            m.visitVarInsn(ALOAD, 5);
            m.visitFieldInsn(GETSTATIC, "baubles/api/BaubleType", "TRINKET", "Lbaubles/api/BaubleType;");
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
            Label l_con_typetrinket_check = new Label();
            m.visitJumpInsn(IFEQ, l_con_typetrinket_check);
            m.visitLabel(new Label());
            m.visitInsn(ICONST_1);
            m.visitInsn(IRETURN);

            m.visitLabel(l_con_typetrinket_check);
            m.visitFrame(F_APPEND, 1, new Object[] { "baubles/api/IBaubleType" }, 0, null);
            m.visitVarInsn(ALOAD, 4);
            m.visitVarInsn(ALOAD, 5);
            m.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
            m.visitInsn(IRETURN);

            m.visitLabel(l_con_swith);
            m.visitFrame(F_CHOP, 1, null, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);

            m.visitLabel(l_con_cap_check);
            m.visitFrame(F_CHOP, 1, null, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);

            m.visitLabel(l_con1);
            m.visitFrame(F_CHOP, 1, null, 0, null);
            m.visitInsn(ICONST_0);
            m.visitInsn(IRETURN);
        }
        { // getBaublesNamesForSlot
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "getBaublesNamesForSlot", "(Lbaubles/api/cap/IBaublesItemHandler;I)Ljava/util/ArrayList;", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ILOAD, 2);
            m.visitMethodInsn(INVOKESTATIC, "baubles/core/transformers/QualityToolsTransformer", "$getBaublesNameForSlot", "(Lbaubles/api/cap/IBaublesItemHandler;I)Ljava/util/ArrayList;", false);
            m.visitInsn(ARETURN);
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static ArrayList<String> $getBaublesNameForSlot(IBaublesItemHandler handler, int slot) {
        ArrayList<String> list = new ArrayList<>();
        SlotDefinition definition = ((BaublesContainer) handler).getSlot(slot);
        for (Map.Entry<String, IBaubleType> type : BaubleType.getTypes().entrySet()) {
            if (definition.canPutType(type.getValue())) {
                list.add("baubles_" + type.getKey());
            }
        }
        return list;
    }
}
