package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.ILocationBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.mrqx.sbr_core.client.layer.LayerSlashEntityBlade;

import javax.annotation.Nullable;
import java.util.List;

public class LayerMaidBladeRenderer<T extends Mob, M extends EntityModel<T>> extends LayerSlashEntityBlade<T, M> {
    public final RenderLayerParent<T, M> parent;
    
    public LayerMaidBladeRenderer(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
        this.parent = entityRendererIn;
    }
    
    @Override
    public void renderOffhandItem(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity) {
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void setUserPose(PoseStack matrixStack, T entity, float partialTicks, @Nullable ISlashBladeState state) {
        if (this.parent instanceof IGeoEntityRenderer geoEntityRenderer) {
            ILocationModel model = geoEntityRenderer.getGeoEntity(entity).getGeoModel();
            List<? extends ILocationBone> locators = model.leftWaistBones();
            if (locators.isEmpty()) {
                locators = model.leftHandBones();
            }
            if (!locators.isEmpty()) {
                RenderUtils.prepMatrixForLocator(matrixStack, locators);
            }
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
            matrixStack.translate(-0.35F, -0.8F, -0.5F);
            matrixStack.mulPose(Axis.YP.rotationDegrees(15.0F));
            
            float comboRot = UserPoseOverrider.getInterpolatedComboRotation(entity, partialTicks);
            if (comboRot != 0f) {
                matrixStack.mulPose(Axis.YP.rotationDegrees(comboRot));
            }
        } else {
            M parentModel = this.getParentModel();
            switch (parentModel) {
                case ILocationModel model -> {
                    List<? extends ILocationBone> locators = model.leftWaistBones();
                    if (locators.isEmpty()) {
                        locators = model.leftHandBones();
                    }
                    if (!locators.isEmpty()) {
                        RenderUtils.prepMatrixForLocator(matrixStack, locators);
                    }
                }
                case BedrockModel bedrockModel when bedrockModel.hasWaistPositioningModel(HumanoidArm.LEFT) ->
                    bedrockModel.translateToPositioningWaist(HumanoidArm.LEFT, matrixStack);
                case BedrockModel bedrockModel when bedrockModel.hasArmPositioningModel(HumanoidArm.LEFT) ->
                    bedrockModel.translateToPositioningHand(HumanoidArm.LEFT, matrixStack);
                default -> {
                    matrixStack.translate(0.25F, 0.85, 0.0F);
                    matrixStack.mulPose(Axis.XP.rotationDegrees(-20.0F));
                }
            }
            matrixStack.translate(-0.3F, -0.2F, -0.5F);
            matrixStack.mulPose(Axis.YP.rotationDegrees(15.0F));
        }
        super.setUserPose(matrixStack, entity, partialTicks, state);
    }
}
