package baubles.common.event;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.common.Baubles;
import baubles.common.Config;
import baubles.common.init.SlotDefinitions;
import baubles.common.network.PacketAddSlot;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketRemoveSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandBaubles extends CommandBase {
    private final List<String> aliases;

    public CommandBaubles() {
        this.aliases = new ArrayList<>();
        this.aliases.add("baub");
        this.aliases.add("bau");
    }

    @Nonnull
    @Override
    public String getName() {
        return "baubles";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender icommandsender) {
        return "/baubles <action> [<player> [<params>]]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] astring, int i) {
        return i == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "view", "clear", "add", "help", "remove");
        } else if (args.length == 2 && ("view".equals(args[0]) || "clear".equals(args[0]) || "add".equals(args[0]) || "remove".equals(args[0]))) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 3 && ("add".equals(args[0]) || "remove".equals(args[0]))) {
            return getListOfStringsMatchingLastWord(args, "amulet", "ring", "belt", "trinket", "head", "body", "charm");
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(new TextComponentTranslation("command.baubles.alias_help"));
            sender.sendMessage(new TextComponentTranslation("command.baubles.view_help"));
            sender.sendMessage(new TextComponentTranslation("command.baubles.clear_help"));
            sender.sendMessage(new TextComponentTranslation("command.baubles.add_help"));
            sender.sendMessage(new TextComponentTranslation("command.baubles.remove_help"));
        } else {
            EntityPlayerMP entityplayermp = getPlayer(server, sender, args[1]);
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(entityplayermp);

            if (args[0].equalsIgnoreCase("view")) {
                sender.sendMessage(new TextComponentTranslation("command.baubles.showing_baubles", entityplayermp.getName()));
                for (int a = 0; a < baubles.getSlots(); a++) {
                    ItemStack st = baubles.getStackInSlot(a);
                    if (!st.isEmpty() && st.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
                        IBauble bauble = Objects.requireNonNull(st.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null));
                        IBaubleType bt = bauble.getType(st);
                        sender.sendMessage(new TextComponentTranslation("command.baubles.slot_info", a, bt, st.getDisplayName()));
                    }
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (args.length >= 3) {
                    int slot = -1;
                    try {
                        slot = Integer.parseInt(args[2]);
                    } catch (Exception e) {
                    }
                    if (slot < 0 || slot >= baubles.getSlots()) {
                        sender.sendMessage(new TextComponentTranslation("command.baubles.invalid_args"));
                        sender.sendMessage(new TextComponentTranslation("command.baubles.use_help"));
                    } else {
                        baubles.setStackInSlot(slot, ItemStack.EMPTY);
                        sender.sendMessage(new TextComponentTranslation("command.baubles.cleared_slot", slot, entityplayermp.getName()));
                        entityplayermp.sendMessage(new TextComponentTranslation("command.baubles.your_slot_cleared", slot, sender.getName()));
                    }
                } else {
                    for (int a = 0; a < baubles.getSlots(); a++) {
                        baubles.setStackInSlot(a, ItemStack.EMPTY);
                    }
                    sender.sendMessage(new TextComponentTranslation("command.baubles.cleared_all_slots", entityplayermp.getName()));
                    entityplayermp.sendMessage(new TextComponentTranslation("command.baubles.all_slots_cleared", sender.getName()));
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (args.length >= 3) {
                    String playerName = args[1];
                    String slotName = args[2];

                    ResourceLocation location;
                    if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
                    else location = new ResourceLocation(slotName);
                    SlotDefinition definition = SlotDefinitions.get(location);

                    if (!checkPlayerBaublesIsFull(entityplayermp)) {
                        BaublesApi.getBaublesHandler(entityplayermp).addSlot(definition);
                        PacketHandler.INSTANCE.sendTo(new PacketAddSlot(slotName), entityplayermp);
                        sender.sendMessage(new TextComponentTranslation("command.baubles.add_success"));
                    } else {
                        sender.sendMessage(new TextComponentTranslation("command.baubles.add_full"));
                    }
                } else {
                    sender.sendMessage(new TextComponentTranslation("command.baubles.add_fail"));
                }
                // 删除
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length >= 3) {
                    String playerName = args[1];
                    String slotName = args[2];

                    ResourceLocation location;
                    if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
                    else location = new ResourceLocation(slotName);
                    SlotDefinition definition = SlotDefinitions.get(location);

                    BaublesApi.getBaublesHandler(entityplayermp).removeSlot(definition);
                    PacketHandler.INSTANCE.sendTo(new PacketRemoveSlot(slotName), entityplayermp);
                    sender.sendMessage(new TextComponentTranslation("command.baubles.remove_success"));
                } else {
                    sender.sendMessage(new TextComponentTranslation("command.baubles.remove_fail"));
                }
            } else {
                sender.sendMessage(new TextComponentTranslation("command.baubles.invalid_args"));
                sender.sendMessage(new TextComponentTranslation("command.baubles.use_help"));
            }
        }
    }

    private boolean checkPlayerBaublesIsFull(EntityPlayerMP entityPlayerMP) {
        IBaublesItemHandler iBaublesItemHandler = BaublesApi.getBaublesHandler(entityPlayerMP);
        boolean isFull = true;
        for (int i = 0; i < iBaublesItemHandler.getSlots(); i++) {
            SlotDefinition slotDefinition = iBaublesItemHandler.getRealSlot(i);
            if (slotDefinition == null) isFull = false;
        }
        return isFull;
    }


}