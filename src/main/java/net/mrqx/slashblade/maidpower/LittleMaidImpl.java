package net.mrqx.slashblade.maidpower;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.GeoLayerRenderer;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.slashblade.maidpower.client.renderer.GeoLayerMaidBladeRenderer;
import net.mrqx.slashblade.maidpower.client.renderer.LayerMaidBladeRenderer;
import net.mrqx.slashblade.maidpower.init.MaidPowerItems;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;

@SuppressWarnings("unused")
@LittleMaidExtension
public class LittleMaidImpl implements ILittleMaid {
    public static final SlashBladeMaidBauble.UnawakenedSoul UNAWAKENED_SOUL_BAUBLE = new SlashBladeMaidBauble.UnawakenedSoul();
    public static final SlashBladeMaidBauble.ComboB COMBO_B_BAUBLE = new SlashBladeMaidBauble.ComboB();
    public static final SlashBladeMaidBauble.ComboC COMBO_C_BAUBLE = new SlashBladeMaidBauble.ComboC();
    public static final SlashBladeMaidBauble.RapidSlash RAPID_SLASH_BAUBLE = new SlashBladeMaidBauble.RapidSlash();
    public static final SlashBladeMaidBauble.AirCombo AIR_COMBO_BAUBLE = new SlashBladeMaidBauble.AirCombo();
    public static final SlashBladeMaidBauble.MirageBlade MIRAGE_BLADE_BAUBLE = new SlashBladeMaidBauble.MirageBlade();
    public static final SlashBladeMaidBauble.Trick TRICK_BAUBLE = new SlashBladeMaidBauble.Trick();
    public static final SlashBladeMaidBauble.Power POWER_BAUBLE = new SlashBladeMaidBauble.Power();
    public static final SlashBladeMaidBauble.JudgementCut JUDGEMENT_CUT_BAUBLE = new SlashBladeMaidBauble.JudgementCut();
    public static final SlashBladeMaidBauble.JustJudgementCut JUST_JUDGEMENT_CUT_BAUBLE = new SlashBladeMaidBauble.JustJudgementCut();
    public static final SlashBladeMaidBauble.VoidSlash VOID_SLASH_BAUBLE = new SlashBladeMaidBauble.VoidSlash();
    public static final SlashBladeMaidBauble.Guard GUARD_BAUBLE = new SlashBladeMaidBauble.Guard();
    public static final SlashBladeMaidBauble.Health HEALTH_BAUBLE = new SlashBladeMaidBauble.Health();
    public static final SlashBladeMaidBauble.Exp EXP_BAUBLE = new SlashBladeMaidBauble.Exp();
    public static final SlashBladeMaidBauble.TruePower TRUE_POWER_BAUBLE = new SlashBladeMaidBauble.TruePower();

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new TaskSlashBlade());
    }

    @Override
    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(MaidPowerItems.UNAWAKENED_SOUL.get(), UNAWAKENED_SOUL_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_COMBO_B.get(), COMBO_B_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_COMBO_C.get(), COMBO_C_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_RAPID_SLASH.get(), RAPID_SLASH_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_AIR_COMBO.get(), AIR_COMBO_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_MIRAGE_BLADE.get(), MIRAGE_BLADE_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_TRICK.get(), TRICK_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_POWER.get(), POWER_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_JUDGEMENT_CUT.get(), JUDGEMENT_CUT_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_JUST_JUDGEMENT_CUT.get(), JUST_JUDGEMENT_CUT_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_VOID_SLASH.get(), VOID_SLASH_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_GUARD.get(), GUARD_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_HEALTH.get(), HEALTH_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_EXP.get(), EXP_BAUBLE);
        manager.bind(MaidPowerItems.SOUL_OF_TRUE_POWER.get(), TRUE_POWER_BAUBLE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addAdditionGeckoMaidLayer(GeckoEntityMaidRenderer<? extends Mob> renderer, EntityRendererProvider.Context context) {
        renderer.addLayer((GeoLayerRenderer) new GeoLayerMaidBladeRenderer<>(renderer));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addAdditionMaidLayer(EntityMaidRenderer renderer, EntityRendererProvider.Context context) {
        renderer.addLayer(new LayerMaidBladeRenderer<>(renderer));
    }
}
