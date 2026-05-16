package net.mrqx.slashblade.maidpower.data;

import com.github.tartaricacid.touhoulittlemaid.datagen.builder.AltarRecipeBuilder;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import mods.flammpfeil.slashblade.registry.SlashBladeItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.mrqx.slashblade.maidpower.init.MaidPowerItems;

import java.util.concurrent.CompletableFuture;

public class TruePowerOfMaidRecipeProvider extends RecipeProvider {
    public TruePowerOfMaidRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    
    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        AltarRecipeBuilder.shapeless(RecipeCategory.COMBAT, MaidPowerItems.UNAWAKENED_SOUL)
            .power(0.2f)
            .requires(6, SlashBladeItems.PROUDSOUL.get())
            .save(recipeOutput);
        
        AltarRecipeBuilder.shapeless(RecipeCategory.COMBAT, MaidPowerItems.SOUL_OF_POWER)
            .power(1)
            .requires(3, SlashBladeItems.PROUDSOUL_CRYSTAL.get())
            .requires(3, InitItems.POWER_POINT)
            .save(recipeOutput);
        
        AltarRecipeBuilder.shapeless(RecipeCategory.COMBAT, MaidPowerItems.SOUL_OF_TRUE_POWER)
            .power(5)
            .requires(6, MaidPowerItems.SOUL_OF_POWER.get())
            .save(recipeOutput);
    }
}
