package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NSSParticles {
    public static final DeferredRegister<ParticleType<?>> SHRIMPARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, NotSoShrimple.MODID);

    public static final RegistryObject<SimpleParticleType> FOAM_STANDARD = SHRIMPARTICLES.register("foam_standard",() -> new SimpleParticleType(true));
    public static void register(IEventBus eventbus){
        SHRIMPARTICLES.register(eventbus);
    }
}
