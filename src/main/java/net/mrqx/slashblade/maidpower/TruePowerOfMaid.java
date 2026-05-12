package net.mrqx.slashblade.maidpower;

import com.google.common.base.CaseFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidClientConfig;
import net.mrqx.slashblade.maidpower.config.TruePowerOfMaidCommonConfig;
import net.mrqx.slashblade.maidpower.entity.EntityUnlimitedBladeWorks;
import net.mrqx.slashblade.maidpower.init.MaidPowerCreativeTab;
import net.mrqx.slashblade.maidpower.init.MaidPowerItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(TruePowerOfMaid.MODID)
public class TruePowerOfMaid {
    public static final String MODID = "true_power_of_maid";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public TruePowerOfMaid(IEventBus modEventBus, ModContainer container) {
        MaidPowerItems.ITEMS.register(modEventBus);
        MaidPowerItems.DATA_COMPONENTS.register(modEventBus);
        MaidPowerCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        
        container.registerConfig(ModConfig.Type.COMMON, TruePowerOfMaidCommonConfig.COMMON_CONFIG);
        container.registerConfig(ModConfig.Type.CLIENT, TruePowerOfMaidClientConfig.CLIENT_CONFIG);
    }
    
    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    
    
    @EventBusSubscriber
    public static class RegistryEvents {
        public static final ResourceLocation ENTITY_UNLIMITED_BLADE_WORKS_RESOURCE_LOCATION = MrqxSlashBladeCore.prefix(classToString(EntityUnlimitedBladeWorks.class));
        @SuppressWarnings("NotNullFieldNotInitialized")
        public static EntityType<EntityUnlimitedBladeWorks> UnlimitedBladeWorks;
        
        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(Registries.ENTITY_TYPE, (entityTypeRegisterHelper) -> {
                UnlimitedBladeWorks = EntityType.Builder.of((EntityUnlimitedBladeWorks::new), MobCategory.MISC)
                    .sized(0, 0)
                    .setTrackingRange(4)
                    .setUpdateInterval(20)
                    .build(ENTITY_UNLIMITED_BLADE_WORKS_RESOURCE_LOCATION.toString());
                entityTypeRegisterHelper.register(ENTITY_UNLIMITED_BLADE_WORKS_RESOURCE_LOCATION, UnlimitedBladeWorks);
            });
        }
        
        @SubscribeEvent
        public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
            event.put(UnlimitedBladeWorks, EntityUnlimitedBladeWorks.createAttributes().build());
        }
        
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(UnlimitedBladeWorks, NoopRenderer::new);
        }
        
        @SuppressWarnings("SameParameterValue")
        private static String classToString(Class<? extends Entity> entityClass) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", "");
        }
    }
}
