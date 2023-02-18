package com.peeko32213.notsoshrimple.core.event;

import com.mojang.serialization.Codec;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NSSMobSpawnBiomeModifier implements BiomeModifier {
    private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(new ResourceLocation(NotSoShrimple.MODID, "nss_mob_spawns"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, NotSoShrimple.MODID);

    public NSSMobSpawnBiomeModifier() {
    }

    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == BiomeModifier.Phase.ADD) {
            CommonForgeEvents.addBiomeSpawns(biome, builder);
        }
    }

    public Codec<? extends BiomeModifier> codec() {
        return (Codec)SERIALIZER.get();
    }

    public static Codec<NSSMobSpawnBiomeModifier> makeCodec() {
        return Codec.unit(NSSMobSpawnBiomeModifier::new);
    }
}
