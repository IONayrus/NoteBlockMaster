package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class ANBInfoRender {

    public static final int renderRadius = 18;
    public static boolean NOTE_OFF_SYNC = false;
    public static boolean SUBTICK_OFF_SYNC = false;

    public static void renderNoteBlockInfo(RenderLevelStageEvent e, Level level, Utils.PROPERTY info){
        RenderSystem.disableDepthTest();
        RenderUtils.getBlocksInRange(renderRadius, pos -> level.getBlockState(pos).is(Registry.ADVANCED_NOTEBLOCK))
                .forEach(pos -> renderNoteBlockInfo(e, pos, level.getBlockState(pos), info));
        RenderSystem.enableDepthTest();
    }

    public static void renderNoteBlockInfo(RenderLevelStageEvent e, BlockPos pos, BlockState state, Utils.PROPERTY info){
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack matrix = e.getPoseStack();
        Color color = AdvancedNoteBlock.getColor(state, info);

        RenderUtils.pushAndTranslateRelativeToCam(matrix);

        float alpha = Utils.exponentialFloor(1.0F, renderRadius, (float) RenderUtils.distanceVecToBlock(RenderUtils.getStableEyeCenter(), pos), 4);
        renderColoredCone(buffer, matrix, color, pos, 0.2F, 0.33F * alpha);

        String text = switch(info){
            case NOTE -> NOTE_OFF_SYNC ? "%" : Utils.NOTE_STRING[AdvancedNoteBlock.getNoteValue(state)];
            case TEMPO -> SUBTICK_OFF_SYNC ? "%" : state.getValue(AdvancedNoteBlock.SUBTICK).toString();
        };
        renderInfoLabel(buffer, matrix, text, color, pos, 0.025F, alpha);

        matrix.popPose();
    }

    public static void renderColoredCone(MultiBufferSource.BufferSource buffer, PoseStack matrix, Color color, BlockPos pos, float scale, float alpha){
        matrix.pushPose();
        matrix.translate(pos.getX(), pos.getY() + 0.7F, pos.getZ());
        matrix.mulPose(Axis.YP.rotationDegrees(-90.0F));
        Matrix4f positionMatrix = matrix.last().pose();

        int resolution = ClientConfig.LOW_RESOLUTION_RENDER.get() ? 4 : (Math.max(24 - (int) RenderUtils.distanceVecToBlock(RenderUtils.CURRENT_CAM_POS, pos) * 2, 8));
        RenderUtils.renderFlippedCone(positionMatrix, buffer.getBuffer(NBMRenderType.SEE_THROUGH_TRIANGLES), color, scale, alpha, resolution);

        matrix.popPose();
    }

    public static void renderInfoLabel(MultiBufferSource.BufferSource buffer, PoseStack matrix, String text, Color color, BlockPos pos, float scale, float alpha){
        matrix.pushPose();
        matrix.translate(pos.getX() + 0.5, pos.getY() + 1.6F, pos.getZ() + 0.5);

        renderInfoText(buffer, matrix, pos, text, RenderUtils.applyAlpha(RenderUtils.shiftColor(Color.LIGHT_GRAY, color, 0.8F), alpha), scale, new Vector3f(),0);
        renderInfoText(buffer, matrix, pos, text, RenderUtils.applyAlpha(RenderUtils.shiftColor(color, Color.BLACK, 0.5F), alpha / 1.25F), scale, new Vector3f(-0.008F),0);
        renderInfoText(buffer, matrix, pos, " ".repeat(text.length() + 2), Color.BLACK, scale - 0.003F, new Vector3f(0.0F,0F,-0.02F), RenderUtils.applyAlpha(RenderUtils.shiftColor(Color.WHITE, color, 0.33F), alpha / 3.0F).getRGB());

        matrix.popPose();
    }

    public static void renderInfoText(MultiBufferSource.BufferSource buffer, PoseStack matrix, BlockPos pos, String text, Color color, float scale, Vector3f offset, int background){
        Font textRender = Minecraft.getInstance().font;
        Vec3 viewVec = new Vec3(pos.getX() + 0.5, pos.getY() + 1.6, pos.getZ() + 0.5).subtract(RenderUtils.CURRENT_CAM_POS);

        matrix.pushPose();
        matrix.translate(-offset.x(), offset.y(), offset.z()); //Bottom-Back-Right for neg val
        rotateTextToPlayer(matrix,textRender.width(text) * scale / 2.0F, viewVec, offset);
        matrix.scale(scale, scale, scale);

        textRender.drawInBatch(text, 0.0F, 0.0F, color.getRGB(), false, matrix.last().pose(), buffer, Font.DisplayMode.SEE_THROUGH, background, 15728880, false);
        matrix.popPose();
    }

    public static void rotateTextToPlayer(PoseStack matrix, float halfTextWidth, Vec3 viewVec, Vector3f offset){
        double len = viewVec.length();
        matrix.mulPose(Axis.XP.rotationDegrees(180.0F));

        Quaternionf tilt = Axis.XP.rotation((float) (Math.PI/2 * (viewVec.y() / len)));
        Quaternionf rotation = Axis.YP.rotation(Utils.getRotationToX(viewVec) + Utils.PI / 2);
        rotation.mul(tilt);
        matrix.rotateAround(rotation, offset.x(), offset.y(), offset.z());

        matrix.translate(-halfTextWidth, 0 , 0);
    }

}
