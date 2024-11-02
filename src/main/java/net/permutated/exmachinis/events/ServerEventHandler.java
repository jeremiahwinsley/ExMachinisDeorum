package net.permutated.exmachinis.events;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.recipes.CompactingRecipe;

import java.util.List;

@EventBusSubscriber(modid = ExMachinis.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ServerEventHandler {

    private ServerEventHandler() {
        // nothing to do
    }

    public static void loadCompactingRecipes(RecipeManager recipeManager) {
        List<RecipeHolder<CompactingRecipe>> compactingRecipes = recipeManager
            .getAllRecipesFor(ModRegistry.COMPACTING_RECIPE_TYPE.get());
        ModRegistry.COMPACTING_REGISTRY.setRecipeList(compactingRecipes);
        ExMachinis.LOGGER.debug("Registered {} compacting recipes", compactingRecipes.size());
    }

    @SubscribeEvent
    public static void onRecipesUpdatedEvent(final RecipesUpdatedEvent event) {
        ExMachinis.LOGGER.debug("Loading recipes on server sync");
        loadCompactingRecipes(event.getRecipeManager());
    }

    @SubscribeEvent
    public static void onServerStartingEvent(final ServerStartingEvent event) {
        if (event.getServer().isDedicatedServer()) {
            ExMachinis.LOGGER.debug("Loading recipes on server startup");
            loadCompactingRecipes(event.getServer().getRecipeManager());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel() instanceof ServerLevel level
            && event.getEntity().isShiftKeyDown()
            && ExNihiloAPI.isHammerItem(event.getItemStack())
        ) {
            BlockState blockState = level.getBlockState(event.getPos());
            if (blockState.getBlock() instanceof AbstractMachineBlock) {
                Direction facing = event.getHitVec().getDirection();
                Direction output = blockState.getValue(AbstractMachineBlock.OUTPUT);
                event.setUseBlock(TriState.FALSE);
                if (!facing.equals(output)) {
                    level.setBlock(event.getPos(), blockState.setValue(AbstractMachineBlock.OUTPUT, facing), Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}
