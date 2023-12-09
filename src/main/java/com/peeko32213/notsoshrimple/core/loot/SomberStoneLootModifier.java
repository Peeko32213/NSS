package com.peeko32213.notsoshrimple.core.loot;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peeko32213.notsoshrimple.core.registry.NSSItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SomberStoneLootModifier extends LootModifier {

    public SomberStoneLootModifier(LootItemCondition[] condition) {
        super(condition);
    }
    public double SomberProbability = 0.10;
    //probability to spawn in decimals

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() > 1 - SomberProbability) {
            generatedLoot.add(new ItemStack(NSSItems.SOMBER_STONE.get(), context.getRandom().nextInt(1, 5)));
        }
        return generatedLoot;
    }

    public static final Supplier<Codec<SomberStoneLootModifier>> CODEC = () -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, SomberStoneLootModifier::new));

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

}