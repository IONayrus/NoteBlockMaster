package net.nayrus.noteblockmaster.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.render.utils.CircularColorGradient;
import net.nayrus.noteblockmaster.render.utils.GeometryBuilder;
import net.nayrus.noteblockmaster.render.NBMRenderType;
import net.nayrus.noteblockmaster.render.utils.RenderUtils;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.awt.*;

public class CoreBaseRender extends BaseItemRender {

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.renderByItem(stack, displayContext, matrix, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderItemInGUI(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.5F, 0.7F, packedLight, 32);
    }

    @Override
    public void renderItemOnGround(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight, 16);
    }

    @Override
    public void renderItemFirstPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.2);
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight, 64);
    }

    @Override
    public void renderItemThirdPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.25);
        renderBaseRings(matrix, buffer, 0.33F, 0.7F, packedLight, 32);
    }

    @Override
    public void renderItemInFrame(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.5F, 0.7F, packedLight, 32);
    }

    @Override
    public void renderItemOnHead(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.5F, 0.7F, packedLight, 32);
    }

    public static void renderBaseRings(PoseStack matrix, MultiBufferSource buffer, float scale, float alpha, int packedLight, int resolution){
        Color ender = new Color(52,153,136, (int)(255 * alpha));
        Color blazeO = RenderUtils.applyAlpha(Color.ORANGE, alpha);
        CircularColorGradient innerGradient = new CircularColorGradient.Builder()
                .addColor(1.7F, ender)
                .addColor(2.0F, RenderUtils.shiftColor(ender, Color.GREEN, 0.2F))
                .addColor(2.1F, ender)
                .build();
        CircularColorGradient middleGradient = new CircularColorGradient.Builder()
                .addColor(0, RenderUtils.applyAlpha(Color.LIGHT_GRAY, alpha))
                .addColor(Utils.PI, RenderUtils.applyAlpha(Color.DARK_GRAY, alpha))
                .build();
        CircularColorGradient outerGradient = new CircularColorGradient.Builder()
                .addColor(0, blazeO)
                .addColor(0.2F, RenderUtils.applyAlpha(RenderUtils.shiftColor(blazeO, Color.RED, 0.3F), alpha))
                .addColor(0.35F, blazeO)
                .build();
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/600F), 0.5F,0,-0.5F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.45F, 0.035F, 0, resolution, innerGradient);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/-3000F), 0.5F,0,-0.5F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.6F, 0.12F, 0, resolution, middleGradient);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/-700F), 0.5F,0,-0.5F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.75F, 0.035F, 0, resolution, outerGradient);
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
