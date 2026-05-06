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

    private static final String ENTITY_LIVING_BASE = "net/minecraft/entity/EntityLivingBase";
    private static final String ENTITY_PLAYER = "net/minecraft/entity/player/EntityPlayer";
    private static final String ITEM_STACK = "net/minecraft/item/ItemStack";
    private static final String POTION = "net/minecraft/potion/Potion";
    private static final String POTION_EFFECT = "net/minecraft/potion/PotionEffect";
    private static final String WORLD = "net/minecraft/world/World";
    private static final String ITEM_RING = "vazkii/potionfingers/ItemRing";
    private static final String BAUBLES_API = "baubles/api/BaublesApi";
    private static final String BAUBLES_HANDLER = "baubles/api/cap/IBaublesItemHandler";

    public static byte[] transformItemRing(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        boolean wornTickExists = removeExistingMethods(cls);

        generateOnEquipped(cls);
        generateOnUnequipped(cls);

        if (!wornTickExists) {
            generateOnWornTick(cls);
        }

        generateUpdatePotionStatus(cls);
        generatePansmithForkFallback(cls);

        writeClass(cls);
        return write(cls);
    }

    private static boolean removeExistingMethods(ClassNode cls) {
        boolean wornTickExists = false;
        Iterator<MethodNode> iterator = cls.methods.iterator();

        while (iterator.hasNext()) {
            MethodNode method = iterator.next();
            if (method.name.equals("updatePotionStatus") ||
                method.name.equals("onUnequipped") ||
                method.name.equals("onEquipped")) {
                iterator.remove();
            } else if (method.name.equals("onWornTick")) {
                wornTickExists = true;
            }
        }

        cls.methods.removeIf(m -> m.name.equals("updatePotionStatus") ||
                                 m.name.equals("onUnequipped") ||
                                 m.name.equals("onEquipped"));

        return wornTickExists;
    }

    private static void generateOnEquipped(ClassNode cls) {
        MethodVisitor mv = cls.visitMethod(ACC_PUBLIC, "onEquipped",
            "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion",
            "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus",
            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();
    }

    private static void generateOnUnequipped(ClassNode cls) {
        MethodVisitor mv = cls.visitMethod(ACC_PUBLIC, "onUnequipped",
            "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion",
            "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus",
            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();
    }

    private static void generateOnWornTick(ClassNode cls) {
        MethodVisitor mv = cls.visitMethod(ACC_PUBLIC, "onWornTick",
            "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion",
            "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus",
            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();
    }

    private static void generateUpdatePotionStatus(ClassNode cls) {
        MethodVisitor mv = cls.visitMethod(ACC_PUBLIC, "updatePotionStatus",
            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", null, null);

        // Null check
        mv.visitVarInsn(ALOAD, 2);
        Label notNullLabel = new Label();
        mv.visitJumpInsn(IFNONNULL, notNullLabel);
        mv.visitInsn(RETURN);
        mv.visitLabel(notNullLabel);
        mv.visitFrame(F_SAME, 0, null, 0, null);

        // Server side check
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(GETFIELD, ENTITY_LIVING_BASE,
            getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
        mv.visitFieldInsn(GETFIELD, WORLD,
            getName("isRemote", "field_72995_K"), "Z");
        Label remoteLabel = new Label();
        mv.visitJumpInsn(IFNE, remoteLabel);

        // Check equip flag
        mv.visitVarInsn(ILOAD, 3);
        Label unequipLabel = new Label();
        mv.visitJumpInsn(IFNE, unequipLabel);

        // Equip logic
        mv.visitInsn(ICONST_M1);
        mv.visitVarInsn(ISTORE, 4);

        // Player check
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, ENTITY_PLAYER);
        Label playerCheckLabel = new Label();
        mv.visitJumpInsn(IFEQ, playerCheckLabel);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, ENTITY_PLAYER);
        mv.visitMethodInsn(INVOKESTATIC, BAUBLES_API, "getBaublesHandler",
            "(Lnet/minecraft/entity/player/EntityPlayer;)Lbaubles/api/cap/IBaublesItemHandler;", false);
        mv.visitVarInsn(ASTORE, 5);

        // Loop counter init
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 6);

        // Loop start
        Label loopLabel = new Label();
        Label loopContinueLabel = new Label();
        mv.visitLabel(loopLabel);
        mv.visitFrame(F_APPEND, 3, new Object[] { INTEGER, BAUBLES_HANDLER, INTEGER }, 0, null);

        mv.visitVarInsn(ILOAD, 6);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEINTERFACE, BAUBLES_HANDLER, "getSlots", "()I", true);
        mv.visitJumpInsn(IF_ICMPEQ, loopContinueLabel);

        mv.visitVarInsn(ALOAD, 5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitMethodInsn(INVOKEINTERFACE, BAUBLES_HANDLER, "getStackInSlot",
            "(I)Lnet/minecraft/item/ItemStack;", true);
        mv.visitVarInsn(ASTORE, 7);

        // Check if ring
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, ITEM_STACK,
            getName("getItem", "func_77973_b"), "()Lnet/minecraft/item/Item;", false);
        mv.visitTypeInsn(INSTANCEOF, ITEM_RING);
        Label instanceLabel = new Label();
        mv.visitJumpInsn(IFEQ, instanceLabel);

        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKESTATIC, ITEM_RING, "getPotion",
            "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitJumpInsn(IF_ACMPNE, instanceLabel);
        mv.visitIincInsn(4, 1);

        mv.visitLabel(instanceLabel);
        mv.visitFrame(F_APPEND, 1, new Object[] { ITEM_STACK }, 0, null);
        mv.visitIincInsn(6, 1);
        mv.visitJumpInsn(GOTO, loopLabel);

        // Loop end
        mv.visitLabel(loopContinueLabel);
        mv.visitFrame(F_CHOP, 3, null, 0, null);

        // Player check end (apply potion effect)
        mv.visitLabel(playerCheckLabel);
        mv.visitFrame(F_SAME, 0, null, 0, null);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(NEW, POTION_EFFECT);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I");
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKESPECIAL, POTION_EFFECT, "<init>",
            "(Lnet/minecraft/potion/Potion;IIZZ)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, ENTITY_LIVING_BASE,
            getName("addPotionEffect", "func_70690_d"),
            "(Lnet/minecraft/potion/PotionEffect;)V", false);
        mv.visitInsn(RETURN);

        // Unequip logic
        mv.visitLabel(unequipLabel);
        mv.visitFrame(F_CHOP, 1, null, 0, null);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, ENTITY_LIVING_BASE,
            getName("removePotionEffect", "func_184589_d"),
            "(Lnet/minecraft/potion/Potion;)V", false);

        // Remote return
        mv.visitLabel(remoteLabel);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);

        mv.visitEnd();
    }

    private static void generatePansmithForkFallback(ClassNode cls) {
        MethodVisitor mv = cls.visitMethod(ACC_PUBLIC, "updatePotionStatus",
            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Lnet/minecraft/item/ItemStack;Z)V",
            null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus",
            "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();
    }
}
