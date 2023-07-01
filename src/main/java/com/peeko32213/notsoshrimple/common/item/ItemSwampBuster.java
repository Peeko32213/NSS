package com.peeko32213.notsoshrimple.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.client.render.SwampBusterRenderer;
import com.peeko32213.notsoshrimple.core.event.CommonForgeEvents;
import com.peeko32213.notsoshrimple.core.registry.NSSItemTiers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ItemSwampBuster extends SwordItem implements IAnimatable{
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public double arthropodBonus = 1.5;

    public ItemSwampBuster(Tier tier, int attackDamage, float attackSpeed) {
        super(tier, attackDamage, attackSpeed, new Properties()
                .stacksTo(1)
                .defaultDurability(tier.getUses())
                .tab(NotSoShrimple.SHRIMPLE)
        );
        //TODO: Clawblade cannot sweep. Clawblade deals bonus damage to arthropods. Clawblade is slower than an axe. Fully charged clawblade attacks on a block sends a shockwave around that block.
        //TODO: Clawblade is repaired with more Calloused Claws.

    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    public Rarity getRarity(ItemStack pStack) {
        return Rarity.EPIC;
        //epic rarity on default
    }

    @NotNull
    public AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target)
    {
        return target.getBoundingBox().inflate(0.0D, 0.0D, 0.0D);
        //The weapon cannot sweep.
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity){

        if (entity instanceof Mob) {
            Mob victim = ((Mob) entity);
            if (victim.getMobType() == MobType.ARTHROPOD) {
                LivingHurtEvent event = new LivingHurtEvent(victim, DamageSource.playerAttack(player), (float) (this.getDamage()*this.arthropodBonus) - this.getDamage());
                //System.out.println(event.getResult());
                //System.out.println("thro pod");
            }
        }
        super.onLeftClickEntity(stack, player, entity);
        return false;
    }

    /*@Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowedIn(tab)) {
            ItemStack stack = new ItemStack(this);
            stack.enchant(Enchantments.BANE_OF_ARTHROPODS, 5);
            stack.enchant(Enchantments.SWEEPING_EDGE, 3);
            stack.enchant(Enchantments.KNOCKBACK, 3);
            list.add(stack);
        }
    }


    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        stack.serializeNBT();
        stack.enchant(Enchantments.BANE_OF_ARTHROPODS, 5);
        stack.enchant(Enchantments.SWEEPING_EDGE, 3);
        stack.enchant(Enchantments.KNOCKBACK, 3);
    }*/

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final SwampBusterRenderer renderer = new SwampBusterRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().loop("idle"));

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
