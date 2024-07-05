package dev.sterner.armadillo_boxes.forge

import net.neoforged.fml.loading.FMLPaths
import java.nio.file.Path

object ArmadilloBoxesPlatformImpl {
    /**
     * This is our actual method to [ExampleExpectPlatform.getConfigDirectory].
     */
    @JvmStatic // Jvm Static is required so that java can access it
    fun getConfigDirectory(): Path {
        return FMLPaths.CONFIGDIR.get()
    }
}