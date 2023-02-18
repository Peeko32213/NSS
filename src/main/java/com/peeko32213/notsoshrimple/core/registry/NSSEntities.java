package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.EntityCrayfish;
import com.peeko32213.notsoshrimple.common.entity.EntityManeaterShell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = NotSoShrimple.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NSSEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            NotSoShrimple.MODID);

    public static final RegistryObject<EntityType<EntityCrayfish>> CRAYFISH = ENTITIES.register("crayfish",
            () -> EntityType.Builder.of(EntityCrayfish::new, MobCategory.MONSTER).sized(5.0F, 3.5F)
                    .build(new ResourceLocation(NotSoShrimple.MODID, "crayfish").toString()));

    public static final RegistryObject<EntityType<EntityManeaterShell>> MANEATER = ENTITIES.register("maneater",
            () -> EntityType.Builder.of(EntityManeaterShell::new, MobCategory.MONSTER).sized(5.0F, 6.5F)
                    .build(new ResourceLocation(NotSoShrimple.MODID, "maneater").toString()));

    public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason){
        if(reason == MobSpawnType.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

}
