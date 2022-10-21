package com.empressvalla.emeraldlasso.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * This class is responsible for containing
 * the creative mode tabs that are
 * custom and specific to this mod.
 */
public class ModCreativeModeTab {

    /**
     * Responsible for storing the creative mode tab for the emerald lasso. There is only
     * one item so this sits in its own tab.
     */
    public static final CreativeModeTab EMERALD_LASSO_TAB = new CreativeModeTab("emeraldlassotab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.EMERALD_LASSO.get());
        }
    };
}
