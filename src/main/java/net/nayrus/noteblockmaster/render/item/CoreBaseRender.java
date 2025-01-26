package net.nayrus.noteblockmaster.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.render.NBMRenderType;
import net.nayrus.noteblockmaster.render.utils.CircularColorGradient;
import net.nayrus.noteblockmaster.render.utils.GeometryBuilder;
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
        renderBaseRings(matrix, buffer, 0.45F,  packedLight, 32);
    }

    @Override
    public void renderItemOnGround(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if(!(stack.getEntityRepresentation() instanceof ItemEntity e)) return;
        double distance = RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, e.getOnPos());
        renderBaseRings(matrix, buffer, 0.25F, packedLight, Math.max(48 - (int)(36 * distance / 16), 12));
    }

    @Override
    public void renderItemFirstPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.15);
        matrix.rotateAround(Axis.ZP.rotation(hand == InteractionHand.MAIN_HAND ? 0.25F : -0.25F), 0.5F,0.5F,0);
        renderBaseRings(matrix, buffer, 0.25F,  packedLight, 64);
    }

    @Override
    public void renderItemThirdPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.2);
        renderBaseRings(matrix, buffer, 0.25F, packedLight, 64);
    }

    @Override
    public void renderItemInFrame(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if(!(stack.getEntityRepresentation() instanceof Entity e)) return;
        double distance = RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, e.getOnPos());
        renderBaseRings(matrix, buffer, 0.45F, packedLight, Math.max(64 - (int)(56 * distance / 20), 8));
    }

    @Override
    public void renderItemOnHead(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderBaseRings(matrix, buffer, 0.45F, packedLight, 32);
    }

    private static final CircularColorGradient innerGradient;
    private static final CircularColorGradient middleGradient;
    private static final CircularColorGradient outerGradient;

    public static void renderBaseRings(PoseStack matrix, MultiBufferSource buffer, float scale, int packedLight, int resolution){
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/600F), 0.5F,0,-0.5F);
        matrix.rotateAround(Axis.ZP.rotation(Mth.sin(Util.getMillis()/600F)*Mth.cos(Util.getMillis()/-600F)/10), 0.5F,0.5F,0);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.45F, 0.045F, 0, resolution, innerGradient);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/-3000F), 0.5F,0,-0.5F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.6F, 0.11F, 0, resolution, middleGradient);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/-700F), 0.5F,0,-0.5F);
        matrix.rotateAround(Axis.ZP.rotation(Mth.sin(Util.getMillis()/-700F)*Mth.cos(Util.getMillis()/700F)/10), 0.5F,0.5F,0);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.75F, 0.045F, 0, resolution, outerGradient);
        matrix.popPose();
    }

    static{
        int alpha = 179;
        int alphaIron = 205;
        float[] point = new float[32]; for(int i = 0; i < 32; i++) point[i] = i * Utils.PI / 16;
        Color ender = new Color(52,153,136, alpha);
        Color enderDark = new Color(16,94,81, alpha);
        Color enderDarker = new Color(11,77,66, alpha);
        Color enderBright = new Color(44, 205, 177, alpha);
        Color enderWhite = new Color(140,244,226, alpha);
        Color enderBlack = new Color(3,38,32, alpha);
        Color iron = new Color(216,216,216, alphaIron);
        Color ironGray = new Color(168,168,168, alphaIron);
        Color ironDark = new Color(114,114,114, alphaIron);
        Color ironWhite = RenderUtils.applyAlpha(Color.WHITE, alphaIron / 255F);
        Color blazeOrange = new Color(255,163,0, alpha);
        Color blazeGold = new Color(255, 224,0, alpha);
        Color blazeYellow = new Color(255,254, 49, alpha);
        Color blazeWhite = new Color(255,255,181, alpha);
        innerGradient = new CircularColorGradient.Builder()
                .addColor(point[0], ender)
                .addColor(point[2], enderDarker)
                .addColor(point[5], enderDark)
                .addColor(point[6], ender)
                .addColor(point[8], enderDark)
                .addColor(point[10], enderBlack)
                .addColor(point[11], enderDarker)
                .addColor(point[13], ender)
                .addColor(point[14], enderBright)
                .addColor(point[15], enderWhite)
                .addColor(point[17], enderBright)
                .addColor(point[19], enderDark)
                .addColor(point[21], ender)
                .addColor(point[24], enderDark)
                .addColor(point[25], ender)
                .addColor(point[27], enderBright)
                .addColor(point[29], enderWhite)
                .addColor(point[31], enderDark)
                .build();
        middleGradient = new CircularColorGradient.Builder()
                .addColor(point[0], iron)
                .addColor(point[6], iron)
                .addColor(point[8], ironWhite)
                .addColor(point[9], iron)
                .addColor(point[12], ironGray)
                .addColor(point[14], ironDark)
                .addColor(point[15], ironDark)
                .addColor(point[17], ironGray)
                .addColor(point[20], iron)
                .addColor(point[24], iron)
                .addColor(point[26], ironWhite)
                .addColor(point[28], iron)
                .addColor(point[29], ironGray)
                .addColor(point[31], ironGray)
                .build();
        outerGradient = new CircularColorGradient.Builder()
                .addColor(point[1], blazeWhite)
                .addColor(point[2], blazeOrange)
                .addColor(point[4], blazeYellow)
                .addColor(point[5], blazeWhite)
                .addColor(point[6], blazeGold)
                .addColor(point[10], blazeOrange)
                .addColor(point[14], blazeOrange)
                .addColor(point[18], blazeGold)
                .addColor(point[23], blazeYellow)
                .addColor(point[24], blazeWhite)
                .addColor(point[27], blazeOrange)
                .addColor(point[30], blazeGold)
                .build();
    }

    public static class Extension implements IClientItemExtensions {

        private final BaseItemRender baseItemRender = new CoreBaseRender();

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return baseItemRender;
        }
    }
}
