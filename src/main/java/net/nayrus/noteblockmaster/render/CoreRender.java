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
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.*;

public class CoreRender {

    public static Map<BlockPos, Long> ANIMATION_ON_POS = new HashMap<>();

    public static void renderCoresInRange(RenderLevelStageEvent e, Level level, int range){
        Vec3 camCenter = RenderUtils.getStableEyeCenter(Minecraft.getInstance().gameRenderer.getMainCamera());
        RenderLevelStageEvent.Stage stage = e.getStage();
        if(stage == AFTER_LEVEL){
            ANIMATION_ON_POS.entrySet().removeIf(entry -> {
                if (!level.getBlockState(entry.getKey()).is(Registry.TUNINGCORE)) return true;
                entry.setValue(entry.getValue() + 1);
                return false;
            });
        }
        if(stage == AFTER_TRANSLUCENT_BLOCKS || stage == AFTER_PARTICLES) {
            RenderUtils.getBlocksInRange(range)
                    .filter(pos -> level.getBlockState(pos).is(Registry.TUNINGCORE))
                    .forEach(pos -> renderCore(pos, level.getBlockState(pos), e.getPoseStack(), stage, Utils.exponentialFloor(0.5F, range, (float) RenderUtils.distanceVecToBlock(camCenter, pos), 2)));
        }
    }

    public static void renderCore(BlockPos pos, BlockState state, PoseStack stack, RenderLevelStageEvent.Stage stage, float alpha){
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtils.pushAndTranslateRelativeToCam(stack);
        BlockPos immutablePos = pos.immutable();
        ANIMATION_ON_POS.putIfAbsent(immutablePos, Util.getNanos());
        long anime = Math.abs(ANIMATION_ON_POS.get(immutablePos));
        if(TuningCore.isSustaining(state) || true){
            int sustain = state.getValue(TuningCore.SUSTAIN);
            int steps = (int)(100.0 * (5 - 4 * (1 / (200.0 / sustain))));
            float animation = (anime % steps) / (steps * 2.0F);
            float halfShift = (animation + 0.25F) % 0.5F;
            renderTorus(buffer, stack, stage, Color.BLUE, pos, pos.getY() - animation + 0.1F, 0.5F, alpha * getAlphaFactor(animation), 0.65F, 0.05F);
            renderTorus(buffer, stack, stage, Color.BLUE, pos, pos.getY() - halfShift + 0.1F, 0.5F, alpha * getAlphaFactor(halfShift), 0.65F, 0.05F);
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

        float offset = Utils.getRotationToX(pos.getCenter().subtract(RenderUtils.CURRENT_CAM_POS));
        RenderUtils.buildHalfTorus(positionMatrix, buffer.getBuffer(NBMRenderType.QUADS), color, scale, radius, innerRadius, stage == AFTER_PARTICLES ? offset :(offset + Utils.PI), alpha);

        matrix.popPose();
    }

}
