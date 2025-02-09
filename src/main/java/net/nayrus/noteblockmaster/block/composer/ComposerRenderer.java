package net.nayrus.noteblockmaster.block.composer;

import com.mojang.blaze3d.vertex.PoseStack;
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
        poseStack.translate(0,1.0,0);

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
