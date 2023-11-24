package com.peeko32213.notsoshrimple.core.config;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.core.config.util.SpawnBiomeConfig;
import com.peeko32213.notsoshrimple.core.config.util.SpawnBiomeData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BiomeConfig {
    public static Pair<String, SpawnBiomeData> crayfish = Pair.of("notsoshrimple:crayfish_spawns", DefaultBiomes.SWAMP);
    public static Pair<String, SpawnBiomeData> maneater = Pair.of("notsoshrimple:maneater_spawns", DefaultBiomes.COLDOCEAN);



    private static boolean init = false;
    private static Map<String, SpawnBiomeData> biomeConfigValues = new HashMap<>();

    public static void init() {
        try {
            for (Field f : BiomeConfig.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if(obj instanceof Pair){
                    String id = (String)((Pair) obj).getLeft();
                    SpawnBiomeData data = (SpawnBiomeData)((Pair) obj).getRight();
                    biomeConfigValues.put(id, SpawnBiomeConfig.create(new ResourceLocation(id), data));
                }
            }
        }catch (Exception e){
            NotSoShrimple.LOGGER.warn("Encountered error building notsoshrimple biome config .json files");
            e.printStackTrace();
        }
        init = true;
    }

    public static boolean test(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome, ResourceLocation name){
        if(!init){
            return false;
        }
        return biomeConfigValues.get(entry.getKey()).matches(biome, name);
    }

    public static boolean test(Pair<String, SpawnBiomeData> spawns, Holder<Biome> biome) {
        return test(spawns, biome, ForgeRegistries.BIOMES.getKey(biome.value()));
    }

}
