package domain.util

import java.util.*

object StringsManager {
    private val bundle: ResourceBundle = ResourceBundle.getBundle("strings/messages", Locale.ENGLISH)

    fun getAppTitle() = getString("app.title")
    fun getAppHelp() = getString("app.help")
    fun getGoodbye() = getString("app.goodbye")

    fun getUnknownCommand() = getString("command.unknown")
    fun getCommandError(message: String) = getString("command.error", message)

    fun getUserCreated(user: String) = getString("user.created", user)
    fun getUserUpdated(user: String) = getString("user.updated", user)
    fun getUserDeleted(id: Int) = getString("user.deleted", id.toString())
    fun getUserFound(user: String) = getString("user.found", user)
    fun getUserNotFound(id: Int) = getString("user.not_found", id.toString())
    fun getNoUsers() = getString("user.no_users")
    fun getUsersSorted(count: Int) = getString("user.sorted", count.toString())
    fun getUsersFiltered(domain: String, count: Int) = getString("user.filtered", domain, count.toString())
    fun getUsersSearched(query: String, count: Int) = getString("user.searched", query, count.toString())

    fun getDuplicateEmailError() = getString("error.duplicate_email")
    fun getDuplicateIdError() = getString("error.duplicate_id")
    fun getInvalidEmailError() = getString("error.invalid_email")
    fun getUserNotFoundError() = getString("error.user_not_found")

    fun getStatsTitle() = getString("stats.title")
    fun getTotalUsers(count: Int) = getString("stats.total_users", count.toString())
    fun getDomainStats(stats: String) = getString("stats.domain_stats", stats)

    fun getHelpText() = getString("help.text")

    fun getUserEmoji() = getString("emoji.user")
    fun getSearchEmoji() = getString("emoji.find")
    fun getStatsEmoji() = getString("emoji.stats")
    fun getSuccessEmoji() = getString("emoji.ok")
    fun getErrorEmoji() = getString("emoji.error")

    private fun getString(key: String, vararg args: String): String {
        return try {
            val template = bundle.getString(key)
            if (args.isEmpty()) template else {
                var result = template
                args.forEachIndexed { index, arg ->
                    result = result.replace("{$index}", arg)
                }
                result
            }
        } catch (e: MissingResourceException) {
            "[$key]"
        }
    }
}