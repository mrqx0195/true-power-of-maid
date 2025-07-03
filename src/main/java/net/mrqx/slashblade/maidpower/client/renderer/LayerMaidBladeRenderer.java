package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockModel;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.ILocationModel;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;

public class LayerMaidBladeRenderer<T extends EntityMaid, M extends EntityModel<T>> extends LayerMainBlade<T, M> {
    public LayerMaidBladeRenderer(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void renderOffhandItem(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity) {
    }

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
