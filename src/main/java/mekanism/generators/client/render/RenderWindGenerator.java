package mekanism.generators.client.render;

import mekanism.client.render.MekanismRenderHelper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.model.ModelWindGenerator;
import mekanism.generators.common.tile.TileEntityWindGenerator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RenderWindGenerator extends TileEntitySpecialRenderer<TileEntityWindGenerator> {

    private ModelWindGenerator model = new ModelWindGenerator();

    @Override
    public void render(TileEntityWindGenerator tileEntity, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).translate(x + 0.5, y + 1.5, z + 0.5);
        bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "WindGenerator.png"));

        MekanismRenderer.glRotateForFacing(tileEntity);

        GlStateManager.rotate(180, 0F, 0F, 1F);

        double angle = tileEntity.getAngle();

        if (tileEntity.getActive()) {
            angle = (tileEntity.getAngle() + ((tileEntity.getPos().getY() + 4F) / TileEntityWindGenerator.SPEED_SCALED) * partialTick) % 360;
        }

        model.render(0.0625F, angle);
        renderHelper.cleanup();
    }
}