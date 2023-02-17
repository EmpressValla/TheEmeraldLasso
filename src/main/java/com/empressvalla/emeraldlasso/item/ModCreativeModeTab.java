package com.empressvalla.emeraldlasso.item;

import com.empressvalla.emeraldlasso.EmeraldLasso;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EmeraldLasso.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTab {
    public static CreativeModeTab EMERALD_LASSO_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTab(CreativeModeTabEvent.Register event) {
        EMERALD_LASSO_TAB = event.registerCreativeModeTab(new ResourceLocation(EmeraldLasso.MOD_ID, "emeraldlassotab"), builder ->
                builder.icon(() -> new ItemStack(ModItems.EMERALD_LASSO.get()))
                        .title(Component.translatable("itemGroup.emeraldlassotab")).build());
    }
}
