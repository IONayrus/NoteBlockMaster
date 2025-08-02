package net.nayrus.noteblockmaster.render.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nayrus.noteblockmaster.network.payload.RemoveBlockInfo;
import net.nayrus.noteblockmaster.network.payload.RequestBlockInfo;
import net.nayrus.noteblockmaster.network.payload.SyncBlockInfos;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.render.CoreRender;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RenderUtils {

    public static Vec3 CURRENT_CAM_POS = Vec3.ZERO;

    public static Color shiftColor(Color base, Color target, float factor) {
        factor = Math.min(1, Math.max(-1, factor));
        if(factor >= 0)
            return new Color(
                    (int)(base.getRed()   + (target.getRed()   - base.getRed())   * factor),
                    (int)(base.getGreen() + (target.getGreen() - base.getGreen()) * factor),
                    (int)(base.getBlue()  + (target.getBlue()  - base.getBlue())  * factor),
                    (int)(base.getAlpha()  + (target.getAlpha()  - base.getAlpha())  * factor)
            );
        return new Color(
                (int)(base.getRed()   - (255 - target.getRed()   - base.getRed())   * factor),
                (int)(base.getGreen() - (255 - target.getGreen() - base.getGreen()) * factor),
                (int)(base.getBlue()  - (255 - target.getBlue()  - base.getBlue())  * factor),
                (int)(base.getAlpha()  - (255 - target.getAlpha()  - base.getAlpha())  * factor)
        );
    }

    public static Color applyAlpha(Color base, float alpha){
        alpha = Math.min(1, Math.max(0, alpha));
        return new Color(base.getRed() / 255.0F, base.getGreen() / 255.0F, base.getBlue() / 255.0F, alpha);
    }

    public static Vec3 getStableEyeCenter(Camera cam){
        return cam.isDetached() ? CURRENT_CAM_POS.add(new Vec3(cam.getLookVector()).multiply(Utils.sphereVec(4))) : CURRENT_CAM_POS;
    }

    public static Vec3 getStableEyeCenter(){
        return getStableEyeCenter(Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    public record RenderBlocks(List<BlockPos> blocks, List<BlockPos> cores) {}
    private static RenderBlocks CACHED_BLOCKS = new RenderBlocks(new ArrayList<>(), new ArrayList<>());
    public static ConcurrentHashMap<BlockPos, ANBInfoRender.BlockInfo> CACHED_BLOCK_INFO = new ConcurrentHashMap<>();
    private static final List<BlockPos> PENDING_CACHE = new ArrayList<>();
    private static long lastUpdate = 0L;
    private static long lastRequestTime = 0L;

    public static RenderBlocks getTargetBlocks(LevelAccessor level, boolean updateCache){
        long currentTime = Util.getMillis();
        if(!updateCache && currentTime - lastUpdate < 200) return CACHED_BLOCKS;
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 lookVec = new Vec3(cam.getLookVector());
        Vec3 blockCenter = getStableEyeCenter(cam);
        int renderRadius = Math.max(ANBInfoRender.INFO_RENDER_RADIUS, CoreRender.CORE_RENDER_RANGE);
        List<BlockPos> blocks = new ArrayList<>();
        List<BlockPos> cores = new ArrayList<>();
        BlockPos.betweenClosedStream(new AABB(blockCenter.add(Utils.sphereVec(-renderRadius)), blockCenter.add(Utils.sphereVec(renderRadius)))).forEach(pos->
        {
            BlockPos imPos = pos.immutable();
            ANBInfoRender.BlockInfo cachedInfo = CACHED_BLOCK_INFO.get(imPos);

            if(cachedInfo != null){
                if(isNotInRenderRange(pos, blockCenter, lookVec, cam.isDetached(), ANBInfoRender.INFO_RENDER_RADIUS)) return;
                blocks.add(imPos);
            } else{
                BlockState currentState = level.getBlockState(pos);
                if(currentState.is(Registry.TUNINGCORE)) {
                    if(isNotInRenderRange(pos, blockCenter, lookVec, cam.isDetached(), CoreRender.CORE_RENDER_RANGE)) return;
                    cores.add(pos.immutable());
                }else if(currentState.is(Registry.ADVANCED_NOTEBLOCK)){
                    if((!PENDING_CACHE.contains(imPos)) && currentTime - lastRequestTime > 100)
                        PENDING_CACHE.add(imPos);
                }
            }
        });
        lastUpdate = currentTime;
        CACHED_BLOCKS = new RenderBlocks(blocks, cores);

        if (!PENDING_CACHE.isEmpty() && currentTime - lastRequestTime > 500) {
            PacketDistributor.sendToServer(new RequestBlockInfo(new ArrayList<>(PENDING_CACHE)));

            PENDING_CACHE.clear();
            lastRequestTime = Util.getMillis();
        }
        return CACHED_BLOCKS;
    }

    public static void handleSyncPacket(final SyncBlockInfos payload, final IPayloadContext ignoredContext) {
        CACHED_BLOCK_INFO.putAll(payload.states());
        getTargetBlocks(Minecraft.getInstance().level, true);
    }

    public static void handleRemovePacket(final RemoveBlockInfo payload, final IPayloadContext ignoredContext){
        CACHED_BLOCK_INFO.remove(payload.position());
    }

    public static RenderBlocks getTargetBlocks(LevelAccessor level){
        return getTargetBlocks(level, false);
    }

    public static void clearCache() {
        CACHED_BLOCK_INFO.clear();
        PENDING_CACHE.clear();
        CACHED_BLOCKS = new RenderBlocks(new ArrayList<>(), new ArrayList<>());
    }

    public static double distanceVecToBlock(Vec3 vPos, BlockPos pos){
        return vPos.distanceTo(pos.getCenter());
    }

    public static boolean isNotInRenderRange(BlockPos pos, Vec3 center, Vec3 look, boolean fullCircle, int renderRadius){
        return distanceVecToBlock(center, pos) > renderRadius || (!fullCircle && !(pos.getCenter().subtract(center).dot(look) >= 0));
    }

    public static void pushAndTranslateRelativeToCam(PoseStack stack){
        stack.pushPose();
        stack.translate(-CURRENT_CAM_POS.x(), -CURRENT_CAM_POS.y(), -CURRENT_CAM_POS.z());
    }

    public static int getPackedLight(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return (skyLight << 4) | blockLight;
    }

    public static boolean eventOnRelevantStage(RenderLevelStageEvent e) {
        RenderLevelStageEvent.Stage stage = e.getStage();
        return stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || stage == RenderLevelStageEvent.Stage.AFTER_WEATHER;
    }
}
