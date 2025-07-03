package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import org.joml.Matrix4f;

import java.io.IOException;

public class GeoLayerMaidBladeRenderer<T extends Mob, R extends IGeoEntityRenderer<T>> extends GeoLayerRenderer<T, R> {
    final LazyOptional<MmdPmdModelMc> bladeholder = LazyOptional.of(() -> {
        try {
            return new MmdPmdModelMc(new ResourceLocation("slashblade", "model/bladeholder.pmd"));
        } catch (IOException | MmdException e) {
            throw new RuntimeException(e);
        }
    });
    final LazyOptional<MmdMotionPlayerGL2> motionPlayer = LazyOptional.of(() -> {
        MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();
        this.bladeholder.ifPresent((pmd) -> {
            try {
                mmp.setPmd(pmd);
            } catch (MmdException e) {
                TruePowerOfMaid.LOGGER.error("", e);
            }
        });
        return mmp;
    });

    public GeoLayerMaidBladeRenderer(R entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public GeoLayerMaidBladeRenderer<T, R> copy(R r) {
        return new GeoLayerMaidBladeRenderer<>(r);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float yOffset = 1.5F;
        double motionScale = 0.125F;
        double modelScaleBase = 0.0078125F;
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty()) {
            LazyOptional<ISlashBladeState> state = stack.getCapability(CapabilitySlashBlade.BLADESTATE);
            state.ifPresent((s) -> this.motionPlayer.ifPresent((mmp) -> {
                ComboState combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboSeq()) != null ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboSeq()) : ComboStateRegistry.NONE.get();
                double time;
                if (combo != null) {
                    for (time = TimeValueHelper.getMSecFromTicks((float) Math.max(0L, entity.level().getGameTime() - s.getLastActionTime()) + partialTicks); combo != ComboStateRegistry.NONE.get() && (double) combo.getTimeoutMS() < time; combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(combo.getNextOfTimeout(entity)) != null ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(combo.getNextOfTimeout(entity)) : ComboStateRegistry.NONE.get()) {
                        time -= combo.getTimeoutMS();
                    }
                    if (combo == ComboStateRegistry.NONE.get()) {
                        combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboRoot()) != null ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboRoot()) : ComboStateRegistry.STANDBY.get();
                    }
                    MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(combo.getMotionLoc());
                    double maxSeconds = 0.0F;
                    try {
                        mmp.setVmd(motion);
                        maxSeconds = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
                    } catch (Exception e) {
                        TruePowerOfMaid.LOGGER.error("", e);
                    }
                    double start = TimeValueHelper.getMSecFromFrames(combo.getStartFrame());
                    double end = TimeValueHelper.getMSecFromFrames(combo.getEndFrame());
                    double span = Math.abs(end - start);
                    span = Math.min(maxSeconds, span);
                    if (combo.getLoop()) {
                        time %= span;
                    }
                    time = Math.min(span, time);
                    time = start + time;
                    try {
                        mmp.updateMotion((float) time);
                    } catch (MmdException e) {
                        TruePowerOfMaid.LOGGER.error("", e);
                    }
                    try (MSAutoCloser ignored = MSAutoCloser.pushMatrix(matrixStack)) {
                        this.setUserPose(matrixStack, entity, partialTicks);
                        matrixStack.translate(0.0F, yOffset, 0.0F);
                        matrixStack.scale((float) motionScale, (float) motionScale, (float) motionScale);
                        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                        ResourceLocation textureLocation = s.getTexture().orElse(DefaultResources.resourceDefaultTexture);
                        WavefrontObject obj = BladeModelManager.getInstance().getModel(s.getModel().orElse(DefaultResources.resourceDefaultModel));
                        try (MSAutoCloser ignored1 = MSAutoCloser.pushMatrix(matrixStack)) {
                            int idx = mmp.getBoneIndexByName("hardpointA");
                            if (0 <= idx) {
                                float[] buf = new float[16];
                                mmp._skinning_mat[idx].getValue(buf);
                                Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
                                matrixStack.scale(-1.0F, 1.0F, 1.0F);
                                PoseStack.Pose entry = matrixStack.last();
                                entry.pose().mul(mat);
                                matrixStack.scale(-1.0F, 1.0F, 1.0F);
                            }
                            float modelScale = (float) (modelScaleBase * ((double) 1.0F / motionScale));
                            matrixStack.scale(modelScale, modelScale, modelScale);
                            String part;
                            if (s.isBroken()) {
                                part = "blade_damaged";
                            } else {
                                part = "blade";
                            }
                            BladeRenderState.renderOverrided(stack, obj, part, textureLocation, matrixStack, bufferIn, lightIn);
                            BladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                        }
                        try (MSAutoCloser ignored2 = MSAutoCloser.pushMatrix(matrixStack)) {
                            int idx = mmp.getBoneIndexByName("hardpointB");
                            if (0 <= idx) {
                                float[] buf = new float[16];
                                mmp._skinning_mat[idx].getValue(buf);
                                Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
                                matrixStack.scale(-1.0F, 1.0F, 1.0F);
                                PoseStack.Pose entry = matrixStack.last();
                                entry.pose().mul(mat);
                                matrixStack.scale(-1.0F, 1.0F, 1.0F);
                            }
                            float modelScale = (float) (modelScaleBase * ((double) 1.0F / motionScale));
                            matrixStack.scale(modelScale, modelScale, modelScale);
                            BladeRenderState.renderOverrided(stack, obj, "sheath", textureLocation, matrixStack, bufferIn, lightIn);
                            BladeRenderState.renderOverridedLuminous(stack, obj, "sheath_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                            if (s.isCharged(entity)) {
                                float f = (float) entity.tickCount + partialTicks;
                                BladeRenderState.renderChargeEffect(stack, f, obj, "effect", new ResourceLocation("textures/entity/creeper/creeper_armor.png"), matrixStack, bufferIn, lightIn);
                            }
                        }
                    }
                }
            }));
        }
    }

    public void setUserPose(PoseStack matrixStack, T entity, float partialTicks) {
        ILocationModel model = getLocationModel(entity);
        RenderUtils.prepMatrixForLocator(matrixStack, model.leftHandBones());
        matrixStack.translate(-0.35F, -0.8F, -0.5F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(15.0F));
        UserPoseOverrider.invertRot(matrixStack, entity, partialTicks);
    }
}
