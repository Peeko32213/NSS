package com.peeko32213.notsoshrimple.client.event;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.client.render.*;
import com.peeko32213.notsoshrimple.common.particles.FoamStandardParticle;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {


    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
    }


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NSSEntities.CRAYFISH.get(), CrayfishRenderer::new);
        event.registerEntityRenderer(NSSEntities.MANEATER.get(), ManeaterRenderer::new);
        event.registerEntityRenderer(NSSEntities.ICEWATER.get(), IceWaterRenderer::new);
        event.registerEntityRenderer(NSSEntities.BLOODWATER.get(), BloodWaterRenderer::new);
        event.registerEntityRenderer(NSSEntities.TOXICWATER.get(), ToxicWaterRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories (final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(NSSParticles.FOAM_STANDARD.get(), FoamStandardParticle.Provider::new);
    }
}