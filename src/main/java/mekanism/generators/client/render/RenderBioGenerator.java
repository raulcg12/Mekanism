package mekanism.generators.client.render;

import java.util.EnumMap;
import java.util.Map;
import mekanism.client.render.MekanismRenderHelper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DisplayInteger;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.model.ModelBioGenerator;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBioGenerator extends TileEntitySpecialRenderer<TileEntityBioGenerator> {

    private static final int stages = 40;
    private ModelBioGenerator model = new ModelBioGenerator();
    private Map<EnumFacing, DisplayInteger[]> energyDisplays = new EnumMap<>(EnumFacing.class);

    @Override
    public void render(TileEntityBioGenerator tileEntity, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        if (tileEntity.bioFuelSlot.fluidStored > 0) {
            MekanismRenderHelper renderHelper = initHelper().enableGlow();
            renderHelper.translate(x, y, z);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            getDisplayList(tileEntity.facing)[tileEntity.getScaledFuelLevel(stages - 1)].render();
            renderHelper.cleanup();
        }

        MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).translate(x + 0.5, y + 1.5, z + 0.5);
        bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "BioGenerator.png"));

        switch (tileEntity.facing.ordinal()) {
            case 2://NORTH
                renderHelper.rotateY(180, 1);
                break;
            case 3://SOUTH
                renderHelper.rotateY(0, 1);
                break;
            case 4://WEST
                renderHelper.rotateY(270, 1);
                break;
            case 5://EAST
                renderHelper.rotateY(90, 1);
                break;
        }

        renderHelper.rotateZ(180, 1);
        model.render(0.0625F);
        renderHelper.cleanup();
    }

    @SuppressWarnings("incomplete-switch")
    private DisplayInteger[] getDisplayList(EnumFacing side) {
        if (energyDisplays.containsKey(side)) {
            return energyDisplays.get(side);
        }

        DisplayInteger[] displays = new DisplayInteger[stages];

        Model3D model3D = new Model3D();
        model3D.baseBlock = Blocks.WATER;
        model3D.setTexture(MekanismRenderer.energyIcon);

        for (int i = 0; i < stages; i++) {
            displays[i] = DisplayInteger.createAndStart();

            switch (side) {
                case NORTH: {
                    model3D.minZ = 0.5;
                    model3D.maxZ = 0.875;

                    model3D.minX = 0.1875;
                    model3D.maxX = 0.8215;
                    break;
                }
                case SOUTH: {
                    model3D.minZ = 0.125;
                    model3D.maxZ = 0.5;

                    model3D.minX = 0.1875;
                    model3D.maxX = 0.8215;
                    break;
                }
                case WEST: {
                    model3D.minX = 0.5;
                    model3D.maxX = 0.875;

                    model3D.minZ = 0.1875;
                    model3D.maxZ = 0.8215;
                    break;
                }
                case EAST: {
                    model3D.minX = 0.125;
                    model3D.maxX = 0.5;

                    model3D.minZ = 0.1875;
                    model3D.maxZ = 0.8215;
                    break;
                }
            }

            model3D.minY = 0.4375 + 0.001;  //prevent z fighting at low fuel levels
            model3D.maxY = 0.4375 + ((float) i / stages) * 0.4375 + 0.001;

            MekanismRenderer.renderObject(model3D);
            DisplayInteger.endList();
        }

        energyDisplays.put(side, displays);
        return displays;
    }

    private MekanismRenderHelper initHelper() {
        MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).enableCull().enableBlend().disableLighting();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        return renderHelper;
    }
}