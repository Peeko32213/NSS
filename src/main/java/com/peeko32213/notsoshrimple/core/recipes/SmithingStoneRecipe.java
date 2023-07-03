package com.peeko32213.notsoshrimple.core.recipes;

import com.google.gson.JsonObject;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.core.registry.NSSItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class SmithingStoneRecipe extends UpgradeRecipe {

    final Ingredient base;
    final Ingredient addition;
    final ItemStack product;
    private final ResourceLocation id;
    private final UUID somberStoneBuffUUID = UUID.randomUUID();
    private final UUID smithingStoneBuffUUID = UUID.randomUUID();

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public ItemStack getResultItem() {
        return this.product.copy();
    }

    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public SmithingStoneRecipe(ResourceLocation pId, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
        super(pId, pBase, pAddition, pResult);
        this.base = pBase;
        this.addition = pAddition;
        this.product = base.getItems()[0];
        this.id = pId;
        //Standard smithing stones increase DURABILITY.
        //Somber smithing stones increase DAMAGE and ARMOUR.
    }


    @Override
    public ItemStack assemble(Container pInv) {
        ItemStack itemstack = this.product.copy();
        System.out.println("original durability " + itemstack.getItem().maxDamage);

        if(addition.test(NSSItems.SOMBER_STONE.get().getDefaultInstance())) {
            double baseDmg = pInv.getItem(0).getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().toList().get(0).getAmount();
            AttributeModifier old = new AttributeModifier(somberStoneBuffUUID, "somber_stone_dmg_mod", baseDmg, AttributeModifier.Operation.ADDITION);

            if (itemstack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsEntry(Attributes.ATTACK_DAMAGE, old)) {
                itemstack.getAttributeModifiers(EquipmentSlot.MAINHAND).remove(Attributes.ATTACK_DAMAGE, old);
                System.out.println("removed");
                //remove the old attributemodifier to prevent stacking
            }

            AttributeModifier dmgModifier = new AttributeModifier(somberStoneBuffUUID, "somber_stone_dmg_mod", baseDmg + 1, AttributeModifier.Operation.ADDITION);
            itemstack.addAttributeModifier(Attributes.ATTACK_DAMAGE, dmgModifier, EquipmentSlot.MAINHAND);
            //add a new AttributeModifier each time the weapon is refined that increases its damage
        }

        if(addition.test(NSSItems.SMITHING_STONE.get().getDefaultInstance())) {
            CompoundTag stackTags = itemstack.getOrCreateTag();
            System.out.println("old tags " + stackTags);

            if (stackTags.getInt("smithing_stone_durability_bonus") != 0) {
                int originalValue = stackTags.getInt("smithing_stone_durability_bonus");
                stackTags.putInt("smithing_stone_durability_bonus", originalValue + 5);
                System.out.println("originally " + originalValue);

            } else {
                stackTags.putInt("smithing_stone_durability_bonus", 5);
            }

            itemstack.setTag(stackTags.copy());
            //change the item's max damage taken each time the weapon is reinforced by 5, after all total runs the weapon has an extra 30 durability.
        }

        System.out.println("now tags " + itemstack.getOrCreateTag());
        System.out.println("now durability " + itemstack.getTag().getInt("smithing_stone_durability_bonus"));
        return itemstack;
    }

    public static class Serializer implements RecipeSerializer<SmithingStoneRecipe> {
        //forgor Serializer.INSTANCE?
        //public static final ResourceLocation ID = new ResourceLocation(NotSoShrimple.MODID, "smithing_stone_recipe");

        public SmithingStoneRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            Ingredient base = Ingredient.fromJson(GsonHelper.getAsJsonObject(pJson, "base"));
            Ingredient addition = Ingredient.fromJson(GsonHelper.getAsJsonObject(pJson, "addition"));
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            return new SmithingStoneRecipe(pRecipeId, base, addition, itemstack);
        }

        public SmithingStoneRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf pBuffer) {
            Ingredient base = Ingredient.fromNetwork(pBuffer);
            Ingredient addition = Ingredient.fromNetwork(pBuffer);
            ItemStack itemstack = pBuffer.readItem();
            return new SmithingStoneRecipe(recipeId, base, addition, itemstack);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, SmithingStoneRecipe pRecipe) {
            pRecipe.base.toNetwork(pBuffer);
            pRecipe.addition.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.getResultItem());
            //pRecipe.product.copy?
        }
    }

}