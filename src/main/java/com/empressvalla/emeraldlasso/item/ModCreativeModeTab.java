package com.empressvalla.emeraldlasso.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab EMERALD_LASSO_TAB = new CreativeModeTab("emeraldlassotab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.EMERALD_LASSO.get());
        }
    };
}
