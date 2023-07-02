package com.peeko32213.notsoshrimple.core.event;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.entity.utl.PisslikeHitboxes;
import com.peeko32213.notsoshrimple.common.item.ItemClawblade;
import com.peeko32213.notsoshrimple.core.config.BiomeConfig;
import com.peeko32213.notsoshrimple.core.config.NotSoShrimpleConfig;
import com.peeko32213.notsoshrimple.core.config.util.SpawnBiomeData;
import com.peeko32213.notsoshrimple.core.recipes.SmithingStoneRecipe;
import com.peeko32213.notsoshrimple.core.registry.NSSEntities;
import com.peeko32213.notsoshrimple.core.registry.NSSItems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

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
        System.out.println("damage " + event.getAmount());

        if (source.getEntity() instanceof Player) {
            Player player = (Player) (source.getEntity());
            Item weapon = player.getMainHandItem().getItem();
            System.out.println("item mods " + player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND));
            System.out.println("item " + player.getMainHandItem());

            if (weapon instanceof ItemClawblade && target instanceof Mob) {
                Mob victim = ((Mob) target);
                ItemClawblade shrimplifier = (ItemClawblade) weapon;
                //System.out.println("passedbusta");

                if (victim.getMobType() == MobType.ARTHROPOD) {
                    //System.out.println("dmg " + (event.getAmount() + (event.getAmount()*shrimplifier.arthropodBonus)));
                    event.setAmount((float) (event.getAmount() + (event.getAmount()*shrimplifier.arthropodBonus)));
                    //System.out.println("passedpod");
                    //System.out.println(((Mob) target).getHealth());
                }
            }
        }

    }

    @SubscribeEvent
    public static void onLeftClickBlock (final PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();

        if (player.getMainHandItem().getItem() instanceof ItemClawblade &&
                player.fallDistance > 0 &&
                player.isCrouching() &&
                !player.level.isClientSide() &&
                !player.getCooldowns().isOnCooldown(NSSItems.GREAT_PRAWN_CLAWBLADE.get())) {
            //you have to be falling and crouching and have the weapon in your main hand to be slamming

            ItemClawblade knife = (ItemClawblade) player.getMainHandItem().getItem();
            ItemStack stack = event.getItemStack();

            PisslikeHitboxes.PivotedPolyHitCheck(player, knife.slamOffset, 4.0, 2, 4.0, (ServerLevel) player.getLevel(), 5, DamageSource.playerAttack(player), 1.5f, false);
            //creates aoe
            stack.hurtAndBreak(50, player, (p_29910_) -> {
                p_29910_.broadcastBreakEvent(player.getUsedItemHand());
            });
            //damages the sword by 50
            player.getCooldowns().addCooldown(NSSItems.GREAT_PRAWN_CLAWBLADE.get(), 20*3);
            //cooldown of 3 seconds

        }
    }

    /*@SubscribeEvent
    public static void addAttributes(ItemAttributeModifierEvent event){
        ItemStack itemStack = event.getItemStack();
        if(!itemStack.hasTag()) return;
        if(itemStack.getTag().contains("smithing_stone_dmg_mod")){
            int addDamageTo = event.getItemStack().getTag().getInt("smithing_stone_dmg_mod");
            AttributeModifier damageModifier = new AttributeModifier(UUID.randomUUID(), "SSDmgMod", addDamageTo, AttributeModifier.Operation.ADDITION);
            event.addModifier(Attributes.ATTACK_DAMAGE, damageModifier);
        }

    }*/

}


