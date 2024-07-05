package dev.sterner.armadillo_boxes

import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.menu.MenuRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction


object ArmadilloBoxes {
    const val MOD_ID = "armadillo_boxes"

    private val createModeTabs = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB)
    val armadilloBoxesTab: RegistrySupplier<CreativeModeTab> = createModeTabs.register("armadillo_boxes") {
        CreativeTabRegistry.create(Component.translatable("category.armadillo_boxes")) {
            ItemStack(armadillo_box.get())
        }
    }

    private val items = DeferredRegister.create(MOD_ID, Registries.ITEM)
    private val blocks = DeferredRegister.create(MOD_ID, Registries.BLOCK)
    private val block_entity_types = DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE)
    private val menu_types = DeferredRegister.create(MOD_ID, Registries.MENU)

    val armadillo: RegistrySupplier<Block> = blocks.register(
        "armadillo"
    ) {
        ArmadilloBoxBlock(BlockBehaviour.Properties.of().forceSolidOn()
            .strength(2.0F)
            .dynamicShape()
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY))
    }

    val armadillo_box: RegistrySupplier<Item> = items.register(
        "armadillo_box"
    ) {
        BlockItem(
            armadillo.get(),
            Item.Properties().`arch$tab`(armadilloBoxesTab),
        )
    }

    val armadillo_box_entity = block_entity_types.register(
        "armadillo_box_entity"
    ) {
        BlockEntityType.Builder.of(::ArmadilloBoxEntity, armadillo.get()).build(null)
    }

    val armadillo_menu = menu_types.register(
        "armadillo_menu"
    ) { MenuRegistry.ofExtended { id, inv, pla -> ArmadilloBoxMenu(id, inv) } }

    @JvmStatic
    fun init() {

        ClientLifecycleEvent.CLIENT_SETUP.register {
            MenuRegistry.registerScreenFactory(armadillo_menu.get(), ::ArmadilloBoxScreen);
        }

        menu_types.register()
        createModeTabs.register()
        blocks.register()
        block_entity_types.register()
        items.register()

        DispenserBlock.registerBehavior(armadillo_box.get(), ShulkerBoxDispenseBehavior())
    }
}