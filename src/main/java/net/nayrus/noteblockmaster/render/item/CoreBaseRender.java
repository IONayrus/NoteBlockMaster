package net.nayrus.noteblockmaster.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.render.NBMRenderType;
import net.nayrus.noteblockmaster.render.RenderUtils;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.awt.*;

public class CoreBaseRender extends BaseItemRender {

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.renderByItem(stack, displayContext, matrix, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderItemInGUI(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.5F, 0.7F, packedLight);
    }

    @Override
    public void renderItemOnGround(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight);
    }

    @Override
    public void renderItemFirstPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.2);
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight);
    }

    @Override
    public void renderItemThirdPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.25);
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight);
    }

    @Override
    public void renderItemInFrame(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight);
    }

    @Override
    public void renderItemOnHead(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.5F, 0.7F, packedLight);
    }

    public static void renderBaseRings(PoseStack matrix, MultiBufferSource buffer, float scale, float alpha, int packedLight){
        matrix.pushPose();
        RenderUtils.buildTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), new Color(52,153,136), scale, 0.5F, 0.03F, alpha);
        RenderUtils.buildTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), Color.LIGHT_GRAY, scale, 0.6F, 0.07F, alpha);
        RenderUtils.buildTorus(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), Color.ORANGE, scale, 0.7F, 0.03F, alpha);
        matrix.popPose();
    }

    public static class Extension implements IClientItemExtensions {

        private final BaseItemRender baseItemRender = new CoreBaseRender();

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return baseItemRender;
        }
    }
}
