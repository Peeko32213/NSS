package com.peeko32213.notsoshrimple;

import com.mojang.logging.LogUtils;
import com.peeko32213.notsoshrimple.common.entity.EntityCrayfish;
import com.peeko32213.notsoshrimple.common.entity.EntityManeaterShell;
import com.peeko32213.notsoshrimple.core.config.BiomeConfig;
import com.peeko32213.notsoshrimple.core.config.ConfigHolder;
import com.peeko32213.notsoshrimple.core.config.NotSoShrimpleConfig;
import com.peeko32213.notsoshrimple.core.recipes.SmithingStoneRecipe;
import com.peeko32213.notsoshrimple.core.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NotSoShrimple.MODID)
public class NotSoShrimple

{

    public static final String MODID = "notsoshrimple";
    public static final List<Runnable> CALLBACKS = new ArrayList<>();
    public static final Logger LOGGER = LogManager.getLogger();

    public NotSoShrimple() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        modEventBus.addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);
        NSSSounds.DEF_REG.register(modEventBus);
        NSSItems.ITEMS.register(modEventBus);
        NSSEntities.ENTITIES.register(modEventBus);
        NSSParticles.SHRIMPARTICLES.register(modEventBus);
        NSSRecipes.SERIALIZERS.register(modEventBus);
        NSSAttributes.ATTRIBUTEREGISTER.register(modEventBus);
        NSSWorldRegistry.STRUCTURE_MODIFIERS.register(modEventBus);
        NSSWorldRegistry.StructureModifierReg.register();
        NSSLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        //MinecraftForge.EVENT_BUS.addListener(SmithingStoneRecipe::addAttributes);

        eventBus.register(this);

        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC, "notsoshrimple.toml");

    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            NotSoShrimpleConfig.bake(config);
        }
        //BiomeConfig.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(NSSEntities.MANEATER.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityManeaterShell::canSpawn);
            SpawnPlacements.register(NSSEntities.CRAYFISH.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrayfish::canSpawn);
            //crayfish confirmed to work
            //shells run on a separate system
        });
    }

    public static final CreativeModeTab SHRIMPLE = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return NSSItems.CLAW.get().getDefaultInstance();
        }
    };

}
