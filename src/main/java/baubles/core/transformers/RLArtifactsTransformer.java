package baubles.core.transformers;

import artifacts.common.init.ModItems;
import artifacts.common.item.BaubleAmulet;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Ah yes, yet another "loop is le bad" way of handling Baubles
 * I'm giving to middle finger to developers of every single mod that has hardcoded the slots and also Azanor.
 * Fuck you Azanor. I hope no one makes you create another public API ever again.
 **/
public class RLArtifactsTransformer extends BaseTransformer {

    public static boolean checkArtifacts(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        return cls.visibleAnnotations.get(0).values.get(3).equals("RLArtifacts");
    }

    public static byte[] transformLayerAmulet(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTexturesGetModel")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerAmulet$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformLayerBelt(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTexturesGetModel")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerBelt$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformLayerCloak(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderChest")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerCloak$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformLayerDrinkingHat(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderLayer")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerDrinkingHat$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformLayerGloves(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTextures")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerGloves$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHandSide;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformLayerNightVisionGoggles(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderLayer")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerNightVisionGoggles$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    public static byte[] transformLayerSnorkel(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderLayer")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/RLArtifactsTransformer", "LayerSnorkel$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static int LayerAmulet$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() instanceof BaubleAmulet) return i;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerBelt$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            Item item = baubles.getStackInSlot(i).getItem();
            if(item == ModItems.BOTTLED_CLOUD) return i;
            else if(item == ModItems.BOTTLED_FART) return i;
            else if(item == ModItems.ANTIDOTE_VESSEL) return i;
            else if(item == ModItems.BUBBLE_WRAP) return i;
            else if(item == ModItems.OBSIDIAN_SKULL) return i;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerCloak$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() == ModItems.STAR_CLOAK) return i;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerDrinkingHat$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() == ModItems.DRINKING_HAT) return i;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerGloves$getRenderStack(BaubleType type, EntityPlayer player, EnumHandSide hand) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        int a = -1;
        int oldA;
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            Item item = baubles.getStackInSlot(i).getItem();
            if (item == ModItems.POWER_GLOVE || item == ModItems.FERAL_CLAWS || item == ModItems.MECHANICAL_GLOVE || item == ModItems.FIRE_GAUNTLET || item == ModItems.POCKET_PISTON) {
                oldA = a;
                a = i;
                if (hand == EnumHandSide.LEFT && oldA == -1) return a;
                if (hand == EnumHandSide.RIGHT && oldA != -1) return a;
            }
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerNightVisionGoggles$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() == ModItems.NIGHT_VISION_GOGGLES) return i;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerSnorkel$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() == ModItems.SNORKEL) return i;
        }
        return -1;
    }
}
