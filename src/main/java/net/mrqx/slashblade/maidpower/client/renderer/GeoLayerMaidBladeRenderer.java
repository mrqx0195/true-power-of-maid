package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;

public class GeoLayerMaidBladeRenderer<T extends Mob, R extends GeckoEntityMaidRenderer<T>> extends GeoLayerRenderer<T, R> {
    public final LayerMaidBladeRenderer<T, HumanoidModel<T>> layerMaidBladeRenderer;
    
    public GeoLayerMaidBladeRenderer(R entityRendererIn) {
        super(entityRendererIn);
        this.layerMaidBladeRenderer = new LayerMaidBladeRenderer<>(entityRendererIn);
    }
    
    @Override
    public GeoLayerMaidBladeRenderer<T, R> copy(R renderer) {
        return new GeoLayerMaidBladeRenderer<>(renderer);
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.layerMaidBladeRenderer.render(poseStack, buffer, light, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}
