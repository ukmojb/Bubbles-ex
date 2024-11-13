package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * transforms {@link Enchantment#getEntityEquipment(EntityLivingBase)} for making enchantments in baubles work.
 **/
public class EnchantmentTransformer extends BaseTransformer {

    public static byte[] transformEnchantment(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getEntityEquipment", ""))) {
                // ALOAD 2 - list
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ARETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, "baubles/core/transformers/EnchantmentTransformer", "Enchantment$getEntityEquipment_tooLazy", "(Ljava/util/List;Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)Ljava/util/List;", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static List<ItemStack> Enchantment$getEntityEquipment_tooLazy(List<ItemStack> list, Enchantment enchantment, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer) || enchantment.type == null) return list;
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble == null) continue;
            if (bauble.getType(stack).canApplyEnchantment(enchantment.type, stack)) list.add(stack);
        }
        return list;
    }
}
