package baubles.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Botania has some items that gets item from specific bauble slots.
 * Makes items work properly with Bubbles. Typical Vazkii mess.
 **/
public class BotaniaTransformer extends BaseTransformer {

    public static byte[] transformItemDivaCharm(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("lambda$onEntityDamaged$0")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemTiara(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("updatePlayerFlyStatus")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ICONST_4) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        node = node.getNext();
                        method.instructions.remove(node.getPrevious());
                        node = node.getNext();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "isEmpty", "()Z", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new LabelNode());
                        list.add(new InsnNode(RETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("shouldPlayerHaveFlight")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ICONST_4) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemGoddessCharm(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onExplosion")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 5));
                        list.add(new FieldInsnNode(GETSTATIC, "vazkii/botania/common/item/ModItems", "goddessCharm", "Lnet/minecraft/item/Item;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemHolyCloak(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onPlayerDamage")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemMonocle(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("hasMonocle")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/player/EntityPlayer;)Lbaubles/api/cap/IBaublesItemHandler;", false));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getSlots", "()I", true));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemTravelBelt(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {


            if (method.name.equals("updatePlayerStepStatus")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
//                        method.instructions.remove(node.getPrevious());
//                        InsnList list = new InsnList();
//                        list.add(new VarInsnNode(ALOAD, 2));
//                        list.add(new VarInsnNode(ALOAD, 0));
//                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));

//                        MethodInsnNode methodInsn = (MethodInsnNode) node;
//                        if (methodInsn.name.equals("getStackInSlot")) {
                            System.out.println("sdfsdfsdfsdfewwef");
                            // 移除 `getStackInSlot(3);`
                            method.instructions.remove(node.getPrevious()); // 移除 `ALOAD 3`
                            method.instructions.remove(node); // 移除 `INVOKEINTERFACE IInventory.getStackInSlot`

                            // 创建新指令
                            InsnList list = new InsnList();
                            LabelNode elseIfLabel = new LabelNode();
                            LabelNode elseLabel = new LabelNode();
                            LabelNode endLabel = new LabelNode();

                            // 直接获取 `ItemStack`，避免存入 `IBaublesItemHandler`
                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/player/EntityPlayer;)Lbaubles/api/cap/IBaublesItemHandler;", false));

                            // **确保 `belt` 变量是 `ItemStack`**
                            list.add(new VarInsnNode(ALOAD, 0)); // this
                            list.add(new TypeInsnNode(INSTANCEOF, "vazkii/botania/common/item/equipment/bauble/ItemSuperTravelBelt"));
                            list.add(new JumpInsnNode(IFEQ, elseIfLabel));

                            // **处理 `ItemSuperTravelBelt`**
                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new TypeInsnNode(NEW, "vazkii/botania/common/item/equipment/bauble/ItemSuperTravelBelt"));
                            list.add(new InsnNode(DUP));
                            list.add(new MethodInsnNode(INVOKESPECIAL, "vazkii/botania/common/item/equipment/bauble/ItemSuperTravelBelt", "<init>", "()V", false));
                            list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getStackInSlot", "(I)Lnet/minecraft/item/ItemStack;", true));
                            list.add(new VarInsnNode(ASTORE, 4)); // 存入 belt
                            list.add(new JumpInsnNode(GOTO, endLabel));

                            // **插入 `FrameNode`，确保 `belt` 是 `ItemStack`**
                            list.add(elseIfLabel);
                            list.add(new FrameNode(Opcodes.F_APPEND, 1, new Object[]{"net/minecraft/item/ItemStack"}, 0, null));

                            // **处理 `ItemSpeedUpBelt`**
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new TypeInsnNode(INSTANCEOF, "vazkii/botania/common/item/equipment/bauble/ItemSpeedUpBelt"));
                            list.add(new JumpInsnNode(IFEQ, elseLabel));

                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new TypeInsnNode(NEW, "vazkii/botania/common/item/equipment/bauble/ItemSpeedUpBelt"));
                            list.add(new InsnNode(DUP));
                            list.add(new MethodInsnNode(INVOKESPECIAL, "vazkii/botania/common/item/equipment/bauble/ItemSpeedUpBelt", "<init>", "()V", false));
                            list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getStackInSlot", "(I)Lnet/minecraft/item/ItemStack;", true));
                            list.add(new VarInsnNode(ASTORE, 4)); // 存入 belt
                            list.add(new JumpInsnNode(GOTO, endLabel));

                            // **插入 `FrameNode`，确保 `belt` 是 `ItemStack`**
                            list.add(elseLabel);
                            list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

                            // **处理默认情况**
                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new VarInsnNode(ALOAD, 0)); // this
                            list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                            list.add(new VarInsnNode(ALOAD, 1)); // player
                            list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getStackInSlot", "(I)Lnet/minecraft/item/ItemStack;", true));
                            list.add(new VarInsnNode(ASTORE, 4)); // 存入 belt

                            // **插入 `FrameNode`，确保 `belt` 是 `ItemStack`**
                            list.add(endLabel);
                            list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

                            // **插入修改后的指令**
                            method.instructions.insert(node, list);
                            break;
//                        }


                    }
                }


            }
            else
                if (method.name.equals("onPlayerJump")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("shouldPlayerHaveStepup")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemWaterRing(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onWornTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean remove = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INSTANCEOF) {
                        remove = true;
                    }
                    if (remove) {
                        method.instructions.remove(node.getPrevious());
                        if (node.getOpcode() == RETURN) {
                            method.instructions.remove(node.getNext().getNext().getNext());
                            method.instructions.remove(node.getNext().getNext());
                            method.instructions.remove(node.getNext());
                            method.instructions.remove(node);
                            break;
                        }
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
