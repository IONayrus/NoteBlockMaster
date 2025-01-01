package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import org.joml.Quaternionf;

import java.awt.*;

public class ANBInfoRender {

    public static final int renderRadius = 16;

    public enum PROPERTY { NOTE, TEMPO }

    public static void renderNoteInfo(RenderLevelStageEvent e, Player player, PROPERTY info){
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        Vec3 lookVec = new Vec3(cam.getLookVector());
        boolean detached = cam.isDetached();
        Vec3 blockCenter = detached ? camPos.add(lookVec.multiply(Utils.sphereVec(4))) : camPos;
        Level level = player.level();
        BlockPos.betweenClosedStream(new AABB(blockCenter.add(Utils.sphereVec(-renderRadius)), blockCenter.add(Utils.sphereVec(renderRadius))))
                .filter(pos -> level.getBlockState(pos).is(Registry.ADVANCED_NOTEBLOCK) && isInRenderRange(pos, blockCenter, lookVec, detached))
                .forEach(pos -> renderNoteOverlay(e, pos, level.getBlockState(pos), camPos, blockCenter, info));
    }

    public static boolean isInRenderRange(BlockPos pos, Vec3 center, Vec3 look, boolean expand){
        return (expand || pos.getCenter().subtract(center).dot(look) >= 0) && center.distanceToSqr(pos.getCenter()) <= renderRadius*renderRadius;
    }

    public static void renderNoteOverlay(RenderLevelStageEvent e, BlockPos pos, BlockState state, Vec3 camPos, Vec3 center, PROPERTY info){

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = e.getPoseStack();
        matrix.pushPose();
        matrix.translate(-camPos.x(), -camPos.y(), -camPos.z());

        VertexConsumer builder;
        builder = buffer.getBuffer(NBMRenderType.BlockOverlay);

        matrix.pushPose();
        matrix.translate(pos.getX(), pos.getY() + 0.7F, pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));

        Matrix4f positionMatrix = matrix.last().pose();
        RenderSystem.disableDepthTest();
        Color color = AdvancedNoteBlock.getColor(state, info);

        RenderUtils.renderFlippedCone(positionMatrix, builder, color, 0.2F);

        matrix.pushPose();

        String text = switch(info){
            case NOTE -> Utils.NOTE_STRING[AdvancedNoteBlock.getNoteValue(state)];
            case TEMPO -> state.getValue(AdvancedNoteBlock.SUBTICK).toString();
        };
        Font textRender = Minecraft.getInstance().font;

        Vec3 camToPos = pos.getCenter().add(0,1.25,0).subtract(camPos);
        double len = camToPos.length(), dY = camToPos.y();
        camToPos = camToPos.subtract(0, dY,0).normalize();
        float scale = 0.025F;
        float textWidth = textRender.width(text) * scale;
        double dotX = camToPos.dot(new Vec3(1,0,0));
        double dotZ = camToPos.dot(new Vec3(0,0,1));

        matrix.translate(0.5 - textWidth / 2.0F * dotX,0.9,-0.5 - textWidth / 2.0F * dotZ);
        matrix.mulPose(Axis.XP.rotationDegrees(-180.0F));

        Quaternionf tilt = Axis.XP.rotation((float)(Math.PI/2 * (dY / len)));
        Quaternionf yRot = Axis.YP.rotation((float) Math.acos(dotX) * (dotZ < 0 ? -1 : 1));
        matrix.mulPose(yRot.mul(tilt));

        matrix.scale(scale, scale, scale);
        positionMatrix = matrix.last().pose();

        textRender.drawInBatch(text, 0.0F, 0.0F, Color.LIGHT_GRAY.getRGB(), false, positionMatrix, buffer, Font.DisplayMode.SEE_THROUGH, 0, 15728880, false);

        matrix.popPose();
        matrix.popPose();
        matrix.popPose();

        buffer.endBatch(NBMRenderType.BlockOverlay);

        RenderSystem.enableDepthTest();
    }
}
