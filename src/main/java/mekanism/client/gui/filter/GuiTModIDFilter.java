package mekanism.client.gui.filter;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.OreDictCache;
import mekanism.common.content.transporter.TModIDFilter;
import mekanism.common.network.PacketLogisticalSorterGui.LogisticalSorterGuiMessage;
import mekanism.common.network.PacketLogisticalSorterGui.SorterGuiPacket;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTModIDFilter extends GuiModIDFilter<TModIDFilter, TileEntityLogisticalSorter> {

    public GuiTModIDFilter(EntityPlayer player, TileEntityLogisticalSorter tile, int index) {
        super(player, tile);
        origFilter = (TModIDFilter) tileEntity.filters.get(index);
        filter = ((TModIDFilter) tileEntity.filters.get(index)).clone();
        updateStackList(filter.getModID());
    }

    public GuiTModIDFilter(EntityPlayer player, TileEntityLogisticalSorter tile) {
        super(player, tile);
        isNew = true;
        filter = new TModIDFilter();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString((isNew ? LangUtils.localize("gui.new") : LangUtils.localize("gui.edit")) + " " +
                                LangUtils.localize("gui.modIDFilter"), 43, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("gui.status") + ": " + status, 35, 20, 0x00CD00);
        renderScaledText(LangUtils.localize("gui.id") + ": " + filter.getModID(), 35, 32, 0x00CD00, 107);
        fontRenderer.drawString(LangUtils.transOnOff(filter.allowDefault), 24, 66, 0x404040);
        renderItem(renderStack, 12, 19);
        drawColorIcon(12, 44, filter.color, 1);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
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
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiTModIDFilter.png");
    }

    @Override
    protected void updateStackList(String modName) {
        iterStacks = OreDictCache.getModIDStacks(modName, false);
        stackSwitch = 0;
        stackIndex = -1;
    }

    @Override
    protected void addButtons(int guiWidth, int guiHeight) {
        buttonList.add(new GuiButton(0, guiWidth + 47, guiHeight + 62, 60, 20, LangUtils.localize("gui.save")));
        buttonList.add(new GuiButton(1, guiWidth + 109, guiHeight + 62, 60, 20, LangUtils.localize("gui.delete")));
    }

    @Override
    protected void sendPacketToServer(int guiID) {
        Mekanism.packetHandler.sendToServer(new LogisticalSorterGuiMessage(SorterGuiPacket.SERVER, Coord4D.get(tileEntity), guiID, 0, 0));
    }
}