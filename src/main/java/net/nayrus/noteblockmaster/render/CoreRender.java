package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.network.payload.TickSchedule;
import net.nayrus.noteblockmaster.render.item.SpinningCoreRender;
import net.nayrus.noteblockmaster.render.utils.GeometryBuilder;
import net.nayrus.noteblockmaster.render.utils.RenderUtils;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.*;

public class CoreRender {

    public static Map<BlockPos, Float> OFFSET_ON_POS = new HashMap<>();
    public static Map<BlockPos, Long> LAST_STAGE_TIME = new HashMap<>();
    public static int RANGE = 20;

    public static void renderCoresInRange(RenderLevelStageEvent e, Level level){
        Vec3 camCenter = RenderUtils.getStableEyeCenter(Minecraft.getInstance().gameRenderer.getMainCamera());
        RenderLevelStageEvent.Stage stage = e.getStage();
        if(stage == AFTER_TRANSLUCENT_BLOCKS || (stage == AFTER_WEATHER && !ClientConfig.LOW_RESOLUTION_RENDER.get())) {
            RenderUtils.getBlocksInRange(RANGE, pos -> level.getBlockState(pos).is(Registry.TUNINGCORE))
                    .forEach(pos -> {
                        long renderTime;
                        if(stage == AFTER_TRANSLUCENT_BLOCKS){
                            renderTime = Util.getMillis();
                            LAST_STAGE_TIME.put(pos.immutable(), renderTime);
                        }else renderTime = LAST_STAGE_TIME.getOrDefault(pos.immutable(), Util.getMillis());
                        renderCore(level, pos, level.getBlockState(pos), e.getPoseStack(), stage, Utils.exponentialFloor(0.5F, RANGE, (float) RenderUtils.distanceVecToBlock(camCenter, pos), 2), renderTime);
                    });
        }
        if(stage == AFTER_WEATHER){
            OFFSET_ON_POS.entrySet().removeIf(entry -> {
                BlockPos pos = entry.getKey();
                if (!level.getBlockState(pos).is(Registry.TUNINGCORE)){
                    PacketDistributor.sendToServer(new TickSchedule(pos, 0));
                    return true;
                }
                return false;
            });
        }
    }

    public static void renderCore(Level level, BlockPos pos, BlockState state, PoseStack stack, RenderLevelStageEvent.Stage stage, float alpha, long time){
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtils.pushAndTranslateRelativeToCam(stack);
        BlockPos immutablePos = pos.immutable();
        OFFSET_ON_POS.putIfAbsent(immutablePos, Math.abs(level.getRandom().nextFloat()));
        float anime = time/6.0F + OFFSET_ON_POS.get(immutablePos);
        if(TuningCore.isMixing(state)){
            int volume = state.getValue(TuningCore.VOLUME);
            int steps = (int)(100.0 * (5 - 4 * (1 / (18.0 / (19 - volume)))));
            float animation = (anime % steps) / (steps * 2.0F);
            float halfShift = (animation + 0.25F) % 0.5F;
            renderTorus(buffer, stack, stage, RenderUtils.applyAlpha(SpinningCoreRender.WOOL_COLOR, 1), pos, pos.getY() - 0.45F, 0.5F, alpha * getAlphaFactor(animation), 1 - animation * 1.8F, 0.05F);
            renderTorus(buffer, stack, stage, RenderUtils.applyAlpha(SpinningCoreRender.WOOL_COLOR, 1), pos, pos.getY() - 0.45F, 0.5F, alpha * getAlphaFactor(halfShift), 1 - halfShift * 1.8F, 0.05F);
        }
        if(TuningCore.isSustaining(state)){
            int sustain = state.getValue(TuningCore.SUSTAIN);
            int steps = (int)(100.0 * (5 - 4 * (1 / ((float) TuningCore.SUSTAIN_MAXVAL / sustain))));
            float animation = (anime % steps) / (steps * 2.0F);
            float halfShift = (animation + 0.25F) % 0.5F;
            renderTorus(buffer, stack, stage, RenderUtils.shiftColor(SpinningCoreRender.LAPIS_COLOR, Color.BLUE,0.3F), pos, pos.getY() - animation + 0.1F, 0.5F, alpha * getAlphaFactor(animation), 0.65F, 0.05F);
            renderTorus(buffer, stack, stage, RenderUtils.shiftColor(SpinningCoreRender.LAPIS_COLOR, Color.BLUE,0.3F), pos, pos.getY() - halfShift + 0.1F, 0.5F, alpha * getAlphaFactor(halfShift), 0.65F, 0.05F);
        }
        stack.popPose();
    }

    private static float getAlphaFactor(float animation){
        float mid = Math.abs(animation - 0.25F);
        return (mid > 0.125 ? (0.25F - mid) * 8.0F : 1F);
    }

    public static void renderTorus(MultiBufferSource.BufferSource buffer, PoseStack matrix, RenderLevelStageEvent.Stage stage, Color color, BlockPos pos, float yPos, float scale, float alpha, float radius, float innerRadius){
        matrix.pushPose();
        matrix.translate(pos.getX(), yPos, pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
        Matrix4f positionMatrix = matrix.last().pose();

        if(!ClientConfig.LOW_RESOLUTION_RENDER.get()) {
            float offset = Utils.getRotationToX(pos.getCenter().subtract(RenderUtils.CURRENT_CAM_POS));
            int resolution = (Math.max(32 - (int) RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, pos) * 4, 16));
            GeometryBuilder.buildHalfTorus(positionMatrix, buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), color, scale, radius, innerRadius,
                    stage == AFTER_WEATHER ? offset : (offset + Utils.PI), alpha, resolution % 2 == 0 ? resolution : resolution - 1);
        }else{
            GeometryBuilder.buildTorus(positionMatrix, buffer.getBuffer(NBMRenderType.TRANSLUCENT_QUADS), color, scale, radius, innerRadius, alpha, 4);
        }

        matrix.popPose();
    }

    public static void clearMaps(){
        OFFSET_ON_POS.entrySet().removeIf(entry -> {
            if(RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, entry.getKey()) > RANGE){
                LAST_STAGE_TIME.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

}
