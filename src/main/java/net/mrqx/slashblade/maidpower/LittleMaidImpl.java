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
import net.mrqx.slashblade.maidpower.init.ModItems;
import net.mrqx.slashblade.maidpower.item.SlashBladeMaidBauble;
import net.mrqx.slashblade.maidpower.task.TaskSlashBlade;

@SuppressWarnings("unused")
@LittleMaidExtension
public class LittleMaidImpl implements ILittleMaid {
    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new TaskSlashBlade());
    }

    @Override
    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(ModItems.SOUL_OF_COMBO_B.get(), new SlashBladeMaidBauble.ComboB());
        manager.bind(ModItems.SOUL_OF_COMBO_C.get(), new SlashBladeMaidBauble.ComboC());
        manager.bind(ModItems.SOUL_OF_RAPID_SLASH.get(), new SlashBladeMaidBauble.RapidSlash());
        manager.bind(ModItems.SOUL_OF_AIR_COMBO.get(), new SlashBladeMaidBauble.AirCombo());
        manager.bind(ModItems.SOUL_OF_MIRAGE_BLADE.get(), new SlashBladeMaidBauble.MirageBlade());
        manager.bind(ModItems.SOUL_OF_TRICK.get(), new SlashBladeMaidBauble.Trick());
        manager.bind(ModItems.SOUL_OF_POWER.get(), new SlashBladeMaidBauble.Power());
        manager.bind(ModItems.SOUL_OF_JUDGEMENT_CUT.get(), new SlashBladeMaidBauble.JudgementCut());
        manager.bind(ModItems.SOUL_OF_JUST_JUDGEMENT_CUT.get(), new SlashBladeMaidBauble.JustJudgementCut());
        manager.bind(ModItems.SOUL_OF_VOID_SLASH.get(), new SlashBladeMaidBauble.VoidSlash());
        manager.bind(ModItems.SOUL_OF_GUARD.get(), new SlashBladeMaidBauble.Guard());
        manager.bind(ModItems.SOUL_OF_HEALTH.get(), new SlashBladeMaidBauble.Health());
        manager.bind(ModItems.SOUL_OF_EXP.get(), new SlashBladeMaidBauble.Exp());
        manager.bind(ModItems.SOUL_OF_TRUE_POWER.get(), new SlashBladeMaidBauble.TruePower());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("all")
    public void addAdditionGeckoMaidLayer(GeckoEntityMaidRenderer<? extends Mob> renderer, EntityRendererProvider.Context context) {
        renderer.addLayer((GeoLayerRenderer) new GeoLayerMaidBladeRenderer<>(renderer));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("all")
    public void addAdditionMaidLayer(EntityMaidRenderer renderer, EntityRendererProvider.Context context) {
        renderer.addLayer(new LayerMaidBladeRenderer(renderer));
    }
}
