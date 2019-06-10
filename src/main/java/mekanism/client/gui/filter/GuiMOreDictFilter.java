package mekanism.client.gui.filter;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.OreDictCache;
import mekanism.common.content.miner.MOreDictFilter;
import mekanism.common.network.PacketDigitalMinerGui.DigitalMinerGuiMessage;
import mekanism.common.network.PacketDigitalMinerGui.MinerGuiPacket;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMOreDictFilter extends GuiOreDictFilter<MOreDictFilter, TileEntityDigitalMiner> {

    public GuiMOreDictFilter(EntityPlayer player, TileEntityDigitalMiner tile, int index) {
        super(player, tile);
        origFilter = (MOreDictFilter) tileEntity.filters.get(index);
        filter = ((MOreDictFilter) tileEntity.filters.get(index)).clone();
        updateStackList(filter.getOreDictName());
    }

    public GuiMOreDictFilter(EntityPlayer player, TileEntityDigitalMiner tile) {
        super(player, tile);
        isNew = true;
        filter = new MOreDictFilter();
    }

    @Override
    protected void addButtons() {
        buttonList.add(new GuiButton(0, guiLeft + 27, guiTop + 62, 60, 20, LangUtils.localize("gui.save")));
        buttonList.add(new GuiButton(1, guiLeft + 89, guiTop + 62, 60, 20, LangUtils.localize("gui.delete")));
    }

    @Override
    protected void sendPacketToServer(int guiID) {
        Mekanism.packetHandler.sendToServer(new DigitalMinerGuiMessage(MinerGuiPacket.SERVER, Coord4D.get(tileEntity), guiID, 0, 0));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiMOreDictFilter.png");
    }

    @Override
    protected void updateStackList(String oreName) {
        iterStacks = OreDictCache.getOreDictStacks(oreName, true);
        stackSwitch = 0;
        stackIndex = -1;
    }
}