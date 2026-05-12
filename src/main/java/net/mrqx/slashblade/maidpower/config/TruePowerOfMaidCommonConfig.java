package net.mrqx.slashblade.maidpower.config;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.util.concurrent.AtomicDouble;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class TruePowerOfMaidCommonConfig {
    public static final ModConfigSpec COMMON_CONFIG;
    
    public static final ModConfigSpec.ConfigValue<List<? extends List<?>>> UNAWAKENED_SOUL_LIST;
    public static final ModConfigSpec.IntValue TRUE_POWER_MAX_SOUL_COUNT;
    public static final RangeMap<Double, String> UNAWAKENED_SOUL_RANGE_MAP = TreeRangeMap.create();
    public static double unawakenedSoulTotalRange;
    
    static {
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();
        
        commonBuilder.comment("TLM: True POWER common settings");
        
        UNAWAKENED_SOUL_LIST = commonBuilder
            .comment("Set the weight for Unawakened Soul finished products.")
            .defineListAllowEmpty(List.of("unawakened_soul_weight_list"),
                List.of(
                    List.of("true_power_of_maid:soul_of_combo_b", 1.0),
                    List.of("true_power_of_maid:soul_of_combo_c", 1.0),
                    List.of("true_power_of_maid:soul_of_rapid_slash", 1.0),
                    List.of("true_power_of_maid:soul_of_air_combo", 1.0),
                    List.of("true_power_of_maid:soul_of_mirage_blade", 1.0),
                    List.of("true_power_of_maid:soul_of_trick", 1.0),
                    List.of("true_power_of_maid:soul_of_judgement_cut", 1.0),
                    List.of("true_power_of_maid:soul_of_just_judgement_cut", 1.0),
                    List.of("true_power_of_maid:soul_of_void_slash", 1.0),
                    List.of("true_power_of_maid:soul_of_guard", 1.0),
                    List.of("true_power_of_maid:soul_of_health", 1.0),
                    List.of("true_power_of_maid:soul_of_exp", 1.0),
                    List.of("true_power_of_maid:soul_of_power", 0.5),
                    List.of("true_power_of_maid:soul_of_true_power", 0.001)
                ), List::of,
                it -> it instanceof List<?> list && list.size() == 2
                    && list.getFirst() instanceof String
                    && list.get(1) instanceof Double
                    && (Double) (list.get(1)) >= 0.0);
        
        TRUE_POWER_MAX_SOUL_COUNT = commonBuilder
            .comment("Set max soul count of. (default: 10)")
            .defineInRange("true_power_max_soul_count", 10, 0, Integer.MAX_VALUE);
        
        COMMON_CONFIG = commonBuilder.build();
    }
    
    @SubscribeEvent
    public static void onServerStartingEvent(ServerStartingEvent event) {
        UNAWAKENED_SOUL_RANGE_MAP.clear();
        Map<String, Double> unawakenedSoulMap = new HashMap<>(16);
        UNAWAKENED_SOUL_LIST.get().forEach(chance -> unawakenedSoulMap.put((String) (chance.getFirst()), (Double) (chance.get(1))));
        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(unawakenedSoulMap.entrySet());
        sortedList.sort(Map.Entry.comparingByValue());
        AtomicDouble total = new AtomicDouble(0);
        sortedList.forEach(entry -> UNAWAKENED_SOUL_RANGE_MAP.put(Range.closedOpen(total.getAndAdd(entry.getValue()), total.get() + entry.getValue()), entry.getKey()));
        unawakenedSoulTotalRange = total.get();
    }
}
