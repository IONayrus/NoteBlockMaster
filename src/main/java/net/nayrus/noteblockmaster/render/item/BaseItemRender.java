package net.nayrus.noteblockmaster.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public abstract class BaseItemRender extends BlockEntityWithoutLevelRenderer {

    public BaseItemRender() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        matrix.pushPose();
        matrix.mulPose(Axis.XP.rotationDegrees(90.0F));
        switch (displayContext){
            case NONE -> {}
            case GUI -> renderItemInGUI(stack, matrix, buffer, packedLight, packedOverlay);
            case GROUND -> renderItemOnGround(stack, matrix, buffer, packedLight, packedOverlay);
            case FIRST_PERSON_RIGHT_HAND -> renderItemFirstPerson(stack, matrix, buffer, packedLight, packedOverlay, InteractionHand.MAIN_HAND);
            case FIRST_PERSON_LEFT_HAND -> renderItemFirstPerson(stack, matrix, buffer, packedLight, packedOverlay, InteractionHand.OFF_HAND);
            case THIRD_PERSON_RIGHT_HAND -> renderItemThirdPerson(stack, matrix, buffer, packedLight, packedOverlay, InteractionHand.MAIN_HAND);
            case THIRD_PERSON_LEFT_HAND -> renderItemThirdPerson(stack, matrix, buffer, packedLight, packedOverlay, InteractionHand.OFF_HAND);
            case FIXED -> renderItemInFrame(stack, matrix, buffer, packedLight, packedOverlay);
            case HEAD -> renderItemOnHead(stack, matrix, buffer, packedLight, packedOverlay);
        }
        matrix.popPose();
    }

    public abstract void renderItemInGUI(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay);
    public abstract void renderItemOnGround(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay);
    public abstract void renderItemFirstPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand);
    public abstract void renderItemThirdPerson(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay, InteractionHand hand);
    public abstract void renderItemInFrame(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay);
    public abstract void renderItemOnHead(ItemStack stack, PoseStack matrix, MultiBufferSource buffer, int packedLight, int packedOverlay);
}
