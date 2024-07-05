package dev.sterner.armadillo_boxes

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ShulkerBoxSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class ArmadilloBoxMenu @JvmOverloads constructor(
    i: Int,
    inventory: Inventory,
    container: Container = SimpleContainer(CONTAINER_SIZE)
) :
    AbstractContainerMenu(ArmadilloBoxes.armadillo_menu.get(), i) {

    private val container: Container

    init {
        checkContainerSize(container, CONTAINER_SIZE)
        this.container = container
        container.startOpen(inventory.player)
        var m: Int
        var p = 0

        while (p < 3) {
            m = 0
            while (m < 7) {
                this.addSlot(ShulkerBoxSlot(container, m + p * 7, 8 + m * 18 + 18, 18 + p * 18))
                ++m
            }
            ++p
        }

        var l = 0
        while (l < 3) {
            m = 0
            while (m < 9) {
                this.addSlot(Slot(inventory, m + l * 9 + 9, 8 + m * 18, 84 + l * 18))
                ++m
            }
            ++l
        }

        l = 0
        while (l < 9) {
            this.addSlot(Slot(inventory, l, 8 + l * 18, 142))
            ++l
        }
    }

    override fun stillValid(player: Player): Boolean {
        return container.stillValid(player)
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[i]
        if (slot.hasItem()) {
            val itemStack2 = slot.item
            itemStack = itemStack2.copy()
            if (i < container.containerSize) {
                if (!this.moveItemStackTo(
                        itemStack2,
                        container.containerSize, slots.size, true
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, container.containerSize, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }

        return itemStack
    }

    override fun removed(player: Player) {
        super.removed(player)
        container.stopOpen(player)
    }

    companion object {
        private const val CONTAINER_SIZE = 21
    }
}