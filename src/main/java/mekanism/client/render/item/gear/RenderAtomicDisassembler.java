package mekanism.client.render.item.gear;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelAtomicDisassembler;
import mekanism.client.render.MekanismRenderHelper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAtomicDisassembler extends MekanismItemStackRenderer {

    private static ModelAtomicDisassembler atomicDisassembler = new ModelAtomicDisassembler();
    public static ItemLayerWrapper model;

    @Override
    protected void renderBlockSpecific(@Nonnull ItemStack stack, TransformType transformType) {
    }

    @Override
    protected void renderItemSpecific(@Nonnull ItemStack stack, TransformType transformType) {
        MekanismRenderHelper renderHelper = new MekanismRenderHelper(true).scale(1.4F).rotateZ(180, 1);

        if (transformType == TransformType.THIRD_PERSON_RIGHT_HAND || transformType == TransformType.THIRD_PERSON_LEFT_HAND) {
            if (transformType == TransformType.THIRD_PERSON_LEFT_HAND) {
                renderHelper.rotateY(-90, 1);
            }

            renderHelper.rotateY(45, 1).rotateX(50, 1).scale(2.0F).translateYZ(-0.4F, 0.4F);
        } else if (transformType == TransformType.GUI) {
            renderHelper.rotateY(225, 1).rotateXZ(45, -1, -1).scale(0.6F).translateY(-0.2F);
        } else {
            if (transformType == TransformType.FIRST_PERSON_LEFT_HAND) {
                renderHelper.rotateY(90, 1);
            }
            renderHelper.rotateY(45, 1).translateY(-0.7F);
        }

        MekanismRenderer.bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "AtomicDisassembler.png"));
        atomicDisassembler.render(0.0625F);
        renderHelper.cleanup();
    }

    @Nonnull
    @Override
    protected TransformType getTransform(@Nonnull ItemStack stack) {
        return model.getTransform();
    }
}