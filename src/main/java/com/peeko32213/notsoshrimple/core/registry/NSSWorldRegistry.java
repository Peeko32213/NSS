package com.peeko32213.notsoshrimple.core.registry;

import com.mojang.serialization.Codec;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.utl.NSSStructureSpawnsModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NSSWorldRegistry {
    //registers modifications to structure spawn pools to our bus, not sure why it can't be done in NotSoShrimple.Java entirely... have to try it out
    public static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, NotSoShrimple.MODID);

    public static final class StructureModifierReg {

        public static void register() {
            STRUCTURE_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Codec<NSSStructureSpawnsModifier>> ADD_SPAWNS_MODIFIER = STRUCTURE_MODIFIERS.register("add_spawn", () -> NSSStructureSpawnsModifier.CODEC);
        //referred to in json to modify a structure with data therein
    }
}