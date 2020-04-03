package io.elytra.sdk.entity

import com.flowpowered.network.Message
import com.mojang.authlib.GameProfile
import io.elytra.api.chat.ChatMode
import io.elytra.api.chat.TextComponent
import io.elytra.api.entity.EntityState
import io.elytra.api.entity.Player
import io.elytra.api.entity.PlayerMode
import io.elytra.api.nbt.tags.NbtCompound
import io.elytra.api.utils.asJson
import io.elytra.api.world.Position
import io.elytra.api.world.World
import io.elytra.api.world.enums.GameMode
import io.elytra.sdk.network.NetworkSession
import io.elytra.sdk.network.protocol.message.DisconnectMessage
import io.elytra.sdk.network.protocol.message.play.OutboundChatMessage
import io.elytra.sdk.server.Elytra

data class ElytraPlayer(
	var id: Int,
	var sessionId: String,
	override var displayName: String,
	override var gameProfile: GameProfile?,
	override var mode: PlayerMode,
	override var online: Boolean,
	override var banned: Boolean,
	override var gamemode: GameMode = GameMode.SURVIVAL
) : Player, ElytraEntity(0) {

	override fun tick() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun kick(reason: String) {
		session()?.send(DisconnectMessage(reason))
	}

	override fun sendMessage(message: String) {
		sendMessage(TextComponent(message))
	}

	override fun sendMessage(textComponent: TextComponent) {
		sendPacket(OutboundChatMessage(textComponent.asJson(), ChatMode.PLAYER))
	}

	private fun session(): NetworkSession? {
		return Elytra.server.sessionRegistry.get(sessionId)
	}

	fun sendPacket(packet: Message) {
		session()?.sendWithFuture(packet)
	}
}
