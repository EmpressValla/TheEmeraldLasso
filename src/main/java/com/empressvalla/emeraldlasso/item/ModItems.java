package com.empressvalla.emeraldlasso.item;

import com.empressvalla.emeraldlasso.EmeraldLasso;
import com.empressvalla.emeraldlasso.item.custom.EmeraldLassoItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class is responsible for containing
 * the registry of items which are used in the mod
 * and allowing that registry to be added
 * to the event bus.
 */
public class ModItems {

    /**
     * Responsible for storing the deferred register for items that will be registered for this mod.
     */
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EmeraldLasso.MOD_ID);

    /**
     * Responsible for storing a registry object that represents the Emerald Lasso item.
     */
    public static final RegistryObject<Item> EMERALD_LASSO = ITEMS.register("emerald_lasso",
            () -> new EmeraldLassoItem(new Item.Properties().tab(ModCreativeModeTab.EMERALD_LASSO_TAB)) );

    /**
     * This method will register the items for this mod
     * to the event bus which is provided.
     *
     * @param eventBus The event bus for the registry to be added to.
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
