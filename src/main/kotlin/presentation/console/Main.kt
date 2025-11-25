package presentation.console

import data.repository.UserRepositoryImpl
import domain.usecase.UserManager
import domain.util.StringsManager
import presentation.command.CommandHandler
import presentation.command.CommandParser

fun main() {
    val userRepository = UserRepositoryImpl()
    val userManager = UserManager(userRepository)
    val commandHandler = CommandHandler(userManager)
    val commandParser = CommandParser()

    println(StringsManager.getAppTitle())
    println(StringsManager.getAppHelp())

    runInteractiveMode(commandParser, commandHandler)
}

private fun runInteractiveMode(parser: CommandParser, handler: CommandHandler) {
    while (true) {
        print("\n> ")
        val input = readLine() ?: continue

        when (val command = parser.parse(input)) {
            null -> println("${StringsManager.getErrorEmoji()} ${StringsManager.getUnknownCommand()}")
            is presentation.command.Command.Exit -> {
                println(handler.handleCommand(command))
                break
            }
            else -> println(handler.handleCommand(command))
        }
    }
}