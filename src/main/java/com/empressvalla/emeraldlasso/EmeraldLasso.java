package com.empressvalla.emeraldlasso;

import com.empressvalla.emeraldlasso.config.ConfigManager;
import com.empressvalla.emeraldlasso.item.ModCreativeModeTab;
import com.empressvalla.emeraldlasso.item.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * This class is the main entry point to the
 * mod and contains the setup functionality
 * as well as the shared mod ID.
 */
@Mod(EmeraldLasso.MOD_ID)
public class EmeraldLasso
{

    /**
     * Responsible for storing the mod ID for this mod, we provide
     * access to this so that it can be shared to any candidates
     * that require it.
     */
    public static final String MOD_ID = "emeraldlasso";

    /**
     * Responsible for storing the logger that will be used.
     */
    private static final Logger LOGGER = LogUtils.getLogger();

    public EmeraldLasso()
    {
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.SPEC_COMMON, "emeraldlasso-common.toml");

        eventBus.addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        eventBus.addListener(this::addCreative);
    }


    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModCreativeModeTab.EMERALD_LASSO_TAB) {
            event.accept(ModItems.EMERALD_LASSO);
        }
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
    }

}
