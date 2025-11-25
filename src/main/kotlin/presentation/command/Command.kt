package presentation.command

sealed class Command {
    object Exit : Command()
    object Help : Command()
    object ListUsers : Command()
    object SortUsersByName : Command()
    object Stats : Command()

    data class CreateUser(val name: String, val email: String) : Command()
    data class UpdateUser(val id: Int, val name: String, val email: String) : Command()
    data class DeleteUser(val id: Int) : Command()
    data class FindUserById(val id: Int) : Command()
    data class FilterByDomain(val domain: String) : Command()
    data class SearchByName(val query: String) : Command()
}