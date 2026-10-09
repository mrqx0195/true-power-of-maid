package net.mrqx.slashblade.maidpower.client.renderer;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Mob;

public class GeoLayerMaidBladeRenderer<T extends Mob, M extends EntityModel<T>, R extends IGeoEntityRenderer<T> & RenderLayerParent<T, M>> extends GeoLayerRenderer<T, R> {
    public final LayerMaidBladeRenderer<T, M> layerMaidBladeRenderer;
    
    public GeoLayerMaidBladeRenderer(R entityRendererIn) {
        super(entityRendererIn);
        this.layerMaidBladeRenderer = new LayerMaidBladeRenderer<>(entityRendererIn);
    }
    
    @Override
    public GeoLayerMaidBladeRenderer<T, M, R> copy(R renderer) {
        return new GeoLayerMaidBladeRenderer<>(renderer);
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.layerMaidBladeRenderer.render(poseStack, buffer, light, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}
