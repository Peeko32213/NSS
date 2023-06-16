package com.peeko32213.notsoshrimple.core.event;

import com.google.common.base.Predicates;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.EntityCrayfish;
import com.peeko32213.notsoshrimple.common.entity.mobs.EntityManeaterShell;
import com.peeko32213.notsoshrimple.common.particles.FoamStandardParticle;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSPacketHub;
import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
        event.enqueueWork(NSSPacketHub::init);
    }

    @SubscribeEvent
    public static void registerAttributes (EntityAttributeCreationEvent event) {
        event.put(NSSEntities.CRAYFISH.get(), EntityCrayfish.createAttributes().build());
        event.put(NSSEntities.MANEATER.get(), EntityManeaterShell.createAttributes().build());

    }

    @SubscribeEvent
    public static void registerParticleFactories (final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(NSSParticles.FOAM_STANDARD.get(), FoamStandardParticle.Provider::new);
    }


    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }



}