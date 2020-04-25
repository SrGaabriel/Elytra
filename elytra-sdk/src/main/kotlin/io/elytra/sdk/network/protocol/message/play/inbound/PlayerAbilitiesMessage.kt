package io.elytra.sdk.network.protocol.message.play.inbound

import com.flowpowered.network.Message

data class PlayerAbilitiesMessage(
    val invulnerable: Boolean,
    val flying: Boolean,
    val allowFlying: Boolean,
    val creativeMode: Boolean,
    val flySpeed: Float,
    val walkSpeed: Float
) : Message