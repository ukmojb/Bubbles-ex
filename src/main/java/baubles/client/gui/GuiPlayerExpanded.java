package baubles.client.gui;

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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.IOException;

public class GuiPlayerExpanded extends InventoryEffectRenderer {

    public static final ResourceLocation background =
            new ResourceLocation(Baubles.MODID, "textures/gui/baubles_inventory.png");

    private final EntityPlayer player;

    protected GuiButtonImage recipeBook;

    private float oldMouseX;
    private float oldMouseY;

    public GuiPlayerExpanded(EntityPlayer player) {
        super(new ContainerPlayerExpanded(player.inventory, !player.getEntityWorld().isRemote, player));
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
        this.buttonList.add(this.recipeBook);
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
        this.drawTexturedModalRect(k, l, 0, 18, this.xSize, this.ySize);

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

    private void openInventoryWithRecipeBook(GuiInventory inventory) throws IOException {
        this.mc.displayGuiScreen(inventory);
        if (!inventory.func_194310_f().isVisible())
            inventory.actionPerformed(recipeBook);
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        if (par2 == ClientProxy.KEY_BAUBLES.getKeyCode()) {
            this.mc.player.closeScreen();
        } else
            super.keyTyped(par1, par2);
    }

    public void displayNormalInventory() {
        GuiInventory gui = new GuiInventory(this.mc.player);
        ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseX, "oldMouseX", "field_147048_u");
        ReflectionHelper.setPrivateValue(GuiInventory.class, gui, this.oldMouseY, "oldMouseY", "field_147047_v");
        this.mc.displayGuiScreen(gui);
    }
}
