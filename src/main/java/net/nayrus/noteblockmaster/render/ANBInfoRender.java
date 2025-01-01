package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.utils.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.awt.*;

public class ANBInfoRender {

    public static final int renderRadius = 16;

    public static void renderNoteInfo(RenderLevelStageEvent e, Player player){
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        Vec3 lookVec = new Vec3(cam.getLookVector());
        boolean detached = cam.isDetached();
        Vec3 blockCenter = detached ? camPos.add(lookVec.multiply(Utils.sphereVec(4))) : camPos;
        Level level = player.level();
        BlockPos.betweenClosedStream(new AABB(blockCenter.add(Utils.sphereVec(-renderRadius)), blockCenter.add(Utils.sphereVec(renderRadius))))
                .filter(pos -> level.getBlockState(pos).is(Registry.ADVANCED_NOTEBLOCK) && isInRenderRange(pos, blockCenter, lookVec, detached))
                .forEach(pos -> renderNoteOverlay(e, pos, level.getBlockState(pos), camPos));
    }

    public static boolean isInRenderRange(BlockPos pos, Vec3 center, Vec3 look, boolean expand){
        return (expand || pos.getCenter().subtract(center).dot(look) >= 0) && center.distanceToSqr(pos.getCenter()) <= renderRadius*renderRadius;
    }

    public static void renderNoteOverlay(RenderLevelStageEvent e, BlockPos pos, BlockState state, Vec3 center){

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = e.getPoseStack();
        matrix.pushPose();
        matrix.translate(-center.x(), -center.y(), -center.z());

        VertexConsumer builder;
        builder = buffer.getBuffer(NBMRenderType.BlockOverlay);

        matrix.pushPose();
        matrix.translate(pos.getX(), pos.getY() + 0.75f, pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));

        Matrix4f positionMatrix = matrix.last().pose();
        int note = AdvancedNoteBlock.getNoteValue(state) - 2;
        float rCol = Math.max(0.0F, Mth.sin((note/29f + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        float gCol = Math.max(0.0F, Mth.sin((note/29f + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        float bCol = Math.max(0.0F, Mth.sin((note/29f + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);

        RenderSystem.disableDepthTest();

        RenderUtils.renderFlippedCone(positionMatrix, builder, new Color(rCol, gCol, bCol), 0.25F);

        matrix.popPose();

        matrix.popPose();
        buffer.endBatch(NBMRenderType.BlockOverlay);

        RenderSystem.enableDepthTest();
    }
}
