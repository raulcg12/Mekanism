package mekanism.client.render.tileentity;

import java.util.HashMap;
import java.util.Map;
import mekanism.client.render.FluidRenderer;
import mekanism.client.render.FluidRenderer.RenderData;
import mekanism.client.render.FluidRenderer.ValveRenderData;
import mekanism.client.render.MekanismRenderHelper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.DisplayInteger;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.content.tank.TankUpdateProtocol;
import mekanism.common.tile.TileEntityBoilerCasing;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderThermoelectricBoiler extends TileEntitySpecialRenderer<TileEntityBoilerCasing> {

    private static Map<RenderData, DisplayInteger[]> cachedLowerFluids = new HashMap<>();
    private static Map<RenderData, DisplayInteger> cachedUpperFluids = new HashMap<>();
    private static Map<ValveRenderData, DisplayInteger> cachedValveFluids = new HashMap<>();

    private FluidStack STEAM = new FluidStack(FluidRegistry.getFluid("steam"), 1);
    private FluidStack WATER = new FluidStack(FluidRegistry.WATER, 1);

    @Override
    public void render(TileEntityBoilerCasing tileEntity, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        if (tileEntity.clientHasStructure && tileEntity.isRendering && tileEntity.structure != null
            && tileEntity.structure.renderLocation != null && tileEntity.structure.upperRenderLocation != null) {
            if (tileEntity.structure.waterStored != null && tileEntity.structure.waterStored.amount != 0) {
                RenderData data = new RenderData();

                data.location = tileEntity.structure.renderLocation;
                data.height = tileEntity.structure.upperRenderLocation.y - 1 - tileEntity.structure.renderLocation.y;
                data.length = tileEntity.structure.volLength;
                data.width = tileEntity.structure.volWidth;
                data.fluidType = WATER;

                bindTexture(MekanismRenderer.getBlocksTexture());

                if (data.height >= 1 && tileEntity.structure.waterStored.getFluid() != null) {
                    MekanismRenderHelper renderHelper = FluidRenderer.initHelper();

                    FluidRenderer.translateToOrigin(data.location);

                    MekanismRenderer.glowOn(tileEntity.structure.waterStored.getFluid().getLuminosity());
                    renderHelper.color(tileEntity.structure.waterStored);

                    if (tileEntity.structure.waterStored.getFluid().isGaseous()) {
                        renderHelper.colorAlpha(Math.min(1, ((float) tileEntity.structure.waterStored.amount / (float) tileEntity.clientWaterCapacity) + MekanismRenderer.GAS_RENDER_BASE));
                        FluidRenderer.getTankDisplay(data).render();
                    } else {
                        FluidRenderer.getTankDisplay(data, tileEntity.prevWaterScale).render();
                    }

                    MekanismRenderer.glowOff();
                    renderHelper.cleanup();

                    for (ValveData valveData : tileEntity.valveViewing) {
                        MekanismRenderHelper valveRenderHelper = FluidRenderer.initHelper();
                        FluidRenderer.translateToOrigin(valveData.location);
                        MekanismRenderer.glowOn(tileEntity.structure.waterStored.getFluid().getLuminosity());
                        FluidRenderer.getValveDisplay(ValveRenderData.get(data, valveData)).render();
                        MekanismRenderer.glowOff();
                        valveRenderHelper.cleanup();
                    }
                }
            }

            if (tileEntity.structure.steamStored != null && tileEntity.structure.steamStored.amount > 0) {
                RenderData data = new RenderData();

                data.location = tileEntity.structure.upperRenderLocation;
                data.height = tileEntity.structure.renderLocation.y + tileEntity.structure.volHeight - 2 - tileEntity.structure.upperRenderLocation.y;
                data.length = tileEntity.structure.volLength;
                data.width = tileEntity.structure.volWidth;
                data.fluidType = STEAM;

                bindTexture(MekanismRenderer.getBlocksTexture());

                if (data.height >= 1 && tileEntity.structure.steamStored.getFluid() != null) {
                    MekanismRenderHelper renderHelper = FluidRenderer.initHelper();
                    FluidRenderer.translateToOrigin(data.location);
                    MekanismRenderer.glowOn(tileEntity.structure.steamStored.getFluid().getLuminosity());
                    renderHelper.color(tileEntity.structure.steamStored);

                    DisplayInteger display = FluidRenderer.getTankDisplay(data);
                    renderHelper.colorAlpha(Math.min(1, ((float) tileEntity.structure.steamStored.amount / (float) tileEntity.clientSteamCapacity) + MekanismRenderer.GAS_RENDER_BASE));
                    display.render();
                    MekanismRenderer.glowOff();
                    renderHelper.cleanup();
                }
            }
        }
    }

    private int getStages(int height) {
        return height * (TankUpdateProtocol.FLUID_PER_TANK / 10);
    }
}