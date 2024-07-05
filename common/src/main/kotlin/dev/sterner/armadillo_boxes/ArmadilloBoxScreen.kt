package dev.sterner.armadillo_boxes

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

@Environment(EnvType.CLIENT)
class ArmadilloBoxScreen(armadilloBoxMenu: ArmadilloBoxMenu?, inventory: Inventory?, component: Component?) :
    AbstractContainerScreen<ArmadilloBoxMenu?>(armadilloBoxMenu, inventory, component) {
    init {
        ++this.imageHeight
        this.titleLabelX = 24
    }

    override fun render(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(guiGraphics, i, j, f)
        this.renderTooltip(guiGraphics, i, j)
    }

    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        val k = (this.width - this.imageWidth) / 2
        val l = (this.height - this.imageHeight) / 2
        guiGraphics.blit(CONTAINER_TEXTURE, k, l, 0, 0, this.imageWidth, this.imageHeight)
    }

    companion object {
        private val CONTAINER_TEXTURE: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(ArmadilloBoxes.MOD_ID,"textures/gui/armadillo_box.png")
    }
}