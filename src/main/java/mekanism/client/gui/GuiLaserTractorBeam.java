package mekanism.client.gui;

import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.ContainerLaserTractorBeam;
import mekanism.common.tile.TileEntityLaserTractorBeam;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLaserTractorBeam extends GuiMekanismTile<TileEntityLaserTractorBeam> {

    public GuiLaserTractorBeam(InventoryPlayer inventory, TileEntityLaserTractorBeam tile) {
        super(tile, new ContainerLaserTractorBeam(inventory, tile));
        addGuiElement(new GuiSecurityTab(this, tileEntity, getGuiLocation()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(getGuiLocation());
        MekanismRenderer.resetColor();
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiFullInv.png");
    }
}