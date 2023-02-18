package com.peeko32213.notsoshrimple.client.event;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.client.render.CrayfishRenderer;
import com.peeko32213.notsoshrimple.client.render.ManeaterRenderer;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {


    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {

    }


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NSSEntities.CRAYFISH.get(), CrayfishRenderer::new);
        event.registerEntityRenderer(NSSEntities.MANEATER.get(), ManeaterRenderer::new);

    }
}