package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.core.recipes.SmithingStoneRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NSSRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NotSoShrimple.MODID);

    public static final RegistryObject<RecipeSerializer<SmithingStoneRecipe>> SOMBER_STONE_RECIPE =
            SERIALIZERS.register("somber_stone_recipe", () -> new SmithingStoneRecipe.Serializer());

    public static final RegistryObject<RecipeSerializer<SmithingStoneRecipe>> SMITHING_STONE_RECIPE =
            SERIALIZERS.register("smithing_stone_recipe", () -> new SmithingStoneRecipe.Serializer());
}
