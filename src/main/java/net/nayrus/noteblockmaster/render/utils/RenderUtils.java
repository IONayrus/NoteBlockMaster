package net.nayrus.noteblockmaster.render.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.awt.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RenderUtils {

    public static Vec3 CURRENT_CAM_POS = Vec3.ZERO;

    public static Color shiftColor(Color base, Color target, float factor) {
        factor = Math.min(1, Math.max(-1, factor));
        if(factor >= 0)
            return new Color(
                    (int)(base.getRed()   + (target.getRed()   - base.getRed())   * factor),
                    (int)(base.getGreen() + (target.getGreen() - base.getGreen()) * factor),
                    (int)(base.getBlue()  + (target.getBlue()  - base.getBlue())  * factor),
                    (int)(base.getAlpha()  + (target.getAlpha()  - base.getAlpha())  * factor)
            );
        return new Color(
                (int)(base.getRed()   - (255 - target.getRed()   - base.getRed())   * factor),
                (int)(base.getGreen() - (255 - target.getGreen() - base.getGreen()) * factor),
                (int)(base.getBlue()  - (255 - target.getBlue()  - base.getBlue())  * factor),
                (int)(base.getAlpha()  - (255 - target.getAlpha()  - base.getAlpha())  * factor)
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

    public static Stream<BlockPos> getBlocksInRange(int renderRadius, Predicate<BlockPos> additionalPredicate){
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 lookVec = new Vec3(cam.getLookVector());
        Vec3 blockCenter = getStableEyeCenter(cam);
        return BlockPos.betweenClosedStream(new AABB(blockCenter.add(Utils.sphereVec(-renderRadius)), blockCenter.add(Utils.sphereVec(renderRadius)))) //TODO Cache Blocks for 100ms or smth
                .filter(pos -> isInRenderRange(pos, blockCenter, lookVec, cam.isDetached(), renderRadius) && additionalPredicate.test(pos)); //TODO Swap statements
    }

    public static double distanceVecToBlock(Vec3 vPos, BlockPos pos){
        return vPos.distanceTo(pos.getCenter());
    }

    public static boolean isInRenderRange(BlockPos pos, Vec3 center, Vec3 look, boolean fullCircle, int renderRadius){
        return (fullCircle || pos.getCenter().subtract(center).dot(look) >= 0) && distanceVecToBlock(center, pos) <= renderRadius;  //TODO Inspect with spark
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

    public static boolean eventOnRelevantStage(RenderLevelStageEvent e) {
        RenderLevelStageEvent.Stage stage = e.getStage();
        return stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || stage == RenderLevelStageEvent.Stage.AFTER_WEATHER;
    }
}
