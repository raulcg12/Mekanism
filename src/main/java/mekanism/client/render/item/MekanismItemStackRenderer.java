package mekanism.client.render.item;

import javax.annotation.Nonnull;
import mekanism.client.render.GLSMHelper;
import mekanism.client.render.MekanismRenderHelper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.RenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class MekanismItemStackRenderer extends TileEntityItemStackRenderer {

    protected abstract void renderBlockSpecific(@Nonnull ItemStack stack, TransformType transformType);

    protected abstract void renderItemSpecific(@Nonnull ItemStack stack, TransformType transformType);

    @Nonnull
    protected abstract TransformType getTransform(@Nonnull ItemStack stack);

    protected boolean earlyExit() {
        return false;
    }

    protected void renderWithTransform(@Nonnull ItemStack stack) {
        TransformType transformType = getTransform(stack);
        if (transformType == TransformType.GUI) {
            GLSMHelper.INSTANCE.rotateY(180, 1);
        }

        renderBlockSpecific(stack, transformType);

        if (!earlyExit()) {
            if (transformType == TransformType.GUI) {
                GLSMHelper.INSTANCE.rotateY(90, 1);
            } else {
                GLSMHelper.INSTANCE.rotateY(180, 1);
            }
            renderItemSpecific(stack, transformType);
        }
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack) {
        Tessellator tessellator = Tessellator.getInstance();
        RenderState renderState = MekanismRenderer.pauseRenderer(tessellator);

        MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).translateAll(0.5F).rotateY(180, 1);

        //
        renderWithTransform(stack);
        //

        //TODO: Make this use helper for lighting and then disable it after bindTexture?
        renderHelper.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        renderHelper.enableCull();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderHelper.cleanup();

        MekanismRenderer.resumeRenderer(tessellator, renderState);
    }
}