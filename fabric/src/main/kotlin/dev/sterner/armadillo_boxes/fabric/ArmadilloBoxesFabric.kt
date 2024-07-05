package dev.sterner.armadillo_boxes.fabric

import net.fabricmc.api.ModInitializer
import dev.sterner.armadillo_boxes.ArmadilloBoxes

object ArmadilloBoxesFabric: ModInitializer {
    override fun onInitialize() {
        ArmadilloBoxes.init()
    }
}
