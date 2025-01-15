package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.awt.*;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_PARTICLES;
import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS;

public class CoreRender {

    public static void renderCoresInRange(RenderLevelStageEvent e, Level level, int range){
        Vec3 camCenter = RenderUtils.getStableEyeCenter(Minecraft.getInstance().gameRenderer.getMainCamera());
        RenderLevelStageEvent.Stage stage = e.getStage();

        if(stage == AFTER_TRANSLUCENT_BLOCKS || stage == AFTER_PARTICLES) {
            RenderUtils.getBlocksInRange(range)
                    .filter(pos -> level.getBlockState(pos).is(Registry.TUNINGCORE))
                    .forEach(pos -> renderCore(pos, level.getBlockState(pos), e.getPoseStack(), stage, Utils.exponentialFloor(0.75F, range, (float) RenderUtils.distanceVecToBlock(camCenter, pos), 2)));
        }
    }

    public static void renderCore(BlockPos pos, BlockState state, PoseStack stack, RenderLevelStageEvent.Stage stage, float alpha){
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtils.pushAndTranslateRelativeToCam(stack);

        renderTorus(buffer, stack, stage, Color.BLUE, pos, 0.5F, alpha, 0.65F, 0.05F);

        stack.popPose();
    }

    public static void renderTorus(MultiBufferSource.BufferSource buffer, PoseStack matrix, RenderLevelStageEvent.Stage stage, Color color, BlockPos pos, float scale, float alpha, float radius, float innerRadius){
        matrix.pushPose();
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
        Matrix4f positionMatrix = matrix.last().pose();

        float offset = Utils.getRotationToX(pos.getCenter().subtract(RenderUtils.CURRENT_CAM_POS));
        RenderUtils.buildHalfTorus(positionMatrix, buffer.getBuffer(NBMRenderType.QUADS), color, scale, radius, innerRadius, stage == AFTER_PARTICLES ? offset :(offset + Utils.PI), alpha);

        matrix.popPose();
    }

}
