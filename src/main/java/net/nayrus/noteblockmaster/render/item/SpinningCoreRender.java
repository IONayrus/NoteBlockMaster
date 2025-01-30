package net.nayrus.noteblockmaster.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.render.NBMRenderType;
import net.nayrus.noteblockmaster.render.utils.CircularColorGradient;
import net.nayrus.noteblockmaster.render.utils.GeometryBuilder;
import net.nayrus.noteblockmaster.render.utils.RenderUtils;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.awt.*;

public class SpinningCoreRender extends BaseItemRender{

    @Override
    public void renderItemInGUI(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderRotatingRings(matrix, buffer, getColorGradient(stack), 0.45F, packedLight, 32);
    }

    @Override
    public void renderItemOnGround(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if(!(stack.getEntityRepresentation() instanceof ItemEntity e)) return;
        double distance = RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, e.getOnPos());
        renderRotatingRings(matrix, buffer, getColorGradient(stack), 0.3F, packedLight, Math.max(40 - (int)(36 * distance / 16), 12));
    }

    @Override
    public void renderItemFirstPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.2);
        renderRotatingRings(matrix, buffer, getColorGradient(stack), 0.3F, packedLight ,48);
    }

    @Override
    public void renderItemThirdPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand) {
        matrix.translate(0.0,0,-0.25);
        renderRotatingRings(matrix, buffer, getColorGradient(stack), 0.3F, packedLight, 48);
    }

    @Override
    public void renderItemInFrame(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if(!(stack.getEntityRepresentation() instanceof Entity e)) return;
        double distance = RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, e.getOnPos());
        renderRotatingRings(matrix, buffer, getColorGradient(stack), 0.45F, packedLight, Math.max(64 - (int)(56 * distance / 20), 8));
    }

    @Override
    public void renderItemOnHead(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderItemInGUI(stack, matrix, buffer, packedLight, packedOverlay);
    }

    public void renderRotatingRings(PoseStack matrix, MultiBufferSource buffer, CircularColorGradient gradient, float scale, int packedLight, int resolution){
        matrix.pushPose();
        matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/1500F),0.5F,0F,-0.5F);
        matrix.rotateAround(Axis.XP.rotation(Util.getMillis()/700F),0F,0.5F,-0.5F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.7F, 0.08F, 0, resolution, gradient);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.XP.rotation(Util.getMillis()/3000F),0F,0.5F,-0.5F);
        matrix.rotateAround(Axis.ZP.rotation(Util.getMillis()/500F),0.5F,0.5F,0F);
        matrix.rotateAround(Axis.YP.rotation(-Util.getMillis()/1000F),0.5F,0F,-0.5F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.6F, 0.02F, 0, resolution, CoreBaseRender.IRON_GRADIENT);
        matrix.popPose();
        matrix.pushPose();
        matrix.rotateAround(Axis.XP.rotation(Util.getMillis()/8000F),0F,0.5F,-0.5F);
        //matrix.rotateAround(Axis.YP.rotation(Util.getMillis()/8000F),0.5F,0F,-0.5F);
        matrix.rotateAround(Axis.ZP.rotation(Util.getMillis()/300F),0.5F,0.5F,0F);
        GeometryBuilder.buildTorusWithGradient(matrix.last().pose(), buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), scale, 0.5F, 0.08F, 0, resolution, gradient);
        matrix.popPose();
    }

    public static final CircularColorGradient LAPIS;
    public static final CircularColorGradient WOOL;
    public static final Color LAPIS_COLOR;
    public static final Color WOOL_COLOR;

    public CircularColorGradient getColorGradient(ItemStack stack){
        return stack.is(Registry.SUSTAIN) ? LAPIS : WOOL;
    }

    static {
        int alpha = 190;
        float[] point = new float[32]; for(int i = 0; i < 32; i++) point[i] = i * Utils.PI / 16;
        LAPIS_COLOR = new Color(52, 94, 195, alpha);
        Color lapis = LAPIS_COLOR;
        Color lapisBright = new Color(90, 130, 226, alpha);
        Color lapisDark = new Color(18, 64, 139, alpha);
        WOOL_COLOR = new Color(254, 254, 254, alpha);
        Color woolWhite = WOOL_COLOR;
        Color wool = new Color(247, 248, 248, alpha);
        Color woolGray = new Color(232, 235, 235, alpha);
        LAPIS = new CircularColorGradient.Builder()
                .addColor(point[0], lapis)
                .addColor(point[9], lapis)
                .addColor(point[11], lapisBright)
                .addColor(point[13], lapis)
                .addColor(point[18], lapis)
                .addColor(point[20], lapisDark)
                .addColor(point[22], lapis)
                .addColor(point[26], lapis)
                .addColor(point[30], lapisBright)
                .build();
        WOOL = new CircularColorGradient.Builder()
                .addColor(point[30], wool)
                .addColor(point[6], wool)
                .addColor(point[8], woolWhite)
                .addColor(point[11], wool)
                .addColor(point[15], wool)
                .addColor(point[17], woolGray)
                .addColor(point[20], wool)
                .addColor(point[24], wool)
                .addColor(point[27], woolWhite)
                .build();
    }

    public static class Extension implements IClientItemExtensions {

        private final BaseItemRender baseItemRender = new SpinningCoreRender();

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return baseItemRender;
        }
    }
}
