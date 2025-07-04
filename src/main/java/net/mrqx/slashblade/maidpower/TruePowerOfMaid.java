package net.mrqx.slashblade.maidpower;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mrqx.slashblade.maidpower.init.ModCreativeTab;
import net.mrqx.slashblade.maidpower.init.ModItems;
import org.slf4j.Logger;

@Mod(TruePowerOfMaid.MODID)
public class TruePowerOfMaid {
    public static final String MODID = "true_power_of_maid";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TruePowerOfMaid() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MODID, path);
    }
}
