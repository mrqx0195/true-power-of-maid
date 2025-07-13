package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidClientConfig;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class MaidRankRenderer {
    public static final ResourceLocation RANK_IMG = new ResourceLocation("slashblade", "textures/gui/rank.png");

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent<?, ?> event) {
        if (event.getEntity() instanceof EntityMaid maid && maid.getTask().getUid().equals(TaskSlashBlade.UID)) {
            maid.getCapability(CapabilityConcentrationRank.RANK_POINT).ifPresent((cr) -> {
                long now = maid.level().getGameTime();
                LivingEntityRenderer<?, ?> renderer = event.getRenderer();
                IConcentrationRank.ConcentrationRanks rank = cr.getRank(now);
                if (rank != IConcentrationRank.ConcentrationRanks.NONE) {
                    PoseStack poseStack = event.getPoseStack();

                    float maidHeight = maid.getBbHeight() + 0.5F;

                    poseStack.pushPose();
                    poseStack.translate(0.0D, maidHeight, 0.0D);
                    poseStack.mulPose(renderer.entityRenderDispatcher.cameraOrientation());
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
                    ;
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
            });
        }
    }


    public static void drawTexturedQuad(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float zLevel) {
        float f = 0.00390625F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder wr = tesselator.getBuilder();
        wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f m = poseStack.last().pose();
        wr.vertex(m, (x), (y + height), zLevel).uv(((float) u + 0.0F) * f, (v + height) * f).endVertex();
        wr.vertex(m, (x + width), (y + height), zLevel).uv((float) (u + width) * f, (v + height) * f).endVertex();
        wr.vertex(m, (x + width), (y), zLevel).uv((u + width) * f, (v) * f).endVertex();
        wr.vertex(m, (x), (y), zLevel).uv((float) (u) * f, (v) * f).endVertex();
        BufferUploader.drawWithShader(wr.end());
    }
}
