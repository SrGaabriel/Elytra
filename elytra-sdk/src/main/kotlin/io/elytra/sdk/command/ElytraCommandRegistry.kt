package io.elytra.sdk.command

import io.elytra.api.command.Command
import io.elytra.api.command.ElytraCommand
import io.elytra.api.command.annotations.CommandArgument
import io.elytra.api.command.annotations.CommandSpec
import io.elytra.api.command.registry.CommandRegistry
import io.elytra.sdk.command.defaults.*
import io.elytra.sdk.server.Elytra
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.system.measureTimeMillis

class ElytraCommandRegistry : CommandRegistry {

    private val commandRegistry: MutableMap<String, Command> = HashMap()
    private val commandAliasRegistry: MutableMap<String, Command> = HashMap()

    init {
        val timeTilRegisterDefaults = measureTimeMillis {
            registerDefaults()
        }

        Elytra.logger.debug("Registered default commands in ${timeTilRegisterDefaults}ms!")
    }

    override fun register(command: Command) {
        val commandClazz: KClass<out Command> = command::class
        val commandSpec = commandClazz.findAnnotation<CommandSpec>()

        command as ElytraCommand

        val executeFun = commandClazz.declaredFunctions.first { it.name.toLowerCase() == "execute" }

        require(commandSpec != null) { "Elytra command must have a @CommandInfo" }

        val commandName = commandSpec.label
        val aliases = commandSpec.aliases

        require(!commandRegistry.containsKey(commandName)) { "$commandName is already registered" }

        for (alias in aliases) {
            val commandByAlias = getCommandByAlias(alias)
            require(commandByAlias == null) { "$alias alias is already registered in ${commandByAlias!!.label}" }
        }

        commandRegistry[commandName] = command
        command.label = commandSpec.label

        for (alias in aliases) {
            commandAliasRegistry[alias] = command
            command.aliases += alias
        }

        executeFun.annotations
            .filterIsInstance<CommandArgument>()
            .forEach { command.argumentList += it }

        Elytra.logger.debug("Command $commandName has been registered.")
        // TODO("Validate command: arguments, name, etc.")
    }

    @Synchronized
    override fun getCommands(): Collection<Command> {
        return commandRegistry.values
    }

    @Synchronized
    override fun getCommandByName(name: String): Command? {
        return commandRegistry[name]
    }

    @Synchronized
    override fun getCommandByAlias(alias: String): Command? {
        return commandAliasRegistry[alias]
    }

    @Synchronized
    override fun disableCommand(name: String) {
        commandRegistry.remove(name)
    }

    private fun registerDefaults() {
        register(TestCommand())
        register(GamemodeCommand())
        register(DebugCommand())
        register(ChunkCommand())
        register(SayCommand())
    }
}
