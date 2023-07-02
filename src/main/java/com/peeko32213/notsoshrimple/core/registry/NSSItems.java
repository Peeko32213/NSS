package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.item.ItemClawblade;
import com.peeko32213.notsoshrimple.common.item.ItemModFood;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NSSItems {

    private NSSItems() {
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            NotSoShrimple.MODID);

    public static final RegistryObject<ForgeSpawnEggItem> CRAYFISH_SPAWN = ITEMS.register("crayfish_spawn_egg",
            () -> new ForgeSpawnEggItem(NSSEntities.CRAYFISH , 0x754123, 0xb59a6e,
                    new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<ForgeSpawnEggItem> MANEATER_SPAWN = ITEMS.register("maneater_spawn_egg",
            () -> new ForgeSpawnEggItem(NSSEntities.MANEATER , 0x223146, 0xd3d1b6,
                    new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));
    public static final RegistryObject<Item> CLAW = ITEMS.register("claw",
            () -> new Item(new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<Item> GREAT_PRAWN_CLAWBLADE = ITEMS.register("swampbuster",
            () -> new ItemClawblade(NSSItemTiers.SHRIMPLE, 14, -3.0F));

    public static final RegistryObject<Item> RAW_PRAWN = ITEMS.register("raw_prawn",
            () -> new Item(new Item.Properties().food(ItemModFood.RAW_PRAWN).tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<Item> COOKED_PRAWN = ITEMS.register("cooked_prawn",
            () -> new Item(new Item.Properties().food(ItemModFood.COOKED_PRAWN).tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<Item> SMITHING_STONE = ITEMS.register("smithing_stone",
            () -> new Item(new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<Item> SOMBER_STONE = ITEMS.register("somber_stone",
            () -> new Item(new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));


}
