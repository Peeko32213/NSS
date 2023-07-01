package com.peeko32213.notsoshrimple.core.event;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.item.ItemSwampBuster;
import com.peeko32213.notsoshrimple.core.config.BiomeConfig;
import com.peeko32213.notsoshrimple.core.config.NotSoShrimpleConfig;
import com.peeko32213.notsoshrimple.core.config.util.SpawnBiomeData;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSItems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class CommonForgeEvents {

    public static void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {

        if (testBiome(BiomeConfig.crayfish, biome) && NotSoShrimpleConfig.crayfishSpawnWeight > 0) {
            builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(NSSEntities.CRAYFISH.get(), NotSoShrimpleConfig.crayfishSpawnWeight, 1, 2));
        }

    }

    private static ResourceLocation getBiomeName(Holder<Biome> biome) {
        return biome.unwrap().map((resourceKey) -> resourceKey.location(), (noKey) -> null);
    }

    public static boolean testBiome(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome) {
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, biome, getBiomeName(biome));
        } catch (Exception e) {
            NotSoShrimple.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }

    @SubscribeEvent
    public static void onLivingHurtEvent (final LivingHurtEvent event) {
        Entity target = event.getEntity();
        DamageSource source = event.getSource();

        if (source.getEntity() instanceof Player) {
            Player player = (Player) (source.getEntity());
            Item weapon = player.getMainHandItem().getItem();
            //System.out.println("passedwp");

            if (weapon instanceof ItemSwampBuster && target instanceof Mob) {
                Mob victim = ((Mob) target);
                ItemSwampBuster shrimplifier = (ItemSwampBuster) weapon;
                //System.out.println("passedbusta");

                if (victim.getMobType() == MobType.ARTHROPOD) {
                    //System.out.println("dmg " + event.getAmount() + (event.getAmount()*shrimplifier.arthropodBonus));
                    event.setAmount((float) (event.getAmount() + (event.getAmount()*shrimplifier.arthropodBonus)));
                    //System.out.println("passedpod");
                    //ystem.out.println(((Mob) target).getHealth());
                }
            }
        }

    }

}


