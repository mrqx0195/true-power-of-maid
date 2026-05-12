package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidClientConfig;
import net.mrqx.slashblade.maidpower.mixin.AccessorEntityRenderer;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(Dist.CLIENT)
public class MaidRankRenderer {
    public static final ResourceLocation RANK_IMG = ResourceLocation.fromNamespaceAndPath("slashblade", "textures/gui/rank.png");
    
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() instanceof EntityMaid maid && maid.getTask().getUid().equals(TaskSlashBlade.UID)) {
            IConcentrationRank cr = maid.getData(CapabilityConcentrationRank.RANK_POINT);
            long now = maid.level().getGameTime();
            LivingEntityRenderer<?, ?> renderer = event.getRenderer();
            IConcentrationRank.ConcentrationRanks rank = cr.getRank(now);
            if (rank != IConcentrationRank.ConcentrationRanks.NONE) {
                PoseStack poseStack = event.getPoseStack();
                
                float maidHeight = maid.getBbHeight() + 0.5F;
                
                poseStack.pushPose();
                poseStack.translate(0, maidHeight, 0);
                poseStack.mulPose(((AccessorEntityRenderer) renderer).getEntityRenderDispatcher().cameraOrientation());
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
                float size = TruePowerOfMaidClientConfig.MAID_RANK_SIZE.get().floatValue();
                poseStack.scale(-size, -size, size);
                
                boolean depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableDepthTest();
                
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                texturemanager.getTexture(RANK_IMG).setFilter(false, false);
                RenderSystem.setShaderTexture(0, RANK_IMG);
                
                boolean showTextRank = false;
                long textTimeout = cr.getLastRankRise() + 20L;
                if (now < textTimeout) {
                    showTextRank = true;
                }
                
                int x = TruePowerOfMaidClientConfig.MAID_RANK_X.get();
                int y = TruePowerOfMaidClientConfig.MAID_RANK_Y.get();
                
                int rankOffset = 32 * (rank.level - 1);
                int textOffset = showTextRank ? 128 : 0;
                int progress = (int) (33.0F * cr.getRankProgress(now));
                int progressIcon = (int) (18.0F * cr.getRankProgress(now));
                int progressIconInv = 17 - progressIcon;
                drawTexturedQuad(poseStack, x, y, textOffset + 64, rankOffset, 64, 32, 0);
                drawTexturedQuad(poseStack, x, y + progressIconInv + 7, textOffset, rankOffset + progressIconInv + 7, 64, progressIcon, 0);
                drawTexturedQuad(poseStack, x, y + 32, 0, 240, 64, 16, 0);
                drawTexturedQuad(poseStack, x + 16, y + 32, 16, 224, progress, 16, 0);
                
                if (depthTestEnabled) {
                    RenderSystem.enableDepthTest();
                } else {
                    RenderSystem.disableDepthTest();
                }
                poseStack.popPose();
            }
        }
    }
    
    
    public static void drawTexturedQuad(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float zLevel) {
        float f = 1 / 256f;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder wr = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f m = poseStack.last().pose();
        float u1 = u * f;
        float v1 = v * f;
        float u2 = width * f;
        float v2 = height * f;
        wr.addVertex(m, x, y + height, zLevel).setUv(u1, v1 + v2);
        wr.addVertex(m, x + width, y + height, zLevel).setUv(u1 + u2, v1 + v2);
        wr.addVertex(m, x + width, y, zLevel).setUv(u1 + u2, v1);
        wr.addVertex(m, x, y, zLevel).setUv(u1, v1);
        MeshData meshData = wr.build();
        if (meshData != null) {
            BufferUploader.drawWithShader(meshData);
        }
    }
}
