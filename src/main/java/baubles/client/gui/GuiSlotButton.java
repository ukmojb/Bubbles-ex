package baubles.client.gui;

import baubles.api.cap.BaublesContainer;
import baubles.common.container.ContainerPlayerExpanded;
import baubles.common.network.PacketChangeOffset;
import baubles.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class GuiSlotButton extends GuiButton {

    protected final GuiContainer parent;

    private final boolean isDown;
    private final BaublesContainer baublesHandler;
    private int ticks;

    public GuiSlotButton(int id, GuiContainer parent, int x, int y, int widthIn, int heightIn, boolean isDown) {
        super(id, x, y, widthIn, heightIn, "");
        this.isDown = isDown;
        this.parent = parent;
        this.baublesHandler = (BaublesContainer) ((ContainerPlayerExpanded) parent.inventorySlots).baubles;
    }

    @Override
    public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(mc, mouseX, mouseY);
        if (pressed && ticks <= 0) {
            this.ticks = 10;
            int amount = isDown ? 1 : -1;
            PacketHandler.INSTANCE.sendToServer(new PacketChangeOffset(amount));
            baublesHandler.incrOffset(amount);
        }
        return pressed;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GuiPlayerExpanded.background);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 200);

            if (ticks > 0) {
                this.drawTexturedModalRect(this.x, this.y, 176 + (isDown ? 1 : 0) * 24, 12, 24, 12);
                ticks--;
            } else this.drawTexturedModalRect(this.x, this.y, 176 + (isDown ? 1 : 0) * 24, 0, 24, 12);

            GlStateManager.popMatrix();

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
