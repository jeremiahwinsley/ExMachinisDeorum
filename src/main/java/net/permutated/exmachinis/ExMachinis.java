package net.permutated.exmachinis;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.machines.buffer.ItemBufferScreen;
import net.permutated.exmachinis.machines.buffer.ItemBufferTile;
import net.permutated.exmachinis.machines.compactor.FluxCompactorScreen;
import net.permutated.exmachinis.machines.hammer.FluxHammerScreen;
import net.permutated.exmachinis.machines.sieve.FluxSieveScreen;
import org.slf4j.Logger;

@Mod(ExMachinis.MODID)
public class ExMachinis {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "exmachinis";

    public ExMachinis(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Registering mod: {}", MODID);

        ModRegistry.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(this::registerCapabilities);
    }

    public void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModRegistry.FLUX_SIEVE_MENU.get(), FluxSieveScreen::new);
        event.register(ModRegistry.FLUX_HAMMER_MENU.get(), FluxHammerScreen::new);
        event.register(ModRegistry.FLUX_COMPACTOR_MENU.get(), FluxCompactorScreen::new);
        event.register(ModRegistry.ITEM_BUFFER_MENU.get(), ItemBufferScreen::new);
    }

    public void registerCapabilities(final RegisterCapabilitiesEvent event) {
        AbstractMachineTile.registerCapabilities(event, ModRegistry.FLUX_COMPACTOR_TILE.get());
        AbstractMachineTile.registerCapabilities(event, ModRegistry.FLUX_HAMMER_TILE.get());
        AbstractMachineTile.registerCapabilities(event, ModRegistry.FLUX_SIEVE_TILE.get());
        ItemBufferTile.registerItemCapability(event);
    }
}
