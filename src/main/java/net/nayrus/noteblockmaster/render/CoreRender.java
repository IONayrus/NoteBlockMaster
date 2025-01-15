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

public class CoreRender {

    public static void renderCoresInRange(RenderLevelStageEvent e, Level level, int range){
        Vec3 camCenter = RenderUtils.getCameraCenter(Minecraft.getInstance().gameRenderer.getMainCamera());
        RenderUtils.getBlocksInRange(range)
                .filter(pos -> level.getBlockState(pos).is(Registry.TUNINGCORE))
                .forEach(pos -> renderCore(pos, level.getBlockState(pos), e.getPoseStack(), Utils.exponentialFloor(0.5F, range, (float) RenderUtils.distanceVecToBlock(camCenter, pos), 2)));

    }

    public static void renderCore(BlockPos pos, BlockState state, PoseStack stack, float alpha){
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtils.pushAndTranslateRelativeToCam(stack);

        renderTorus(buffer, stack, Color.BLUE, pos, 0.5F, alpha);
        stack.popPose();
    }

    public static void renderTorus(MultiBufferSource.BufferSource buffer, PoseStack matrix, Color color, BlockPos pos, float scale, float alpha){
        matrix.pushPose();
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
        Matrix4f positionMatrix = matrix.last().pose();

        RenderUtils.buildTorus(positionMatrix, buffer.getBuffer(NBMRenderType.SEE_THROUGH_QUADS), color, scale, 0.7F,0.07F, alpha);

        matrix.popPose();
    }

}
