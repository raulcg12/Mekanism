package mekanism.generators.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGauge.Type;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.network.PacketSimpleGui.SimpleGuiMessage;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.GuiHeatTab;
import mekanism.generators.client.gui.element.GuiStatTab;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiReactorFuel extends GuiMekanismTile<TileEntityReactorController> {

    private GuiTextField injectionRateField;

    public GuiReactorFuel(InventoryPlayer inventory, final TileEntityReactorController tile) {
        super(tile, new ContainerNull(inventory.player, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiEnergyInfo(() -> tileEntity.isFormed() ? Arrays.asList(
              LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()),
              LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getReactor().getPassiveGeneration(false, true)) + "/t")
                                                                    : new ArrayList<>(), this, resource));
        addGuiElement(new GuiGasGauge(() -> tileEntity.deuteriumTank, Type.SMALL, this, resource, 25, 64));
        addGuiElement(new GuiGasGauge(() -> tileEntity.fuelTank, Type.STANDARD, this, resource, 79, 50));
        addGuiElement(new GuiGasGauge(() -> tileEntity.tritiumTank, Type.SMALL, this, resource, 133, 64));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.SMALL_RIGHT, this, resource, 45, 75));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.SMALL_LEFT, this, resource, 99, 75));
        addGuiElement(new GuiHeatTab(this, tileEntity, resource));
        addGuiElement(new GuiStatTab(this, tileEntity, resource));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(tileEntity.getName(), 46, 6, 0x404040);
        String str = LangUtils.localize("gui.reactor.injectionRate") + ": " + (tileEntity.getReactor() == null ? "None" : tileEntity.getReactor().getInjectionRate());
        fontRenderer.drawString(str, (xSize / 2) - (fontRenderer.getStringWidth(str) / 2), 35, 0x404040);
        fontRenderer.drawString("Edit Rate" + ":", 50, 117, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        drawTexturedModalRect(guiLeft + 6, guiTop + 6, 176, xAxis >= 6 && xAxis <= 20 && yAxis >= 6 && yAxis <= 20, 14);
        injectionRateField.drawTextBox();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        injectionRateField.updateCursorCounter();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        injectionRateField.mouseClicked(mouseX, mouseY, button);
        int xAxis = mouseX - (width - xSize) / 2;
        int yAxis = mouseY - (height - ySize) / 2;
        if (button == 0) {
            if (xAxis >= 6 && xAxis <= 20 && yAxis >= 6 && yAxis <= 20) {
                SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tileEntity), 1, 10));
            }
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiTall.png");
    }

    @Override
    public void keyTyped(char c, int i) throws IOException {
        if (!injectionRateField.isFocused() || i == Keyboard.KEY_ESCAPE) {
            super.keyTyped(c, i);
        }
        if (i == Keyboard.KEY_RETURN) {
            if (injectionRateField.isFocused()) {
                setInjection();
            }
        }
        if (Character.isDigit(c) || isTextboxKey(c, i)) {
            injectionRateField.textboxKeyTyped(c, i);
        }
    }

    private void setInjection() {
        if (!injectionRateField.getText().isEmpty()) {
            int toUse = Math.max(0, Integer.parseInt(injectionRateField.getText()));
            toUse -= toUse % 2;
            TileNetworkList data = TileNetworkList.withContents(0, toUse);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            injectionRateField.setText("");
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        String prevRad = injectionRateField != null ? injectionRateField.getText() : "";
        injectionRateField = new GuiTextField(0, fontRenderer, guiLeft + 98, guiTop + 115, 26, 11);
        injectionRateField.setMaxStringLength(2);
        injectionRateField.setText(prevRad);
    }
}