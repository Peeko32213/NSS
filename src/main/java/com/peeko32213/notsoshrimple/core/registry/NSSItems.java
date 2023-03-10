package com.peeko32213.notsoshrimple.core.registry;

import com.peeko32213.notsoshrimple.NotSoShrimple;
import com.peeko32213.notsoshrimple.common.item.ItemModFood;
import com.peeko32213.notsoshrimple.common.item.ItemSwampBuster;
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

    public static final RegistryObject<ForgeSpawnEggItem> CRAYFISH_EGG = ITEMS.register("crayfish_spawn_egg",
            () -> new ForgeSpawnEggItem(NSSEntities.CRAYFISH , 0x754123, 0xb59a6e,
                    new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<ForgeSpawnEggItem> MANEATER_EGG = ITEMS.register("maneater_spawn_egg",
            () -> new ForgeSpawnEggItem(NSSEntities.MANEATER , 0x223146, 0xd3d1b6,
                    new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));
    public static final RegistryObject<Item> CLAW = ITEMS.register("claw",
            () -> new Item(new Item.Properties().tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<Item> SWAMP_BUSTER = ITEMS.register("swampbuster",
            () -> new ItemSwampBuster(NSSItemTiers.SHRIMPLE, 5, -3.0F));

    public static final RegistryObject<Item> RAW_COTY = ITEMS.register("raw_prawn",
            () -> new Item(new Item.Properties().food(ItemModFood.RAW_PRAWN).tab(NotSoShrimple.SHRIMPLE)));

    public static final RegistryObject<Item> COOKED_COTY = ITEMS.register("cooked_prawn",
            () -> new Item(new Item.Properties().food(ItemModFood.COOKED_PRAWN).tab(NotSoShrimple.SHRIMPLE)));


}
