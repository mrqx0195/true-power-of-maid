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
import java.util.Objects;

public class GeoLayerMaidBladeRenderer<T extends Mob, R extends IGeoEntityRenderer<T>> extends GeoLayerRenderer<T, R> {
    private static final ResourceLocation CREEPER_ARMOR = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private final LazyOptional<MmdPmdModelMc> bladeHolder = LazyOptional.of(() -> {
        try {
            return new MmdPmdModelMc(new ResourceLocation("slashblade", "model/bladeholder.pmd"));
        } catch (IOException | MmdException e) {
            throw new RuntimeException(e);
        }
    });

    private final LazyOptional<MmdMotionPlayerGL2> motionPlayer = LazyOptional.of(() -> {
        MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();
        this.bladeHolder.ifPresent(pmd -> {
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
    public GeoLayerMaidBladeRenderer<T, R> copy(R renderer) {
        return new GeoLayerMaidBladeRenderer<>(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty()) {
            return;
        }
        stack.getCapability(CapabilitySlashBlade.BLADESTATE).ifPresent(state -> motionPlayer.ifPresent(mmp ->
                renderBlade(poseStack, buffer, light, entity, partialTicks, stack, state, mmp)));
    }

    private void renderBlade(PoseStack poseStack, MultiBufferSource buffer, int light, T entity, float partialTicks,
                             ItemStack stack, ISlashBladeState state, MmdMotionPlayerGL2 mmp) {
        float yOffset = 1.5F;
        double motionScale = 0.125F;
        double modelScaleBase = 0.0078125F;

        ComboState combo = getComboState(state);
        double time = getComboTime(combo, entity, state, partialTicks);

        if (combo == ComboStateRegistry.NONE.get()) {
            combo = getComboRootState(state);
        }

        MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(Objects.requireNonNull(combo).getMotionLoc());
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

        try (MSAutoCloser ignored = MSAutoCloser.pushMatrix(poseStack)) {
            setUserPose(poseStack, entity, partialTicks);
            poseStack.translate(0.0F, yOffset, 0.0F);
            poseStack.scale((float) motionScale, (float) motionScale, (float) motionScale);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            ResourceLocation textureLocation = state.getTexture().orElse(DefaultResources.resourceDefaultTexture);
            WavefrontObject obj = BladeModelManager.getInstance().getModel(state.getModel().orElse(DefaultResources.resourceDefaultModel));
            renderBladePart(poseStack, buffer, light, stack, obj, mmp, "hardpointA", modelScaleBase, motionScale, state, textureLocation);
            renderSheathPart(poseStack, buffer, light, stack, obj, mmp, "hardpointB", modelScaleBase, motionScale, state, textureLocation, entity, partialTicks);
        }
    }

    private ComboState getComboState(ISlashBladeState state) {
        ComboState combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(state.getComboSeq()) != null
                ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(state.getComboSeq())
                : ComboStateRegistry.NONE.get();
        return combo;
    }

    private ComboState getComboRootState(ISlashBladeState state) {
        return ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(state.getComboRoot()) != null
                ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(state.getComboRoot())
                : ComboStateRegistry.STANDBY.get();
    }

    private double getComboTime(ComboState combo, T entity, ISlashBladeState state, float partialTicks) {
        double time = 0.0;
        if (combo != null) {
            time = TimeValueHelper.getMSecFromTicks((float) Math.max(0L, entity.level().getGameTime() - state.getLastActionTime()) + partialTicks);
            while (combo != ComboStateRegistry.NONE.get() && (double) Objects.requireNonNull(combo).getTimeoutMS() < time) {
                time -= combo.getTimeoutMS();
                combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(combo.getNextOfTimeout(entity)) != null
                        ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(combo.getNextOfTimeout(entity))
                        : ComboStateRegistry.NONE.get();
            }
        }
        return time;
    }

    private void renderBladePart(PoseStack poseStack, MultiBufferSource buffer, int light, ItemStack stack, WavefrontObject obj,
                                 MmdMotionPlayerGL2 mmp, String boneName, double modelScaleBase, double motionScale,
                                 ISlashBladeState state, ResourceLocation textureLocation) {
        try (MSAutoCloser ignored = MSAutoCloser.pushMatrix(poseStack)) {
            int idx = mmp.getBoneIndexByName(boneName);
            if (0 <= idx) {
                float[] buf = new float[16];
                mmp._skinning_mat[idx].getValue(buf);
                Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
                poseStack.scale(-1.0F, 1.0F, 1.0F);
                PoseStack.Pose entry = poseStack.last();
                entry.pose().mul(mat);
                poseStack.scale(-1.0F, 1.0F, 1.0F);
            }
            float modelScale = (float) (modelScaleBase * ((double) 1.0F / motionScale));
            poseStack.scale(modelScale, modelScale, modelScale);
            String part = state.isBroken() ? "blade_damaged" : "blade";
            BladeRenderState.renderOverrided(stack, obj, part, textureLocation, poseStack, buffer, light);
            BladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", textureLocation, poseStack, buffer, light);
        }
    }

    private void renderSheathPart(PoseStack poseStack, MultiBufferSource buffer, int light, ItemStack stack, WavefrontObject obj,
                                  MmdMotionPlayerGL2 mmp, String boneName, double modelScaleBase, double motionScale,
                                  ISlashBladeState state, ResourceLocation textureLocation, T entity, float partialTicks) {
        try (MSAutoCloser ignored = MSAutoCloser.pushMatrix(poseStack)) {
            int idx = mmp.getBoneIndexByName(boneName);
            if (0 <= idx) {
                float[] buf = new float[16];
                mmp._skinning_mat[idx].getValue(buf);
                Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
                poseStack.scale(-1.0F, 1.0F, 1.0F);
                PoseStack.Pose entry = poseStack.last();
                entry.pose().mul(mat);
                poseStack.scale(-1.0F, 1.0F, 1.0F);
            }
            float modelScale = (float) (modelScaleBase * ((double) 1.0F / motionScale));
            poseStack.scale(modelScale, modelScale, modelScale);
            BladeRenderState.renderOverrided(stack, obj, "sheath", textureLocation, poseStack, buffer, light);
            BladeRenderState.renderOverridedLuminous(stack, obj, "sheath_luminous", textureLocation, poseStack, buffer, light);
            if (state.isCharged(entity)) {
                float f = (float) entity.tickCount + partialTicks;
                BladeRenderState.renderChargeEffect(stack, f, obj, "effect", CREEPER_ARMOR, poseStack, buffer, light);
            }
        }
    }

    public void setUserPose(PoseStack poseStack, T entity, float partialTicks) {
        ILocationModel model = getLocationModel(entity);
        RenderUtils.prepMatrixForLocator(poseStack, model.leftHandBones());
        poseStack.translate(-0.35F, -0.8F, -0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(15.0F));
        UserPoseOverrider.invertRot(poseStack, entity, partialTicks);
    }
}
