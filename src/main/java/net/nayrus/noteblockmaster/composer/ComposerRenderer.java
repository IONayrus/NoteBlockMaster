package net.nayrus.noteblockmaster.composer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class ComposerRenderer implements BlockEntityRenderer<ComposerBlockEntity> {

    @Override
    public void render(ComposerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        ItemStack item = blockEntity.getItem();
        if(item.isEmpty()) return;

        poseStack.pushPose();

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.rotateAround(Axis.ZP.rotationDegrees(-blockEntity.getRotation() - 45), 0.5F,-0.5F,0);
        poseStack.translate(0.48,-0.7,0.966);
        poseStack.scale(1.5F,1.5F,1.5F);

        renderer.renderStatic(
                item,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                1);

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderScreenOverlay(GuiGraphics guiGraphics, List<Note> notes, ComposeData data){
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        for(int i = 0; i < notes.size(); i++){
            String text = notes.get(i).toString();
            guiGraphics.drawString(mc.font, text, screenWidth / 2 - mc.font.width(text) / 2, screenHeight / 2 + i*10, 0xFFFFFF, false);
        }
    }
}
