package net.nayrus.noteblockmaster.composer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

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
}
