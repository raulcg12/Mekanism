package mekanism.client.gui.filter;

import java.io.IOException;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.client.render.MekanismRenderHelper;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismSounds;
import mekanism.common.content.transporter.TItemStackFilter;
import mekanism.common.network.PacketEditFilter.EditFilterMessage;
import mekanism.common.network.PacketLogisticalSorterGui.LogisticalSorterGuiMessage;
import mekanism.common.network.PacketLogisticalSorterGui.SorterGuiPacket;
import mekanism.common.network.PacketNewFilter.NewFilterMessage;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.TransporterUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiTItemStackFilter extends GuiItemStackFilter<TItemStackFilter, TileEntityLogisticalSorter> {

    private GuiTextField minField;
    private GuiTextField maxField;

    public GuiTItemStackFilter(EntityPlayer player, TileEntityLogisticalSorter tile, int index) {
        super(player, tile);
        origFilter = (TItemStackFilter) tileEntity.filters.get(index);
        filter = ((TItemStackFilter) tileEntity.filters.get(index)).clone();
    }

    public GuiTItemStackFilter(EntityPlayer player, TileEntityLogisticalSorter tile) {
        super(player, tile);
        isNew = true;
        filter = new TItemStackFilter();
    }

    @Override
    protected void addButtons(int guiWidth, int guiHeight) {
        buttonList.add(new GuiButton(0, guiWidth + 47, guiHeight + 62, 60, 20, LangUtils.localize("gui.save")));
        buttonList.add(new GuiButton(1, guiWidth + 109, guiHeight + 62, 60, 20, LangUtils.localize("gui.delete")));
    }

    @Override
    public void initGui() {
        super.initGui();
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        minField = new GuiTextField(2, fontRenderer, guiWidth + 149, guiHeight + 19, 20, 11);
        minField.setMaxStringLength(2);
        minField.setText("" + filter.min);
        maxField = new GuiTextField(3, fontRenderer, guiWidth + 149, guiHeight + 31, 20, 11);
        maxField.setMaxStringLength(2);
        maxField.setText("" + filter.max);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (guibutton.id == 0) {
            if (!filter.itemType.isEmpty() && !minField.getText().isEmpty() && !maxField.getText().isEmpty()) {
                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());
                if (max >= min && max <= 64) {
                    filter.min = Integer.parseInt(minField.getText());
                    filter.max = Integer.parseInt(maxField.getText());
                    if (isNew) {
                        Mekanism.packetHandler.sendToServer(new NewFilterMessage(Coord4D.get(tileEntity), filter));
                    } else {
                        Mekanism.packetHandler.sendToServer(new EditFilterMessage(Coord4D.get(tileEntity), false, origFilter, filter));
                    }
                    Mekanism.packetHandler.sendToServer(new LogisticalSorterGuiMessage(SorterGuiPacket.SERVER, Coord4D.get(tileEntity), 0, 0, 0));
                } else if (min > max) {
                    status = EnumColor.DARK_RED + "Max<min";
                    ticker = 20;
                } else { //if(max > 64 || min > 64)
                    status = EnumColor.DARK_RED + "Max>64";
                    ticker = 20;
                }
            } else if (filter.itemType.isEmpty()) {
                status = EnumColor.DARK_RED + "No item";
                ticker = 20;
            } else if (minField.getText().isEmpty() || maxField.getText().isEmpty()) {
                status = EnumColor.DARK_RED + "Max/min";
                ticker = 20;
            }
        } else if (guibutton.id == 1) {
            Mekanism.packetHandler.sendToServer(new EditFilterMessage(Coord4D.get(tileEntity), true, origFilter, null));
            Mekanism.packetHandler.sendToServer(new LogisticalSorterGuiMessage(SorterGuiPacket.SERVER, Coord4D.get(tileEntity), 0, 0, 0));
        }
    }

    @Override
    public void keyTyped(char c, int i) throws IOException {
        if ((!minField.isFocused() && !maxField.isFocused()) || i == Keyboard.KEY_ESCAPE) {
            super.keyTyped(c, i);
        }
        if (Character.isDigit(c) || isTextboxKey(c, i)) {
            minField.textboxKeyTyped(c, i);
            maxField.textboxKeyTyped(c, i);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString((isNew ? LangUtils.localize("gui.new") : LangUtils.localize("gui.edit")) + " " +
                                LangUtils.localize("gui.itemFilter"), 43, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("gui.status") + ": " + status, 35, 20, 0x00CD00);
        fontRenderer.drawString(LangUtils.localize("gui.itemFilter.details") + ":", 35, 32, 0x00CD00);
        fontRenderer.drawString(LangUtils.localize("gui.itemFilter.min") + ":", 128, 20, 0x404040);
        fontRenderer.drawString(LangUtils.localize("gui.itemFilter.max") + ":", 128, 32, 0x404040);
        String sizeModeString = LangUtils.transOnOff(filter.sizeMode);
        if (tileEntity.singleItem && filter.sizeMode) {
            sizeModeString = EnumColor.RED + sizeModeString + "!";
        }

        fontRenderer.drawString(sizeModeString, 141, 46, 0x404040);
        fontRenderer.drawString(LangUtils.transOnOff(filter.allowDefault), 24, 66, 0x404040);
        if (!filter.itemType.isEmpty()) {
            renderScaledText(filter.itemType.getDisplayName(), 35, 41, 0x00CD00, 89);
            MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(filter.itemType, 12, 19);
            renderHelper.cleanup();
        }
        drawColorIcon(12, 44, filter.color, 1);
        int xAxis = mouseX - (width - xSize) / 2;
        int yAxis = mouseY - (height - ySize) / 2;
        if (xAxis >= 128 && xAxis <= 139 && yAxis >= 44 && yAxis <= 55) {
            String sizeModeTooltip = LangUtils.localize("gui.sizeMode");
            if (tileEntity.singleItem && filter.sizeMode) {
                sizeModeTooltip += " - " + LangUtils.localize("mekanism.gui.sizeModeConflict");
            }
            drawHoveringText(MekanismUtils.splitTooltip(sizeModeTooltip, ItemStack.EMPTY), xAxis, yAxis);
        }
        if (xAxis >= 11 && xAxis <= 22 && yAxis >= 64 && yAxis <= 75) {
            drawHoveringText(LangUtils.localize("gui.allowDefault"), xAxis, yAxis);
        }
        if (xAxis >= 12 && xAxis <= 28 && yAxis >= 44 && yAxis <= 60) {
            if (filter.color != null) {
                drawHoveringText(filter.color.getColoredName(), xAxis, yAxis);
            } else {
                drawHoveringText(LangUtils.localize("gui.none"), xAxis, yAxis);
            }
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        minField.updateCursorCounter();
        maxField.updateCursorCounter();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(getGuiLocation());
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight);
        int xAxis = mouseX - guiWidth;
        int yAxis = mouseY - guiHeight;
        drawTexturedModalRect(guiWidth + 5, guiHeight + 5, 176, xAxis >= 5 && xAxis <= 16 && yAxis >= 5 && yAxis <= 16, 11);
        drawTexturedModalRect(guiWidth + 128, guiHeight + 44, 187, xAxis >= 128 && xAxis <= 139 && yAxis >= 44 && yAxis <= 55, 11);
        drawTexturedModalRect(guiWidth + 11, guiHeight + 64, 198, xAxis >= 11 && xAxis <= 22 && yAxis >= 64 && yAxis <= 75, 11);
        minField.drawTextBox();
        maxField.drawTextBox();
        if (xAxis >= 12 && xAxis <= 28 && yAxis >= 19 && yAxis <= 35) {
            MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).disableLighting().disableDepth().colorMaskAlpha();
            int x = guiWidth + 12;
            int y = guiHeight + 19;
            drawRect(x, y, x + 16, y + 16, 0x80FFFFFF);
            renderHelper.cleanup();
        }
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        minField.mouseClicked(mouseX, mouseY, button);
        maxField.mouseClicked(mouseX, mouseY, button);
        int xAxis = mouseX - (width - xSize) / 2;
        int yAxis = mouseY - (height - ySize) / 2;
        if (button == 0) {
            if (xAxis >= 5 && xAxis <= 16 && yAxis >= 5 && yAxis <= 16) {
                SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                Mekanism.packetHandler.sendToServer(new LogisticalSorterGuiMessage(SorterGuiPacket.SERVER, Coord4D.get(tileEntity), isNew ? 4 : 0, 0, 0));
            }
            if (xAxis >= 12 && xAxis <= 28 && yAxis >= 19 && yAxis <= 35) {
                ItemStack stack = mc.player.inventory.getItemStack();
                if (!stack.isEmpty() && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    filter.itemType = stack.copy();
                    filter.itemType.setCount(1);
                } else if (stack.isEmpty() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    filter.itemType = ItemStack.EMPTY;
                }
                SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
            }
            if (xAxis >= 128 && xAxis <= 139 && yAxis >= 44 && yAxis <= 55) {
                SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                filter.sizeMode = !filter.sizeMode;
            }
            if (xAxis >= 11 && xAxis <= 22 && yAxis >= 64 && yAxis <= 75) {
                SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                filter.allowDefault = !filter.allowDefault;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && button == 0) {
            button = 2;
        }
        if (xAxis >= 12 && xAxis <= 28 && yAxis >= 44 && yAxis <= 60) {
            SoundHandler.playSound(MekanismSounds.DING);
            if (button == 0) {
                filter.color = TransporterUtils.increment(filter.color);
            } else if (button == 1) {
                filter.color = TransporterUtils.decrement(filter.color);
            } else if (button == 2) {
                filter.color = null;
            }
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiTItemStackFilter.png");
    }
}