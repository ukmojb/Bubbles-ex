package baubles.client.gui;

import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.client.ClientProxy;
import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.network.PacketChangeOffset;
import baubles.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

public class GuiPlayerExpanded extends InventoryEffectRenderer {

    public static final ResourceLocation background =
            new ResourceLocation(Baubles.MODID, "textures/gui/baubles_inventory.png");

    private static final Field REF_OLD_MOUSE_X, REF_OLD_MOUSE_Y; // in GuiInventory to retain mouse positions when you close baubles gui
    private static final Method REF_ACTION_PERFORMED; // in GuiInventory for recipe book

    static {
        boolean deobfEnv = FMLLaunchHandler.isDeobfuscatedEnvironment();

        try {
            REF_OLD_MOUSE_X = GuiInventory.class.getDeclaredField(deobfEnv ? "oldMouseX" : "field_147048_u");
            REF_OLD_MOUSE_Y = GuiInventory.class.getDeclaredField(deobfEnv ? "oldMouseY" : "field_147047_v");
            REF_ACTION_PERFORMED = GuiInventory.class.getDeclaredMethod(deobfEnv ? "actionPerformed" : "func_146284_a", GuiButton.class);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        REF_OLD_MOUSE_X.setAccessible(true);
        REF_OLD_MOUSE_Y.setAccessible(true);
        REF_ACTION_PERFORMED.setAccessible(true);
    }

    private final EntityPlayer player;
    private final IBaublesItemHandler baublesHandler = ((ContainerPlayerExpanded) this.inventorySlots).baubles;
    protected GuiButtonImage recipeBook;
    protected GuiSlotButton up, down;
    private float oldMouseX, oldMouseY;

    public GuiPlayerExpanded(EntityPlayer player) {
        super(new ContainerPlayerExpanded(player.inventory, player));
        this.allowUserInput = true;
        this.player = player;
    }

    private void resetGuiLeft() {
        this.guiLeft = (this.width - this.xSize) / 2;
    }

    @Override
    public void updateScreen() {
        ((ContainerPlayerExpanded) inventorySlots).baubles.setEventBlock(false);
        updateActivePotionEffects();
        resetGuiLeft();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        this.recipeBook = new GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
        this.up = new GuiSlotButton(56, this, guiLeft - 27, guiTop - 10, 27, 14, false);
        this.down = new GuiSlotButton(57, this, guiLeft - 27, guiTop + 4 + (18 * (Math.min(7, baublesHandler.getSlots()))), 27, 14, true);
        this.buttonList.add(this.recipeBook);
        this.buttonList.add(this.up);
        this.buttonList.add(this.down);
        resetGuiLeft();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
        int xLoc = this.guiLeft - 22;
        if (mouseX > xLoc && mouseX < xLoc + 18) {
            int yLoc = this.guiTop + 4;
            if (mouseY >= yLoc && mouseY < yLoc + (17 * baublesHandler.getSlots()) - 10) {
                int slotIndex = (mouseY - yLoc) / 18;
                BaublesContainer container = ((BaublesContainer) baublesHandler);

                ItemStack stack = container.getStack(slotIndex);
                if (!stack.isEmpty()) return;

                SlotDefinition definition = container.getSlot(slotIndex);

                FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 200);
                String str = I18n.format(definition.getType().getTranslationKey());
                GuiUtils.drawHoveringText(Collections.singletonList(str), mouseX - this.guiLeft, mouseY - this.guiTop + 7, width, height, 300, renderer);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int xLoc = this.guiLeft - 22;
        if (mouseX > xLoc && mouseX < xLoc + 18) {
            int yLoc = this.guiTop + 4;
            if (mouseY >= yLoc && mouseY < yLoc + (17 * baublesHandler.getSlots()) - 10) {
                int dWheel = Mouse.getDWheel();
                if (dWheel != 0) {
                    // TODO add a config option to reverse the thing
                    int value = -(dWheel / 120);
                    PacketHandler.INSTANCE.sendToServer(new PacketChangeOffset(value));
                    ((BaublesContainer) baublesHandler).incrOffset(value);
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);

        int k = this.guiLeft;
        int l = this.guiTop;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        int size = Math.min(7, baublesHandler.getSlots());

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.drawTexturedModalRect(k - 27, l + 4 + (i * 18), 228, 28, 27, 18);
            }
        }

        GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float) (k + 51) - this.oldMouseX, (float) (l + 75 - 50) - this.oldMouseY, this.mc.player);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1: // Stats button
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
                break;
            case 10: // Recipe Book Button
                openInventoryWithRecipeBook(new GuiInventory(this.player));
                break;
        }
        /*if (button.id == 0) {
            //this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.player.getStatFileWriter()));
        }*/
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        if (par2 == ClientProxy.KEY_BAUBLES.getKeyCode()) {
            this.mc.player.closeScreen();
        } else
            super.keyTyped(par1, par2);
    }

    @Override
    protected void updateActivePotionEffects() {
        boolean hasVisibleEffect = false;
        for (PotionEffect potioneffect : this.mc.player.getActivePotionEffects()) {
            Potion potion = potioneffect.getPotion();
            if (potion.shouldRender(potioneffect)) {
                hasVisibleEffect = true;
                break;
            }
        }
        if (this.mc.player.getActivePotionEffects().isEmpty() || !hasVisibleEffect) {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        } else {
            this.hasActivePotionEffects = true;
        }
    }

    @Override
    protected void drawActivePotionEffects() {
        guiLeft -= 27;
        super.drawActivePotionEffects();
        guiLeft += 27;
    }

    public void displayNormalInventory() {
        GuiInventory gui = new GuiInventory(this.mc.player);

        try {
            REF_OLD_MOUSE_Y.set(gui, this.oldMouseX);
            REF_OLD_MOUSE_Y.set(gui, this.oldMouseY);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        this.mc.displayGuiScreen(gui);
    }

    private void openInventoryWithRecipeBook(GuiInventory inventory) {
        this.mc.displayGuiScreen(inventory);
        if (!inventory.func_194310_f().isVisible()) {
            try {
                REF_ACTION_PERFORMED.invoke(inventory, recipeBook);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
