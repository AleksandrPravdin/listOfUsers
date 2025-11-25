package presentation.command

import domain.util.Result
import domain.usecase.UserManager
import domain.util.StringsManager

class CommandHandler(
    private val userManager: UserManager
) {

    fun handleCommand(command: Command): String {
        return try {
            when (command) {
                is Command.Exit -> StringsManager.getGoodbye()
                is Command.Help -> StringsManager.getHelpText()
                is Command.ListUsers -> handleListUsers()
                is Command.CreateUser -> handleCreateUser(command.name, command.email)
                is Command.UpdateUser -> handleUpdateUser(command.id, command.name, command.email)
                is Command.DeleteUser -> handleDeleteUser(command.id)
                is Command.FindUserById -> handleFindUserById(command.id)
                is Command.SortUsersByName -> handleSortUsersByName()
                is Command.FilterByDomain -> handleFilterByDomain(command.domain)
                is Command.SearchByName -> handleSearchByName(command.query)
                is Command.Stats -> handleStats()
            }
        } catch (e: Exception) {
            StringsManager.getCommandError(e.message ?: "Unknown error")
        }
    }

    private fun handleCreateUser(name: String, email: String): String {
        return when (val result = userManager.createUser(name, email)) {
            is Result.Success -> "${StringsManager.getSuccessEmoji()} ${StringsManager.getUserCreated(result.data.toString())}"
            is Result.Error -> "${StringsManager.getErrorEmoji()} ${StringsManager.getUserCreated(name)}: ${result.getErrorMessage()}"
        }
    }

    private fun handleUpdateUser(id: Int, name: String, email: String): String {
        return when (val result = userManager.updateUser(id, name, email)) {
            is Result.Success -> "${StringsManager.getSuccessEmoji()} ${StringsManager.getUserUpdated(result.data.toString())}"
            is Result.Error -> "${StringsManager.getErrorEmoji()} ${StringsManager.getUserUpdated(name)}: ${result.getErrorMessage()}"
        }
    }

    private fun handleDeleteUser(id: Int): String {
        return when (val result = userManager.deleteUser(id)) {
            is Result.Success -> "${StringsManager.getSuccessEmoji()} ${StringsManager.getUserDeleted(id)}"
            is Result.Error -> "${StringsManager.getErrorEmoji()} ${StringsManager.getUserDeleted(id)}: ${result.getErrorMessage()}"
        }
    }

    private fun handleListUsers(): String {
        val users = userManager.getUsers()
        return if (users.isEmpty()) {
            StringsManager.getNoUsers()
        } else {
            "${StringsManager.getNoUsers().replace("No users found", "Users (${users.size} total):")}\n" +
                    users.joinToString("\n") { "${StringsManager.getUserEmoji()} $it" }
        }
    }

    private fun handleFindUserById(id: Int): String {
        return when (val result = userManager.getUserById(id)) {
            is Result.Success -> "${StringsManager.getSearchEmoji()} ${StringsManager.getUserFound(result.data.toString())}"
            is Result.Error -> "${StringsManager.getErrorEmoji()} ${StringsManager.getUserNotFound(id)}"
        }
    }

    private fun handleSortUsersByName(): String {
        val users = userManager.getUsersSortedByName()
        return if (users.isEmpty()) {
            StringsManager.getNoUsers()
        } else {
            "${StringsManager.getUsersSorted(users.size)}\n" +
                    users.joinToString("\n") { "${StringsManager.getUserEmoji()} $it" }
        }
    }

    private fun handleFilterByDomain(domain: String): String {
        val users = userManager.getUsersByEmailDomain(domain)
        return if (users.isEmpty()) {
            StringsManager.getNoUsers()
        } else {
            "${StringsManager.getUsersFiltered(domain, users.size)}\n" +
                    users.joinToString("\n") { "${StringsManager.getUserEmoji()} $it" }
        }
    }

    private fun handleSearchByName(query: String): String {
        val users = userManager.searchUsersByName(query)
        return if (users.isEmpty()) {
            StringsManager.getNoUsers()
        } else {
            "${StringsManager.getUsersSearched(query, users.size)}\n" +
                    users.joinToString("\n") { "${StringsManager.getUserEmoji()} $it" }
        }
    }

    private fun handleStats(): String {
        val totalUsers = userManager.getUserCount()
        val domainStats = getDomainStats()

        return "${StringsManager.getStatsEmoji()} ${StringsManager.getStatsTitle()}\n" +
                "${StringsManager.getTotalUsers(totalUsers)}\n" +
                "${StringsManager.getDomainStats(domainStats)}"
    }

    private fun getDomainStats(): String {
        val users = userManager.getUsers()
        val domainCounts = users.groupingBy { user ->
            user.email.substringAfter('@')
        }.eachCount()

        return domainCounts.entries.joinToString(", ") { (domain, count) ->
            "$domain: $count"
        }
    }

    private fun Result.Error.getErrorMessage(): String {
        return when (this) {
            is Result.Error.DuplicateEmail -> StringsManager.getDuplicateEmailError()
            is Result.Error.DuplicateId -> StringsManager.getDuplicateIdError()
            is Result.Error.InvalidEmail -> StringsManager.getInvalidEmailError()
            is Result.Error.UserNotFound -> StringsManager.getUserNotFoundError()
        }
    }
}