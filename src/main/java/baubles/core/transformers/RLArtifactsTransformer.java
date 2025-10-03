package baubles.core.transformers;

import artifacts.common.init.ModItems;
import artifacts.common.item.AttributeModifierBauble;
import artifacts.common.item.BaubleAmulet;
import artifacts.common.util.BaubleHelper;
import artifacts.common.util.RenderHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.objectweb.asm.tree.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;


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

    /**
     * MODDERS LOOK AT API PACKAGE {@link baubles.api.BaublesApi#isBaubleEquipped(EntityPlayer, Item)} CHALLENGE
     * FUCKING IMPOSSIBLE
     **/
    public static byte[] transformBaubleAmulet(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onLivingDeath")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots") &&
                            ((MethodInsnNode) node).desc.equals("()[I")) {

                        MethodInsnNode original = (MethodInsnNode) node;

                        method.instructions.insertBefore(original, new VarInsnNode(ALOAD, 1));

                        method.instructions.set(original, new MethodInsnNode(INVOKEVIRTUAL,
                                original.owner,
                                "getValidSlotsArrays",
                                "(Lnet/minecraft/entity/player/EntityPlayer;)[I",
                                false));
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    /**
     * MODDERS LOOK AT API PACKAGE {@link baubles.api.BaublesApi#isBaubleEquipped(EntityPlayer, Item)} CHALLENGE
     * FUCKING IMPOSSIBLE
     **/
    public static byte[] transformBaubleBottledCloud(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onClientTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).name.equals("BELT")) {
                        for (int i = 0; i < 4; i++) {
                            iterator.remove();
                            node = iterator.next();
                        }
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new FieldInsnNode(GETSTATIC, "artifacts/common/init/ModItems", "BOTTLED_CLOUD", "Lartifacts/common/item/BaubleBase;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    public static byte[] transformAttributeModifierBauble(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("applyModifiers")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots") &&
                            ((MethodInsnNode) node).desc.equals("()[I")) {

                        MethodInsnNode original = (MethodInsnNode) node;

                        method.instructions.insertBefore(original, new VarInsnNode(ALOAD, 2));

                        method.instructions.set(original, new MethodInsnNode(INVOKEVIRTUAL,
                                original.owner,
                                "getValidSlotsArrays",
                                "(Lnet/minecraft/entity/player/EntityPlayer;)[I",
                                false));
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    public static byte[] transformBaubleHelper(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getAmountBaubleEquipped")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getSlots") &&
                            ((MethodInsnNode) node).desc.equals("()[I")) {

                        MethodInsnNode original = (MethodInsnNode) node;

//                        method.instructions.insertBefore(original, new VarInsnNode(ALOAD, 2));

                        method.instructions.set(original, new MethodInsnNode(INVOKEVIRTUAL,
                                original.owner,
                                "getRealBaubleSlots",
                                "()I",
                                false));
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static int LayerAmulet$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.AMULET.getValidSlots(player).size(); i++) {
            int num = BaubleType.AMULET.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());
        }
        return slot;
    }

    @SuppressWarnings("unused")
    public static int LayerBelt$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.BELT.getValidSlots(player).size(); i++) {
            int num = BaubleType.BELT.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            Item item = stack.getItem();
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());

            if(item == ModItems.BOTTLED_CLOUD) return slot;
            else if(item == ModItems.BOTTLED_FART) return slot;
            else if(item == ModItems.ANTIDOTE_VESSEL) return slot;
            else if(item == ModItems.BUBBLE_WRAP) return slot;
            else if(item == ModItems.OBSIDIAN_SKULL) return slot;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerCloak$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.BODY.getValidSlots(player).size(); i++) {
            int num = BaubleType.BODY.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());
            if (stack.getItem() == ModItems.STAR_CLOAK) return slot;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerDrinkingHat$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.HEAD.getValidSlots(player).size(); i++) {
            int num = BaubleType.HEAD.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());
            if (stack.getItem() == ModItems.DRINKING_HAT) return slot;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerGloves$getRenderStack(BaubleType type, EntityPlayer player, EnumHandSide hand) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.RING.getValidSlots(player).size(); i++) {
            int num = BaubleType.RING.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());
            Item item = handler.getStackInSlotNA(i).getItem();
            if (item == ModItems.POWER_GLOVE || item == ModItems.FERAL_CLAWS || item == ModItems.MECHANICAL_GLOVE || item == ModItems.FIRE_GAUNTLET || item == ModItems.POCKET_PISTON) {
                return slot;
            }
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerNightVisionGoggles$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.HEAD.getValidSlots(player).size(); i++) {
            int num = BaubleType.HEAD.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());
            if (stack.getItem() == ModItems.NIGHT_VISION_GOGGLES) return slot;
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static int LayerSnorkel$getRenderStack(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        int slot = -1;
        for (int i = 0; i < BaubleType.HEAD.getValidSlots(player).size(); i++) {
            int num = BaubleType.HEAD.getValidSlots(player).get((int) i);
            ItemStack stack = handler.getStackInSlotNA(num);
            if (!stack.isEmpty()) slot = BaublesApi.isBaubleEquipped(player, stack.getItem());
            if (stack.getItem() == ModItems.SNORKEL) return slot;
        }
        return -1;
    }
}