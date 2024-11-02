package net.permutated.exmachinis.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.items.ComparatorUpgradeItem;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.util.WorkStatus;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractMachineMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess containerLevelAccess;
    protected final DataHolder dataHolder;
    protected final boolean enableMeshSlot;
    protected final boolean enableComparatorSlot;
    protected final int totalSlots;

    protected AbstractMachineMenu(@Nullable MenuType<?> containerType, int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(containerType, windowId);
        this.enableMeshSlot = buf.readBoolean();
        this.enableComparatorSlot = buf.readBoolean();

        if (enableMeshSlot) {
            totalSlots = 11;
        } else if (enableComparatorSlot) {
            totalSlots = 20;
        } else {
            totalSlots = 10;
        }

        BlockPos pos = buf.readBlockPos();
        Level level = playerInventory.player.getCommandSenderWorld();
        if (level instanceof ServerLevel serverLevel) { // server-side
            this.containerLevelAccess = ContainerLevelAccess.create(level, pos);
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            if (blockEntity instanceof AbstractMachineTile tile) {
                this.dataHolder = new DataHolderServer(tile);
                registerHandlerSlots(tile.overlayItemhandler);
            } else {
                ExMachinis.LOGGER.error("Tried to create DataHolder on server, but did not find matching tile for pos: {}", pos);
                this.dataHolder = new DataHolderClient();
            }
        } else { // client-side
            this.containerLevelAccess = ContainerLevelAccess.NULL;
            this.dataHolder = new DataHolderClient();
            registerHandlerSlots(new ItemStackHandler(totalSlots));
        }

        registerPlayerSlots(new InvWrapper(playerInventory));
        registerDataSlots();
    }

    protected abstract Supplier<Block> getBlock();

    protected WorkStatus getWorkStatus() {
        return dataHolder.getWorkStatus();
    }

    @Override
    public boolean stillValid(Player playerEntity) {
        return stillValid(containerLevelAccess, playerEntity, getBlock().get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        int inventorySize = totalSlots;

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < inventorySize) {
                if (!this.moveItemStackTo(stack, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public void registerHandlerSlots(IItemHandler handler) {
        int index = 0;
        int upgradeOffsetX = enableComparatorSlot ? 134 : 116;
        addSlot(new FilteredSlot(handler, index++, upgradeOffsetX, 54, stack -> stack.getItem() instanceof UpgradeItem));
        if (enableMeshSlot) {
            addSlot(new FilteredSlot(handler, index++, 80, 36, ExNihiloAPI::isMeshItem));
        }
        if (enableComparatorSlot) {
            addSlot(new FilteredSlot(handler, index++, upgradeOffsetX, 18, stack -> stack.getItem() instanceof ComparatorUpgradeItem));
        }

        int width = enableComparatorSlot ? 6 : 3;

        // 3 x 3
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < width; j++) {
                addSlot(new SlotItemHandler(handler, index++, 8 + j * 18, 18 + i * 18));
            }
        }
    }

    public void registerPlayerSlots(IItemHandler wrappedInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(wrappedInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(wrappedInventory, i, 8 + i * 18, 142));
        }
    }

    public void registerDataSlots() {
        addDataSlot(dataHolder::getWork, dataHolder::setWork);
        addDataSlot(dataHolder::getMaxWork, dataHolder::setMaxWork);
        addDataSlot(() -> dataHolder.getWorkStatus().ordinal(), dataHolder::setWorkStatus);

        // split max energy
        addDataSlot(() -> dataHolder.getMaxEnergy() & 0xffff, value -> {
            int energyStored = dataHolder.getMaxEnergy() & 0xffff0000;
            dataHolder.setMaxEnergy(energyStored + (value & 0xffff));
        });

        addDataSlot(() -> (dataHolder.getMaxEnergy() >> 16) & 0xffff, value -> {
            int energyStored = dataHolder.getMaxEnergy() & 0x0000ffff;
            dataHolder.setMaxEnergy(energyStored | (value << 16));
        });

        // split current energy
        addDataSlot(() -> dataHolder.getEnergy() & 0xffff, value -> {
            int energyStored = dataHolder.getEnergy() & 0xffff0000;
            dataHolder.setEnergy(energyStored + (value & 0xffff));
        });

        addDataSlot(() -> (dataHolder.getEnergy() >> 16) & 0xffff, value -> {
            int energyStored = dataHolder.getEnergy() & 0x0000ffff;
            dataHolder.setEnergy(energyStored | (value << 16));
        });
    }

    private void addDataSlot(IntSupplier getter, IntConsumer setter) {
        addDataSlot(new LambdaDataSlot(getter, setter));
    }

    /**
     * Based on <a href="https://github.com/Shadows-of-Fire/Placebo/blob/b104501c18e2f6432c843944a8106d07cab825cf/src/main/java/shadows/placebo/container/EasyContainerData.java">Placebo</a>
     */
    static class LambdaDataSlot extends DataSlot {

        private final IntSupplier getter;
        private final IntConsumer setter;

        public LambdaDataSlot(IntSupplier getter, IntConsumer setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int get() {
            return this.getter.getAsInt();
        }

        @Override
        public void set(int pValue) {
            this.setter.accept(pValue);
        }

    }

    /**
     * Based on <a href="https://github.com/Shadows-of-Fire/Placebo/blob/b104501c18e2f6432c843944a8106d07cab825cf/src/main/java/shadows/placebo/container/FilteredSlot.java">Placebo</a>
     */
    static class FilteredSlot extends SlotItemHandler {

        protected final Predicate<ItemStack> filter;

        public FilteredSlot(IItemHandler handler, int index, int x, int y, Predicate<ItemStack> filter) {
            super(handler, index, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.filter.test(stack);
        }
    }
}
