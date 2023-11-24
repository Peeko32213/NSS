package com.peeko32213.notsoshrimple.common.entity.utl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peeko32213.notsoshrimple.core.registry.NSSWorldRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;

public class NSSStructureSpawnsModifier implements StructureModifier {

    public static final Codec<NSSStructureSpawnsModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            RegistryCodecs.homogeneousList(Registry.STRUCTURE_REGISTRY, Structure.DIRECT_CODEC).fieldOf("structures").forGetter(NSSStructureSpawnsModifier::getStructures),
            MobCategory.CODEC.fieldOf("category").forGetter(NSSStructureSpawnsModifier::getCategory),
            MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawn").forGetter(NSSStructureSpawnsModifier::getSpawn)
    ).apply(builder, NSSStructureSpawnsModifier::new));
    //no clue what this does. forms a codec of modified structures?

    protected final HolderSet<Structure> structures;
    protected final MobCategory category;
    protected final MobSpawnSettings.SpawnerData spawn;

    public NSSStructureSpawnsModifier(HolderSet<Structure> structures, MobCategory category, MobSpawnSettings.SpawnerData spawn) {
        this.structures = structures;
        this.category = category;
        this.spawn = spawn;
    }

    public HolderSet<Structure> getStructures() {
        return structures;
    }

    public MobCategory getCategory() {
        return category;
    }

    public MobSpawnSettings.SpawnerData getSpawn() {
        return spawn;
    }

    @Override
    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase == Phase.ADD && this.structures.contains(structure)) {
            builder.getStructureSettings()
                    .getOrAddSpawnOverrides(category)
                    .addSpawn(spawn);
            //actively modifies a structure's settings
        }
    }

    @Override
    public Codec<? extends StructureModifier> codec() {
        return NSSWorldRegistry.StructureModifierReg.ADD_SPAWNS_MODIFIER.get();
    }

    // credit to this mod for the code https://github.com/skyjay1/GreekFantasy/tree/master-1.19
    // was gonna plagiarize alex but he doesn't run json spawns
}