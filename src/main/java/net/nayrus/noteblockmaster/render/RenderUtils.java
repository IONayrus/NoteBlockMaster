package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.utils.Utils;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.stream.Stream;

public class RenderUtils {

    public static Vec3 CURRENT_CAM_POS = Vec3.ZERO;

    public static void renderFlippedCone(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float alpha) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f;

        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float dX = Math.abs(midX - startX), dZ = Math.abs(midX - startX);

        int resolution = 32;

        for(int i = 0; i < resolution; i++){
            float w1 = (i / (float)resolution) * Utils.PI * 2;
            float w2 = ((i + 1) /(float)resolution) * Utils.PI * 2;
            //Top
            builder.addVertex(matrix, midX, endY, midZ).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w1), endY, midZ + dZ * Mth.sin(w1)).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w2), endY, midZ + dZ * Mth.sin(w2)).setColor(red, green, blue, alpha);
            //Side
            builder.addVertex(matrix, midX, startY, midZ).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w1), endY, midZ + dZ * Mth.sin(w1)).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w2), endY, midZ + dZ * Mth.sin(w2)).setColor(red, green, blue, alpha);
        }
    }

    public static void buildHalfTorus(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float radius, float innerRadius, float radialOffset, float alpha, int resolution) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f;

        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midY = (startY + endY) / 2, midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float r = innerRadius * scale;
        float R = radius * scale;

        for(int i = 0; i< resolution / 2; i++) {
            float w1 = (i / (float) resolution) * Utils.PI * 2 + radialOffset;
            float w2 = ((i + 1) / (float) resolution) * Utils.PI * 2 + radialOffset;
            float cos1 = Mth.cos(w1), cos2 = Mth.cos(w2);
            float sin1 = Mth.sin(w1), sin2 = Mth.sin(w2);

            for (int k = 0; k < resolution; k++) {
                float w3 = (k / (float) resolution) * Utils.PI * 2;
                float w4 = ((k + 1) / (float) resolution) * Utils.PI * 2;
                float cos3 = Mth.cos(w3), cos4 = Mth.cos(w4);
                float sin3 = Mth.sin(w3), sin4 = Mth.sin(w4);

                builder.addVertex(matrix, midX + (R + r * cos3) * cos1, midY + r * sin3, midZ + (R + r * cos3) * sin1).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos4) * cos1, midY + r * sin4, midZ + (R + r * cos4) * sin1).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos4) * cos2, midY + r * sin4, midZ + (R + r * cos4) * sin2).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos3) * cos2, midY + r * sin3, midZ + (R + r * cos3) * sin2).setColor(red, green, blue, alpha);
            }
        }
    }

    public static void buildTorus(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float radius, float innerRadius, float alpha, int resolution) {
        buildHalfTorus(matrix, builder, color, scale, radius, innerRadius,0, alpha, resolution);
        buildHalfTorus(matrix, builder, color, scale, radius, innerRadius, Utils.PI, alpha, resolution);
    }

    public static Color shiftColor(Color base, Color target, float factor) {
        factor = Math.min(1, Math.max(-1, factor));
        if(factor >= 0)
            return new Color(
                (int)(base.getRed()   + (target.getRed()   - base.getRed())   * factor),
                (int)(base.getGreen() + (target.getGreen() - base.getGreen()) * factor),
                (int)(base.getBlue()  + (target.getBlue()  - base.getBlue())  * factor)
            );
        return new Color(
                (int)(base.getRed()   - (255 - target.getRed()   - base.getRed())   * factor),
                (int)(base.getGreen() - (255 - target.getGreen() - base.getGreen()) * factor),
                (int)(base.getBlue()  - (255 - target.getBlue()  - base.getBlue())  * factor)
        );
    }

    public static Color applyAlpha(Color base, float alpha){
        alpha = Math.min(1, Math.max(0, alpha));
        return new Color(base.getRed() / 255.0F, base.getGreen() / 255.0F, base.getBlue() / 255.0F, alpha);
    }

    public static Vec3 getStableEyeCenter(Camera cam){
        return cam.isDetached() ? CURRENT_CAM_POS.add(new Vec3(cam.getLookVector()).multiply(Utils.sphereVec(4))) : CURRENT_CAM_POS;
    }

    public static Vec3 getStableEyeCenter(){
        return getStableEyeCenter(Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public static Stream<BlockPos> getBlocksInRange(int renderRadius){
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 lookVec = new Vec3(cam.getLookVector());
        Vec3 blockCenter = getStableEyeCenter(cam);
        return BlockPos.betweenClosedStream(new AABB(blockCenter.add(Utils.sphereVec(-renderRadius)), blockCenter.add(Utils.sphereVec(renderRadius))))
                .filter(pos -> isInRenderRange(pos, blockCenter, lookVec, cam.isDetached(), renderRadius));
    }

    public static double distanceVecToBlock(Vec3 vPos, BlockPos pos){
        return vPos.distanceTo(pos.getCenter());
    }

    public static boolean isInRenderRange(BlockPos pos, Vec3 center, Vec3 look, boolean fullCircle, int renderRadius){
        return (fullCircle || pos.getCenter().subtract(center).dot(look) >= 0) && distanceVecToBlock(center, pos) <= renderRadius;
    }

    public static void pushAndTranslateRelativeToCam(PoseStack stack){
        stack.pushPose();
        stack.translate(-CURRENT_CAM_POS.x(), -CURRENT_CAM_POS.y(), -CURRENT_CAM_POS.z());
    }

    public static int getPackedLight(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return (skyLight << 4) | blockLight;
    }

}
