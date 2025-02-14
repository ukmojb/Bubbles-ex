package baubles.core.transformers;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class TrinketsAndBaublesTransformer extends BaseTransformer {

    /**
     * This fucking mod use setPlayer method which shouldn't even be used by mods.
     * Fucking mod developers, I swear.
     **/
    public static byte[] transformBaublesHelper(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.methods.removeIf(method -> method.name.equals("getBaublesHandler"));
        { // getBaublesHandler(EntityLivingBase)
            MethodVisitor method = cls.visitMethod(ACC_PUBLIC | ACC_STATIC, "getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;)Lbaubles/api/cap/IBaublesItemHandler;", null, null);
            method.visitVarInsn(ALOAD, 0);
            method.visitMethodInsn(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;)Lbaubles/api/cap/IBaublesItemHandler;", false);
            method.visitInsn(ARETURN);
        }
        { // getBaublesHandler(EntityLivingBase, Consumer)
            MethodVisitor method = cls.visitMethod(ACC_PUBLIC | ACC_STATIC, "getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;Ljava/util/function/Consumer;)Lbaubles/api/cap/IBaublesItemHandler;", null, null);
            method.visitVarInsn(ALOAD, 0);
            method.visitMethodInsn(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;)Lbaubles/api/cap/IBaublesItemHandler;", false);
            method.visitVarInsn(ASTORE, 2);
            method.visitVarInsn(ALOAD, 2);
            Label l_con_null = new Label();
            method.visitJumpInsn(IFNONNULL, l_con_null);
            method.visitInsn(ACONST_NULL);
            method.visitInsn(ARETURN);
            method.visitLabel(l_con_null);
            method.visitFrame(F_APPEND, 1, new Object[] { "baubles/api/cap/IBaublesItemHandler" }, 0, null);
            method.visitVarInsn(ALOAD, 1);
            method.visitVarInsn(ALOAD, 2);
            method.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
            method.visitVarInsn(ALOAD, 2);
            method.visitInsn(ARETURN);
        }
        { // getBaublesHandler(EntityLivingBase, Object, BiFunction)
            MethodVisitor method = cls.visitMethod(ACC_PUBLIC | ACC_STATIC, "getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;", null, null);
            method.visitVarInsn(ALOAD, 0);
            method.visitMethodInsn(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;)Lbaubles/api/cap/IBaublesItemHandler;", false);
            method.visitVarInsn(ASTORE, 3);
            method.visitVarInsn(ALOAD, 3);
            Label l_con_null = new Label();
            method.visitJumpInsn(IFNONNULL, l_con_null);
            method.visitInsn(ACONST_NULL);
            method.visitInsn(ARETURN);
            method.visitLabel(l_con_null);
            method.visitFrame(F_APPEND, 1, new Object[] { "baubles/api/cap/IBaublesItemHandler" }, 0, null);
            method.visitVarInsn(ALOAD, 2);
            method.visitVarInsn(ALOAD, 3);
            method.visitVarInsn(ALOAD, 1);
            method.visitMethodInsn(INVOKEINTERFACE, "java/util/function/BiFunction", "apply", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            method.visitInsn(ARETURN);
        }
        return write(cls);
    }

    public static byte[] transformTrinketGuiButton(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("drawButton", "func_191745_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == SIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new IntInsnNode(SIPUSH, 176 + i * 10));
                        list.add(new IntInsnNode(BIPUSH, 24));
                        method.instructions.insertBefore(node, list);
                        iterator.remove();
                        iterator.next();
                        iterator.remove();
                        if (i == 1) break;
                        i++;
                    }
                }
            }
        }
        return write(cls);
    }
}
