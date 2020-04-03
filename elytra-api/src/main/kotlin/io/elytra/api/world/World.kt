package io.elytra.api.world

import io.elytra.api.entity.Player
import io.elytra.api.utils.Tickable

interface World : Tickable {
    val name: String
    val spawnPoint: Position

    fun getChunkAt(x: Int, z: Int): Chunk?

    fun setChunkAt(x: Int, y: Int, z: Int, blockId: Int)

    fun teleport(player: Player, position: Position)
}
