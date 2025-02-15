package baubles.client.gui;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.SlotDefinition;
import baubles.client.ClientProxy;
import baubles.common.Baubles;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.integration.ModCompatibility;
import baubles.common.network.PacketChangeOffset;
import baubles.common.network.PacketHandler;
import com.google.common.collect.Ordering;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.ModConfigs;
import lain.mods.cos.client.GuiCosArmorButton;
import lain.mods.cos.client.GuiCosArmorToggleButton;
import lain.mods.cos.client.PlayerRenderHandler;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import static baubles.common.integration.ModCompatibility.CA;
import static baubles.common.integration.ModCompatibility.ME$shouldMoveLeft;

public class GuiPlayerExpanded extends InventoryEffectRenderer {

    public static final ResourceLocation background =
            new ResourceLocation(Baubles.MODID, "textures/gui/baubles_inventory.png");

    private static final boolean ENABLE_RECIPE_BOOK = !ModCompatibility.RecipeBook$isDisabled();
    private static final Field REF_OLD_MOUSE_X, REF_OLD_MOUSE_Y; // in GuiInventory to retain mouse positions when you close baubles gui
    private static final Method REF_ACTION_PERFORMED; // in GuiInventory for recipe book

    static {
        boolean deobfEnv = FMLLaunchHandler.isDeobfuscatedEnvironment();

        try {
            REF_OLD_MOUSE_X = GuiInventory.class.getDeclaredField(deobfEnv ? "oldMouseX" : "field_147048_u");
            REF_OLD_MOUSE_Y = GuiInventory.class.getDeclaredField(deobfEnv ? "oldMouseY" : "field_147047_v");

            REF_OLD_MOUSE_X.setAccessible(true);
            REF_OLD_MOUSE_Y.setAccessible(true);

            if (ENABLE_RECIPE_BOOK) {
                REF_ACTION_PERFORMED = GuiInventory.class.getDeclaredMethod(deobfEnv ? "actionPerformed" : "func_146284_a", GuiButton.class);
                REF_ACTION_PERFORMED.setAccessible(true);
            }
            else REF_ACTION_PERFORMED = null;

        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final EntityPlayer player;
    private final IBaublesItemHandler baublesHandler = ((ContainerPlayerExpanded) this.inventorySlots).baubles;
    protected GuiButtonImage recipeBook;
    protected GuiButton cosButton, cosToggleButton;
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
        updateActivePotionEffects();
        resetGuiLeft();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        this.up = new GuiSlotButton(56, this, guiLeft - 26, guiTop - 9, 27, 14, false);
        this.down = new GuiSlotButton(57, this, guiLeft - 26, guiTop + 7 + getMaxY(), 27, 14, true);

        this.up.visible = this.getVRealBaubleSlots() > this.getActualMaxBaubleSlots();
        this.down.visible = this.up.visible;

        this.buttonList.add(this.up);
        this.buttonList.add(this.down);

        if (ENABLE_RECIPE_BOOK) {
            this.initRecipeBook();
            this.buttonList.add(this.recipeBook);
        }

        if (Loader.isModLoaded(CA)) {
            this.initCosButtons();
            this.buttonList.add(this.cosButton);
            this.buttonList.add(this.cosToggleButton);
        }

        resetGuiLeft();
    }

    private void initRecipeBook() {
        this.recipeBook = new GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
    }

    @Optional.Method(modid = CA)
    private void initCosButtons() {
        if (!ModConfigs.CosArmorGuiButton_Hidden) {
            this.cosButton = new GuiCosArmorButton(58, this.guiLeft + ModConfigs.CosArmorGuiButton_Left, this.guiTop + ModConfigs.CosArmorGuiButton_Top, 10, 10, "cos.gui.buttoncos") {
                @Override
                public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
                    boolean pressed = super.mousePressed(mc, mouseX, mouseY);
                    if (pressed) {
                        CosmeticArmorReworked.network.sendToServer(new PacketOpenCosArmorInventory());
                    }
                    return pressed;
                }
            };
        }
        if (!ModConfigs.CosArmorToggleButton_Hidden) {
            GuiCosArmorToggleButton toggleButton = new GuiCosArmorToggleButton(59, this.guiLeft + ModConfigs.CosArmorToggleButton_Left, this.guiTop + ModConfigs.CosArmorToggleButton_Top, 5, 5, "") {
                @Override
                public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
                    boolean pressed = super.mousePressed(mc, mouseX, mouseY);
                    if (pressed) {
                        PlayerRenderHandler.HideCosArmor = !PlayerRenderHandler.HideCosArmor;
                        this.state = PlayerRenderHandler.HideCosArmor ? 1 : 0;
                    }
                    return pressed;
                }
            };
            toggleButton.state = PlayerRenderHandler.HideCosArmor ? 1 : 0;
            this.cosToggleButton = toggleButton;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
        int xLoc = this.guiLeft - 24;
        if (mouseX > xLoc && mouseX < xLoc + 19) {
            int yLoc = this.guiTop + 5;
            if (mouseY >= yLoc && mouseY < yLoc + getMaxY()) {
                int slotIndex = (mouseY - yLoc) / 18;
                BaublesContainer container = ((BaublesContainer) baublesHandler);

                ItemStack stack = container.getStackInSlot(slotIndex);
                if (!stack.isEmpty()) return;

                SlotDefinition definition = container.getSlot(slotIndex);

                if (definition != null) {

                    FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0, 0, 200);
                    String str = I18n.format(definition.getTranslationKey(slotIndex));
                    GuiUtils.drawHoveringText(Collections.singletonList(str), mouseX - this.guiLeft, mouseY - this.guiTop, width, height, 300, renderer);
                    GlStateManager.popMatrix();
                }
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
        if (this.getRealBaubleSlots() <= this.getActualMaxBaubleSlots()) return;
        if (ModCompatibility.MT$shouldScroll(this.getSlotUnderMouse())) {
            int dWheel = Mouse.getEventDWheel();
            if (dWheel != 0) {
                int value = -(dWheel / 120);
                PacketHandler.INSTANCE.sendToServer(new PacketChangeOffset(value));
                ((BaublesContainer) baublesHandler).incrOffset(value);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);

        int k = this.guiLeft;
        int l = this.guiTop;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        int maxSlots = this.getRealBaubleSlots();

        if (maxSlots > 0) {
            if (maxSlots == 1) {
                this.drawTexturedModalRect(k - 28, l, 176, 34, 28, 28);
            }
            else {
                for (int i = 0; i < maxSlots; i++) {
                    int textureY = 39;
                    int height = 20;
                    int y = l + (i * 18);

                    if (i == 0) {
                        textureY = 34;
                        height += 4;
                    }
                    else y += 5;
                    if (i == maxSlots - 1) height += 4;

                    this.drawTexturedModalRect(k - 28, y, 176, textureY, 28, height);
                }
            }
        }

        GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float) (k + 51) - this.oldMouseX, (float) (l + 75 - 50) - this.oldMouseY, this.mc.player);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: // Stats button
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
                break;
            case 10: // Recipe Book Button
                this.openInventoryWithRecipeBook(new GuiInventory(this.player));
                break;
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

    // I wonder why it doesn't work
    @Override
    protected void drawActivePotionEffects() {
        boolean moveLeft = ME$shouldMoveLeft(this);
//        System.out.println(moveLeft);
        int b = this.guiLeft;
        if (moveLeft) this.guiLeft -= 27;

        //InventoryEffectRenderer

        int i = this.guiLeft - 124;
        int j = this.guiTop;
        int k = 166;
        Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();

        if (!collection.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int l = 33;

            if (collection.size() > 5)
            {
                l = 132 / (collection.size() - 1);
            }

            for (PotionEffect potioneffect : Ordering.natural().sortedCopy(collection))
            {
                Potion potion = potioneffect.getPotion();
                if(!potion.shouldRender(potioneffect)) continue;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
                this.drawTexturedModalRect(i, j, 0, 166, 140, 32);

                if (potion.hasStatusIcon())
                {
                    int i1 = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(i + 6, j + 7, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                potion.renderInventoryEffect(potioneffect, this, i, j, this.zLevel);
                if (!potion.shouldRenderInvText(potioneffect)) { j += l; continue; }
                String s1 = I18n.format(potion.getName());

                if (potioneffect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.2");
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.3");
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.4");
                }

                this.fontRenderer.drawStringWithShadow(s1, (float)(i + 10 + 18), (float)(j + 6), 16777215);
                String s = Potion.getPotionDurationString(potioneffect, 1.0F);
                this.fontRenderer.drawStringWithShadow(s, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
                j += l;
            }
        }

        guiLeft = b;
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
                assert REF_ACTION_PERFORMED != null;
                REF_ACTION_PERFORMED.invoke(inventory, recipeBook);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getMaxY() {
        return 18 * this.getRealBaubleSlots();
    }

    public int getBaubleSlots() {
//        int slotNum = 0;
//        for (int i = 0; i < baublesHandler.getSlots(); i++) {
//            if (baublesHandler.getSlot(i) != null) {
//                slotNum += 1;
//            }
//        }
//        return slotNum;
        return this.baublesHandler.getSlots();
    }

    public int getRealBaubleSlots() {
        int slotNum = 0;
        for (int i = 0; i < baublesHandler.getSlots(); i++) {
            if (baublesHandler.getSlot(i) != null) {
                slotNum += 1;
            }
        }
        return Math.min(slotNum, this.getActualMaxBaubleSlots());
    }
    public int getVRealBaubleSlots() {
        int slotNum = 0;
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            if (BaublesApi.getBaublesHandler(player).getRealSlot(i) != null) {
                slotNum += 1;
            }
        }
        return slotNum;
    }

    public int getMaxBaubleSlots() {
//        int slotNum = 0;
//        for (int i = 0; i < baublesHandler.getSlots(); i++) {
//            if (baublesHandler.getSlot(i) != null) {
//                slotNum += 1;
//            }
//        }
//        return Math.min(slotNum, this.getActualMaxBaubleSlots());
        return Math.min(baublesHandler.getSlots(), this.getActualMaxBaubleSlots());
    }

    public int getActualMaxBaubleSlots() {
        return 8;
    }
}
