package baubles.core.transformers;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * Fuck you Vazkii!!
 **/
public class PotionFingersTransformer extends BaseTransformer {

    public static byte[] transformItemRing(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        boolean wornTickExists = false;
        Iterator<MethodNode> iterator = cls.methods.iterator();
        while (iterator.hasNext()) {
            MethodNode method = iterator.next();
            if (method.name.equals("updatePotionStatus") || method.name.equals("onUnequipped") || method.name.equals("onEquipped")) iterator.remove();
            else if (method.name.equals("onWornTick")) wornTickExists = true;
        }
        cls.methods.removeIf(m -> m.name.equals("updatePotionStatus") || m.name.equals("onUnequipped") || m.name.equals("onEquipped"));
        { // onUnequipped
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "onUnequipped", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
            m.visitInsn(ICONST_1);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
            m.visitInsn(RETURN);
        }
        if (!wornTickExists) {
            { // onWornTick
                MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "onWornTick", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 2);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
                m.visitInsn(ICONST_0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
                m.visitInsn(RETURN);
            }
        }
        { // updatePotionStatus
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", null, null);
            m.visitVarInsn(ALOAD, 2);
            Label l_con_pnull = new Label();
            m.visitJumpInsn(IFNONNULL, l_con_pnull);
            m.visitInsn(RETURN);
            m.visitLabel(l_con_pnull);
            m.visitFrame(F_SAME, 0, null, 0, null);

            String entityLivingBase = "net/minecraft/entity/EntityLivingBase";
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKEVIRTUAL, entityLivingBase, getName("getActivePotionEffect", "func_70660_b"), "(Lnet/minecraft/potion/Potion;)Lnet/minecraft/potion/PotionEffect;", false);
            m.visitVarInsn(ASTORE, 4); // currentEffect

            m.visitVarInsn(ILOAD, 3);
            Label l_con = new Label();
            m.visitJumpInsn(IFNE, l_con);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(GETFIELD, entityLivingBase, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
            m.visitFieldInsn(GETFIELD, "net/minecraft/world/World", getName("isRemote", "field_72995_K"), "Z");
            Label l_con_remote = new Label();
            m.visitJumpInsn(IFNE, l_con_remote);
            Label l_con_effectCheck = new Label();
            m.visitVarInsn(ALOAD, 4);
            Label l_con_or = new Label();
            m.visitJumpInsn(IFNONNULL, l_con_effectCheck);
            m.visitLabel(l_con_or);
            m.visitFrame(F_APPEND, 1, new Object[] { "net/minecraft/potion/PotionEffect" }, 0, null);

            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(NEW, "net/minecraft/potion/PotionEffect");
            m.visitInsn(DUP);
            m.visitVarInsn(ALOAD, 2);
            m.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I");
            m.visitInsn(ICONST_0);
            m.visitInsn(ICONST_1);
            m.visitInsn(ICONST_0);
            m.visitMethodInsn(INVOKESPECIAL, "net/minecraft/potion/PotionEffect", "<init>", "(Lnet/minecraft/potion/Potion;IIZZ)V", false);
            m.visitMethodInsn(INVOKEVIRTUAL, entityLivingBase, getName("addPotionEffect", "func_70690_d"), "(Lnet/minecraft/potion/PotionEffect;)V", false);
            m.visitInsn(RETURN);

            m.visitLabel(l_con_effectCheck);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 4);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/potion/PotionEffect", getName("getAmplifier", "func_76458_c"), "()I", false);
            m.visitInsn(ICONST_0);
            Label l_con_end = new Label();
            m.visitJumpInsn(IF_ICMPEQ, l_con_end);
            m.visitVarInsn(ALOAD, 4);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/potion/PotionEffect", getName("getDuration", "func_76459_b"), "()I", false);
            m.visitIntInsn(SIPUSH, 32767);
            m.visitJumpInsn(IF_ICMPGT, l_con_or);
            m.visitLabel(l_con_end);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);

            m.visitLabel(l_con_remote);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);

            m.visitLabel(l_con);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKEVIRTUAL, entityLivingBase, getName("removePotionEffect", "func_184589_d"), "(Lnet/minecraft/potion/Potion;)V", false);

            m.visitInsn(RETURN);
        }
        { // Fallback for the Pansmith's fork
           MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Lnet/minecraft/item/ItemStack;Z)V", null, null);
           m.visitVarInsn(ALOAD, 0);
           m.visitVarInsn(ALOAD, 1);
           m.visitVarInsn(ALOAD, 2);
           m.visitVarInsn(ILOAD, 4);
           m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
           m.visitInsn(RETURN);
        }
        writeClass(cls);
        return write(cls);
    }
}