package com.peeko32213.notsoshrimple.core.registry;

import com.mojang.serialization.Codec;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.core.loot.SmithingStoneLootModifier;
import com.peeko32213.notsoshrimple.core.loot.SomberStoneLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NSSLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NotSoShrimple.MODID);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> SMITHING_STONE_MODIFIER = LOOT_MODIFIERS.register("add_smithing_stones", SmithingStoneLootModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> SOMBER_STONE_MODIFIER = LOOT_MODIFIERS.register("add_somber_stones", SomberStoneLootModifier.CODEC);
}
