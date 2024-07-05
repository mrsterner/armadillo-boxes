package dev.sterner.armadillo_boxes.forge

import dev.sterner.armadillo_boxes.ArmadilloBoxes
import net.neoforged.fml.common.Mod

@Mod(ArmadilloBoxes.MOD_ID)
object ArmadilloBoxesForge {

    init {
        ArmadilloBoxes.init()
    }
}