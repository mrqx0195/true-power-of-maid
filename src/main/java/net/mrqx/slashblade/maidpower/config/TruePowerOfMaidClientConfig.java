package net.mrqx.slashblade.maidpower.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TruePowerOfMaidClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.DoubleValue MAID_RANK_SIZE;
    public static final ForgeConfigSpec.IntValue MAID_RANK_X;
    public static final ForgeConfigSpec.IntValue MAID_RANK_Y;

    static {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

        clientBuilder.comment("TLM: True POWER client settings");

        MAID_RANK_SIZE = clientBuilder
                .comment("Set the size of maid's ranking display. (default: 0.018)")
                .defineInRange("maid_rank_size", 0.018, 0.0, Double.MAX_VALUE);

        MAID_RANK_X = clientBuilder
                .comment("Set the x pos of maid's ranking display. (default: 8)")
                .defineInRange("maid_rank_x", 8, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MAID_RANK_Y = clientBuilder
                .comment("Set the x pos of maid's ranking display. (default: -28)")
                .defineInRange("maid_rank_y", -28, Integer.MIN_VALUE, Integer.MAX_VALUE);

        CLIENT_CONFIG = clientBuilder.build();
    }
}
