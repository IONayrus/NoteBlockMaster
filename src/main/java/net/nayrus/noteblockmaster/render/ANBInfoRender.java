package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
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
import org.joml.Vector3f;

import java.awt.*;

public class ANBInfoRender {

    public static final int renderRadius = 16;
    public static boolean NOTE_OFF_SYNC = false;
    public static boolean SUBTICK_OFF_SYNC = false;

    public static void renderNoteBlockInfo(RenderLevelStageEvent e, Player player, Utils.PROPERTY info){
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        Vec3 lookVec = new Vec3(cam.getLookVector());
        boolean detached = cam.isDetached();
        Vec3 blockCenter = detached ? camPos.add(lookVec.multiply(Utils.sphereVec(4))) : camPos;
        Level level = player.level();
        RenderSystem.disableDepthTest();
        BlockPos.betweenClosedStream(new AABB(blockCenter.add(Utils.sphereVec(-renderRadius)), blockCenter.add(Utils.sphereVec(renderRadius))))
                .filter(pos -> level.getBlockState(pos).is(Registry.ADVANCED_NOTEBLOCK) && isInRenderRange(pos, blockCenter, lookVec, detached))
                .forEach(pos -> renderNoteBlockInfo(e, pos, level.getBlockState(pos), camPos, info));
        RenderSystem.enableDepthTest();
    }

    public static boolean isInRenderRange(BlockPos pos, Vec3 center, Vec3 look, boolean expand){
        return (expand || pos.getCenter().subtract(center).dot(look) >= 0) && center.distanceToSqr(pos.getCenter()) <= renderRadius*renderRadius;
    }

    public static void renderNoteBlockInfo(RenderLevelStageEvent e, BlockPos pos, BlockState state, Vec3 camPos, Utils.PROPERTY info){
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack matrix = e.getPoseStack();
        Color color = AdvancedNoteBlock.getColor(state, info);

        matrix.pushPose();
        matrix.translate(-camPos.x(), -camPos.y(), -camPos.z());

        renderColoredCone(buffer, matrix, color, pos);

        String text = switch(info){
            case NOTE -> NOTE_OFF_SYNC ? "%" : Utils.NOTE_STRING[AdvancedNoteBlock.getNoteValue(state)];
            case TEMPO -> SUBTICK_OFF_SYNC ? "%" : state.getValue(AdvancedNoteBlock.SUBTICK).toString();
        };
        renderInfoLabel(buffer, matrix, text, color, pos, camPos);

        matrix.popPose();
    }

    public static void renderColoredCone(MultiBufferSource.BufferSource buffer, PoseStack matrix, Color color, BlockPos pos){
        matrix.pushPose();
        matrix.translate(pos.getX(), pos.getY() + 0.7F, pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
        Matrix4f positionMatrix = matrix.last().pose();

        RenderUtils.renderFlippedCone(positionMatrix, buffer.getBuffer(NBMRenderType.SEE_THROUGH_TRIANGLES), color, 0.2F, 0.33F);

        matrix.popPose();
    }

    public static void renderInfoLabel(MultiBufferSource.BufferSource buffer, PoseStack matrix, String text, Color color, BlockPos pos, Vec3 camPos){
        float scale = 0.025F;

        matrix.pushPose();
        matrix.translate(pos.getX() + 0.5, pos.getY() + 1.6F, pos.getZ() + 0.5);

        renderInfoText(buffer, matrix, pos, camPos, text, RenderUtils.shiftColor(Color.LIGHT_GRAY, color, 0.8F), scale, new Vector3f(),0);
        renderInfoText(buffer, matrix, pos, camPos, text, RenderUtils.applyAlpha(RenderUtils.shiftColor(color, Color.BLACK, 0.5F), 0.8F), scale, new Vector3f(-0.008F),0);
        renderInfoText(buffer, matrix, pos, camPos, " ".repeat(text.length() + 2), Color.BLACK, scale - 0.003F, new Vector3f(0.0F,0F,-0.02F), RenderUtils.applyAlpha(RenderUtils.shiftColor(Color.WHITE, color, 0.33F), 0.33F).getRGB());

        matrix.popPose();
    }

    public static void renderInfoText(MultiBufferSource.BufferSource buffer, PoseStack matrix, BlockPos pos,  Vec3 camPos, String text, Color color, float scale, Vector3f offset, int background){
        Font textRender = Minecraft.getInstance().font;
        Vec3 viewVec = new Vec3(pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5).subtract(camPos);

        matrix.pushPose();
        matrix.translate(-offset.x(), offset.y(), offset.z()); //Bottom-Back-Right for neg val
        rotateTextToPlayer(matrix,textRender.width(text) * scale / 2.0F, viewVec, offset);
        matrix.scale(scale, scale, scale);

        textRender.drawInBatch(text, 0.0F, 0.0F, color.getRGB(), false, matrix.last().pose(), buffer, Font.DisplayMode.SEE_THROUGH, background, 15728880, false);
        matrix.popPose();
    }

    public static void rotateTextToPlayer(PoseStack matrix, float halfTextWidth, Vec3 viewVec, Vector3f offset){
        double len = viewVec.length(), dY = viewVec.y();
        Vec3 viewXZ = viewVec.subtract(0, dY,0).normalize();
        double dotX = viewXZ.dot(new Vec3(1,0,0));
        double dotZ = viewXZ.dot(new Vec3(0,0,1));
        matrix.mulPose(Axis.XP.rotationDegrees(180.0F));

        Quaternionf tilt = Axis.XP.rotation((float) (Math.PI/2 * (dY / len)));
        Quaternionf rotation = Axis.YP.rotation((float) (Math.acos(dotX) * (dotZ < 0 ? -1 : 1) + Math.PI / 2));
        rotation.mul(tilt);
        matrix.rotateAround(rotation, offset.x(), offset.y(), offset.z());

        matrix.translate(-halfTextWidth, 0 , 0);
    }

}
