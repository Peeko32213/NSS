package com.peeko32213.notsoshrimple.core.recipes;

import com.google.gson.JsonObject;
import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.core.registry.NSSAttributes;
import com.peeko32213.notsoshrimple.core.registry.NSSItems;
import com.peeko32213.notsoshrimple.core.registry.NSSTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.List;
import java.util.UUID;

public class SmithingStoneRecipe extends UpgradeRecipe {


    //final ItemStack actualFirstItem = this.(0);
    final Ingredient base;
    final Ingredient addition;
    final ItemStack product;
    private final ResourceLocation id;

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ItemStack getResultItem() {
        return this.product.copy();
    }

    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        Item toBeSmithed = pInv.getItem(0).getItem();

        if (pInv.getItem(0).is(NSSTags.SMITHINGWHITELIST)) {
            return true;
            //whitelist
        }

        return (toBeSmithed instanceof TieredItem || toBeSmithed instanceof ShieldItem || toBeSmithed instanceof ProjectileWeaponItem || toBeSmithed instanceof ElytraItem || toBeSmithed instanceof TridentItem) && this.addition.test(pInv.getItem(1)) && !pInv.getItem(0).is(NSSTags.SMITHINGBLACKLIST);
        //conditions for smithing
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
        //ItemStack itemstack = this.product.copy();
        ItemStack itemstack = pInv.getItem(0).copy();
        System.out.println("addition " + this.addition.getItems()[0]);
        //the statement pInv.getItem(0) gives you the item in the first ingredient slot in its entirety. Use it for compat.

        if(addition.test(NSSItems.SOMBER_STONE.get().getDefaultInstance())) {
            CompoundTag itemTags = itemstack.getOrCreateTag().copy();
            //current item tags

            if (itemTags.contains("SomberDamageBuff")) {
                int currentDmgBuff = itemTags.getInt("SomberDamageBuff");
                itemTags.putInt("SomberDamageBuff", currentDmgBuff + 1);
                //if the buff already exists increase it

            } else {
                //otherwise add the buff
                itemTags.putInt("SomberDamageBuff", 1);
            }

            itemstack.setTag(itemTags.copy());
            //adds the buff
        }
        //somber stuff

        if(addition.test(NSSItems.SMITHING_STONE.get().getDefaultInstance())) {
            System.out.println("smith");
            CompoundTag itemTags = itemstack.getOrCreateTag().copy();

            if (itemTags.contains("SmithingDurabilityBuff")) {
                int currentDmgBuff = itemTags.getInt("SmithingDurabilityBuff");
                itemTags.putInt("SmithingDurabilityBuff", currentDmgBuff + 30);
                //if the buff already exists increase it

            } else {
                itemTags.putInt("SmithingDurabilityBuff", 30);
                //otherwise add the buff
            }

            itemstack.setTag(itemTags.copy());
            //modifies this new attribute to contain the extra healthbar
        }
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