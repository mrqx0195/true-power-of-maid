package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.mrqx.slashblade.maidpower.TruePowerOfMaid;
import net.mrqx.slashblade.maidpower.mixin.AccessorLayerMainBlade;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class LayerMaidBladeRenderer<T extends Mob, M extends EntityModel<T>> extends LayerMainBlade<T, M> {
    public LayerMaidBladeRenderer(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int lightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderOffhandItem(matrixStack, bufferIn, lightIn, entity);
        float yOffset = 1.5F;
        double motionScale = 0.125F;
        double modelScaleBase = 0.0078125F;
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty()) {
            LazyOptional<ISlashBladeState> state = stack.getCapability(CapabilitySlashBlade.BLADESTATE);
            AccessorLayerMainBlade accessorLayerMainBlade = (AccessorLayerMainBlade) this;
            state.ifPresent((s) -> accessorLayerMainBlade.getMotionPlayer().ifPresent((mmp) -> {
                ComboState combo = ComboStateRegistry.REGISTRY.get().getValue(s.getComboSeq()) != null ? ComboStateRegistry.REGISTRY.get().getValue(s.getComboSeq()) : ComboStateRegistry.NONE.get();

                double time;
                for (time = TimeValueHelper.getMSecFromTicks((float) Math.max(0L, entity.level().getGameTime() - s.getLastActionTime()) + partialTicks); combo != ComboStateRegistry.NONE.get() && (double) (combo != null ? combo.getTimeoutMS() : 0) < time; combo = ComboStateRegistry.REGISTRY.get().getValue(combo != null ? combo.getNextOfTimeout(entity) : null) != null ? ComboStateRegistry.REGISTRY.get().getValue(combo != null ? combo.getNextOfTimeout(entity) : null) : ComboStateRegistry.NONE.get()) {
                    time -= combo != null ? combo.getTimeoutMS() : 0;
                }

                if (combo == ComboStateRegistry.NONE.get()) {
                    combo = ComboStateRegistry.REGISTRY.get().getValue(s.getComboRoot()) != null ? ComboStateRegistry.REGISTRY.get().getValue(s.getComboRoot()) : ComboStateRegistry.STANDBY.get();
                }

                MmdVmdMotionMc motion;
                if (combo != null) {
                    motion = BladeMotionManager.getInstance().getMotion(combo.getMotionLoc());
                    double maxSeconds = 0.0F;

                    try {
                        mmp.setVmd(motion);
                        maxSeconds = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
                    } catch (Exception e) {
                        TruePowerOfMaid.LOGGER.error("Error while rendering maid`s SlashBlade:", e);
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
                        TruePowerOfMaid.LOGGER.error("Error while rendering maid`s SlashBlade:", e);
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

                        try (MSAutoCloser ignored1 = MSAutoCloser.pushMatrix(matrixStack)) {
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

    @Override
    public void renderOffhandItem(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity) {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setUserPose(PoseStack matrixStack, T entity, float partialTicks) {
        M model = getParentModel();
        if (model instanceof BedrockModel bedrockModel && bedrockModel.hasWaistPositioningModel(HumanoidArm.LEFT)) {
            bedrockModel.translateToPositioningWaist(HumanoidArm.LEFT, matrixStack);
        } else if (model instanceof ILocationModel iLocationModel) {
            RenderUtils.prepMatrixForLocator(matrixStack, iLocationModel.leftHandBones());
        } else {
            matrixStack.translate(0.25F, 0.85, 0.0F);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-20.0F));
        }
        matrixStack.translate(-0.3F, -0.2F, -0.5F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(15.0F));
        super.setUserPose(matrixStack, entity, partialTicks);
    }

}
