package dev.sterner.armadillo_boxes

import com.mojang.serialization.MapCodec
import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.piglin.PiglinAi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Consumer

class ArmadilloBoxBlock(properties: Properties) : BaseEntityBlock(properties) {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): ArmadilloBoxEntity? {
        return ArmadilloBoxes.armadillo_box_entity.get().create(blockPos, blockState)
    }


    init {
        this.registerDefaultState(
            (stateDefinition.any() as BlockState).setValue(
                BlockStateProperties.FACING,
                Direction.UP
            ) as BlockState
        )
        Blocks.SHULKER_BOX
    }

    override fun getRenderShape(blockState: BlockState?): RenderShape {
        return RenderShape.MODEL
    }

    override fun useWithoutItem(
        blockState: BlockState?,
        level: Level,
        blockPos: BlockPos?,
        player: Player,
        blockHitResult: BlockHitResult?
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        } else if (player.isSpectator) {
            return InteractionResult.CONSUME
        } else {
            val blockEntity = level.getBlockEntity(blockPos)
            if (blockEntity is ArmadilloBoxEntity && blockEntity is ExtendedMenuProvider) {
                val menu = blockEntity as ExtendedMenuProvider
                MenuRegistry.openExtendedMenu(player as ServerPlayer, menu)

                PiglinAi.angerNearbyPiglins(player, true)

                return InteractionResult.CONSUME
            } else {
                return InteractionResult.PASS
            }
        }
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {

        return when (blockState.getValue(BlockStateProperties.FACING)) {
            Direction.UP -> {
                SHAPE_UP}
            Direction.DOWN -> {
                SHAPE_DOWN}
            Direction.EAST -> {
                SHAPE_EAST}
            Direction.NORTH -> {
                SHAPE_NORTH}
            Direction.SOUTH -> {
                SHAPE_SOUTH}
            Direction.WEST -> {
                SHAPE_WEST}
        }
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(BlockStateProperties.FACING, blockPlaceContext.clickedFace) as BlockState
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(*arrayOf<Property<*>>(BlockStateProperties.FACING))
    }

    override fun playerWillDestroy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState?,
        player: Player
    ): BlockState {
        val blockEntity = level.getBlockEntity(blockPos)
        if (blockEntity is ArmadilloBoxEntity) {
            if (!level.isClientSide && player.isCreative && !blockEntity.isEmpty) {
                val itemStack = ArmadilloBoxes.armadillo_box.get().defaultInstance
                itemStack.applyComponents(blockEntity.collectComponents())
                val itemEntity = ItemEntity(
                    level,
                    blockPos.x.toDouble() + 0.5,
                    blockPos.y.toDouble() + 0.5,
                    blockPos.z.toDouble() + 0.5,
                    itemStack
                )
                itemEntity.setDefaultPickUpDelay()
                level.addFreshEntity(itemEntity)
            }
        }

        return super.playerWillDestroy(level, blockPos, blockState, player)
    }

    override fun getDrops(blockState: BlockState?, builder: LootParams.Builder): List<ItemStack> {
        var builder = builder
        val blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
        if (blockEntity is ArmadilloBoxEntity) {
            builder = builder.withDynamicDrop(
                CONTENTS!!
            ) { consumer: Consumer<ItemStack?> ->
                for (i in 0 until blockEntity.containerSize) {
                    consumer.accept(blockEntity.getItem(i))
                }
            }
        }

        return super.getDrops(blockState, builder)
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos?,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if (!blockState.`is`(blockState2.block)) {
            val blockEntity = level.getBlockEntity(blockPos)
            if (blockEntity is ArmadilloBoxEntity) {
                level.updateNeighbourForOutputSignal(blockPos, blockState.block)
            }

            super.onRemove(blockState, level, blockPos, blockState2, bl)
        }
    }

    override fun hasAnalogOutputSignal(blockState: BlockState?): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(blockState: BlockState?, level: Level, blockPos: BlockPos?): Int {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos))
    }

    override fun getCloneItemStack(levelReader: LevelReader, blockPos: BlockPos?, blockState: BlockState?): ItemStack {
        val itemStack = super.getCloneItemStack(levelReader, blockPos, blockState)

        levelReader.getBlockEntity(blockPos, ArmadilloBoxes.armadillo_box_entity.get())
            .ifPresent { be: ArmadilloBoxEntity ->
                be.saveToItem(itemStack, levelReader.registryAccess())
            }
        return itemStack
    }

    override fun rotate(blockState: BlockState, rotation: Rotation): BlockState {
        return blockState.setValue(
            BlockStateProperties.FACING,
            rotation.rotate(blockState.getValue(BlockStateProperties.FACING) as Direction)
        ) as BlockState
    }

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(BlockStateProperties.FACING) as Direction))
    }

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return CODEC
    }

    companion object{

        val SHAPE_UP: VoxelShape = box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0)
        val SHAPE_DOWN: VoxelShape = box(3.0, 6.0, 3.0, 13.0, 16.0, 13.0)

        val SHAPE_EAST: VoxelShape = box(0.0, 3.0, 3.0, 10.0, 13.0, 13.0)
        val SHAPE_WEST: VoxelShape = box(6.0, 3.0, 3.0, 16.0, 13.0, 13.0)

        val SHAPE_NORTH: VoxelShape = box(3.0, 3.0, 6.0, 13.0, 13.0, 16.0)
        val SHAPE_SOUTH: VoxelShape = box(3.0, 3.0, 0.0, 13.0, 13.0, 10.0)

        val CODEC: MapCodec<ArmadilloBoxBlock> = simpleCodec { properties: Properties? ->
            ArmadilloBoxBlock(
                properties!!
            )
        }
        val CONTENTS: ResourceLocation? = ResourceLocation.withDefaultNamespace("contents");
    }
}