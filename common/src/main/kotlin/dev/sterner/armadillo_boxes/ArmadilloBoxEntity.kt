package dev.sterner.armadillo_boxes

import dev.architectury.registry.menu.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.stream.IntStream

class ArmadilloBoxEntity(blockPos: BlockPos, blockState: BlockState
) : RandomizableContainerBlockEntity(ArmadilloBoxes.armadillo_box_entity.get(), blockPos, blockState), WorldlyContainer, ExtendedMenuProvider {

    val SLOTS: IntArray = IntStream.range(0, CONTAINER_SIZE).toArray()
    private var itemStacks: NonNullList<ItemStack>? = null

    init {
        this.itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY)
    }

    override fun createMenu(i: Int, playerInventory: Inventory, playerEntity: Player?): AbstractContainerMenu {
        return ArmadilloBoxMenu(i, playerInventory, this)
    }

    override fun saveExtraData(buf: FriendlyByteBuf) {
        buf.writeBlockPos(blockPos)
    }

    fun loadFromTag(compoundTag: CompoundTag, provider: HolderLookup.Provider?) {
        this.itemStacks = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        if (compoundTag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(compoundTag, this.itemStacks, provider)
        }
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider?) {
        super.loadAdditional(compoundTag, provider)
        this.loadFromTag(compoundTag, provider)
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider?) {
        super.saveAdditional(compoundTag, provider)
        ContainerHelper.saveAllItems(compoundTag, this.itemStacks, false, provider)
    }

    override fun getContainerSize(): Int {
        return itemStacks!!.size
    }

    override fun getSlotsForFace(direction: Direction): IntArray {
        return SLOTS
    }

    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean {
        return Block.byItem(itemStack.item) !is ArmadilloBoxBlock
    }

    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction): Boolean {
        val state = this.blockState
        val directionFromState = state.getValue(BlockStateProperties.FACING)
        return directionFromState == direction.opposite
    }

    override fun createMenu(i: Int, inventory: Inventory): AbstractContainerMenu {
        return ArmadilloBoxMenu(i, inventory, this)
    }

    override fun getDefaultName(): Component {
        return Component.translatable("container.armadillo_box")
    }

    override fun getItems(): NonNullList<ItemStack> {
        return itemStacks!!
    }

    override fun setItems(nonNullList: NonNullList<ItemStack>) {
        this.itemStacks = nonNullList
    }

    companion object {
        const val COLUMNS: Int = 7
        const val ROWS: Int = 3
        const val CONTAINER_SIZE: Int = 21

    }
}