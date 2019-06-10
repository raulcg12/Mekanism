package mekanism.client.gui.element.tab;

import mekanism.client.gui.IGuiWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTabElementType<TILE extends TileEntity, TAB extends Enum & TabType> extends GuiTabElement<TILE> {

    private final TAB tabType;

    public GuiTabElementType(IGuiWrapper gui, TILE tile, TAB type, ResourceLocation def) {
        super(type.getResource(), gui, def, tile, type.getYPos());
        tabType = type;
    }

    @Override
    public void buttonClicked() {
        tabType.openGui(tileEntity);
    }

    @Override
    public void displayForegroundTooltip(int xAxis, int yAxis) {
        displayTooltip(tabType.getDesc(), xAxis, yAxis);
    }
}