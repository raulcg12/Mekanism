package mekanism.client.gui;

import java.io.IOException;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiTransporterConfigTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerGasTank;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityGasTank;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGasTank extends GuiMekanismTile<TileEntityGasTank> {

    public GuiGasTank(InventoryPlayer inventory, TileEntityGasTank tile) {
        super(tile, new ContainerGasTank(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 7, 7).with(SlotOverlay.PLUS));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 7, 39).with(SlotOverlay.MINUS));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String stored = "" + (tileEntity.gasTank.getStored() == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : tileEntity.gasTank.getStored());
        String capacityInfo = stored + " / " + (tileEntity.tier.getStorage() == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : tileEntity.tier.getStorage());
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 6, 0x404040);
        fontRenderer.drawString(capacityInfo, 45, 40, 0x404040);
        renderScaledText(LangUtils.localize("gui.gas") + ": " + (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName()
                                                                                                     : LangUtils.localize("gui.none")), 45, 49, 0x404040, 112);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, ySize - 96 + 2, 0x404040);
        String name = chooseByMode(tileEntity.dumping, LangUtils.localize("gui.idle"), LangUtils.localize("gui.dumping"), LangUtils.localize("gui.dumping_excess"));
        fontRenderer.drawString(name, 156 - fontRenderer.getStringWidth(name), 73, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        int displayInt = chooseByMode(tileEntity.dumping, 10, 18, 26);
        drawTexturedModalRect(guiLeft + 160, guiTop + 73, 176, displayInt, 8, 8);
        if (tileEntity.gasTank.getGas() != null) {
            int scale = (int) (((double) tileEntity.gasTank.getStored() / tileEntity.tier.getStorage()) * 72);
            drawTexturedModalRect(guiLeft + 65, guiTop + 17, 176, 0, scale, 10);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        int xAxis = x - guiLeft;
        int yAxis = y - guiTop;
        if (xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82) {
            TileNetworkList data = TileNetworkList.withContents(0);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiGasTank.png");
    }

    private <T> T chooseByMode(TileEntityGasTank.GasMode dumping, T idleOption, T dumpingOption, T dumpingExcessOption) {
        if (dumping.equals(TileEntityGasTank.GasMode.IDLE)) {
            return idleOption;
        } else if (dumping.equals(TileEntityGasTank.GasMode.DUMPING)) {
            return dumpingOption;
        } else if (dumping.equals(TileEntityGasTank.GasMode.DUMPING_EXCESS)) {
            return dumpingExcessOption;
        }
        return idleOption; //should not happen;
    }
}