package net.nayrus.noteblockmaster.render.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.render.NBMRenderType;
import net.nayrus.noteblockmaster.render.RenderUtils;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;

import java.awt.*;

public class SpinningCoreRender extends BaseItemRender{

    @Override
    public void renderItemInGUI(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderRotatingRings(matrix, buffer, getBaseColor(stack), 0.5F, 0.7F);
    }

    @Override
    public void renderItemOnGround(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        RenderSystem.enableDepthTest();
        renderRotatingRings(matrix, buffer, getBaseColor(stack), 0.33F, 0.7F);
        RenderSystem.disableDepthTest();
    }

    @Override
    public void renderItemFirstPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.2);
        renderRotatingRings(matrix, buffer, getBaseColor(stack), 0.33F, 0.7F);
    }

    @Override
    public void renderItemThirdPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.25);
        renderRotatingRings(matrix, buffer, getBaseColor(stack), 0.33F, 0.7F);
    }

    @Override
    public void renderItemInFrame(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderItemInGUI(stack, matrix, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderItemOnHead(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {

    }

    public void renderRotatingRings(PoseStack matrix, MultiBufferSource buffer, Color baseColor, float scale, float alpha){
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/1500F),0.5F,0F,-0.5F);
        matrix.rotateAround(Axis.XP.rotation(Util.getMillis()/700F),0F,0.5F,-0.5F);
        RenderUtils.buildHalfTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.QUADS), baseColor, scale, 0.7F, 0.08F, 0F, 1);
        RenderUtils.buildHalfTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.QUADS), baseColor, scale, 0.7F, 0.08F, Utils.PI, 1);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.XP.rotation(Util.getMillis()/8000F),0F,0.5F,-0.5F);
        //matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/8000F),0.5F,0F,-0.5F);
        matrix.rotateAround(Axis.ZP.rotation(Util.getMillis()/300F),0.5F,0.5F,0F);
        RenderUtils.buildHalfTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.QUADS), baseColor, scale, 0.5F, 0.08F, 0F, 1);
        RenderUtils.buildHalfTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.QUADS), baseColor, scale, 0.5F, 0.08F, Utils.PI, 1);
        matrix.popPose();
    }

    public Color getBaseColor(ItemStack stack){
        return stack.is(Registry.SUSTAIN) ? RenderUtils.shiftColor(Color.BLUE, Color.GRAY, 0.1F) : RenderUtils.shiftColor(Color.WHITE,Color.BLACK,0.1F);
    }
}
