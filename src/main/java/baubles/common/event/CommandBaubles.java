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
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
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
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(new TextComponentTranslation("\u00a73You can also use /baub or /bau instead of /baubles."));
            sender.sendMessage(new TextComponentTranslation("\u00a73Use this to view the baubles inventory of a player."));
            sender.sendMessage(new TextComponentTranslation("  /baubles view <player>"));
            sender.sendMessage(new TextComponentTranslation("\u00a73Use this to clear a players baubles inventory. Default is everything or you can give a slot number"));
            sender.sendMessage(new TextComponentTranslation("  /baubles clear <player> [<slot>]"));
        } else {
            EntityPlayerMP entityplayermp = getPlayer(server, sender, args[1]);
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(entityplayermp);

            if (args[0].equalsIgnoreCase("view")) {
                sender.sendMessage(new TextComponentTranslation("\u00a73Showing baubles for " + entityplayermp.getName()));
                for (int a = 0; a < baubles.getSlots(); a++) {
                    ItemStack st = baubles.getStackInSlot(a);
                    if (!st.isEmpty() && st.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
                        IBauble bauble = Objects.requireNonNull(st.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null));
                        IBaubleType bt = bauble.getType(st);
                        sender.sendMessage(new TextComponentTranslation("\u00a73 [Slot " + a + "] " + bt + " " + st.getDisplayName()));
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
                        sender.sendMessage(new TextComponentTranslation("\u00a7cInvalid arguments"));
                        sender.sendMessage(new TextComponentTranslation("\u00a7cUse /baubles help to get help"));
                    } else {
                        baubles.setStackInSlot(slot, ItemStack.EMPTY);
                        sender.sendMessage(new TextComponentTranslation("\u00a73Cleared baubles slot " + slot + " for " + entityplayermp.getName()));
                        entityplayermp.sendMessage(new TextComponentTranslation("\u00a74Your baubles slot " + slot + " has been cleared by admin " + sender.getName()));
                    }
                } else {
                    for (int a = 0; a < baubles.getSlots(); a++) {
                        baubles.setStackInSlot(a, ItemStack.EMPTY);
                    }
                    sender.sendMessage(new TextComponentTranslation("\u00a73Cleared all baubles slots for " + entityplayermp.getName()));
                    entityplayermp.sendMessage(new TextComponentTranslation("\u00a74All your baubles slots have been cleared by admin " + sender.getName()));
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                // /baubles add <player> slotName
                if (args.length >= 3) {
                    String playerName = args[1];
                    String slotName = args[2];

                    ResourceLocation location;
                    if (!slotName.contains(":")) location = new ResourceLocation(Baubles.MODID, slotName);
                    else location = new ResourceLocation(slotName);
                    SlotDefinition definition = SlotDefinitions.get(location);

                    BaublesApi.getBaublesHandler(entityplayermp).addSlot(definition);
                    PacketHandler.INSTANCE.sendTo(new PacketAddSlot(slotName), entityplayermp);
                    sender.sendMessage(new TextComponentString("添加执行"));
                } else {
                    sender.sendMessage(new TextComponentString("添加失败"));
                }
            } else {
                sender.sendMessage(new TextComponentTranslation("\u00a7cInvalid arguments"));
                sender.sendMessage(new TextComponentTranslation("\u00a7cUse /baubles help to get help"));
            }
        }
    }
}
