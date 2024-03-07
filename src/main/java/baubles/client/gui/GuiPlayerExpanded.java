package baubles.client.gui;

import baubles.api.cap.IBaublesItemHandler;
import baubles.client.ClientProxy;
import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
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

    @Override
    protected void drawActivePotionEffects() {
        guiLeft -= 27;
        super.drawActivePotionEffects();
        guiLeft += 27;
    }
}
