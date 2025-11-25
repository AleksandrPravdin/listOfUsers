package presentation.command

class CommandParser {

    fun parse(input: String): Command? {
        val trimmedInput = input.trim()
        if (trimmedInput.isEmpty()) return null

        val firstSpaceIndex = trimmedInput.indexOf(' ')
        val commandWord = if (firstSpaceIndex == -1) {
            trimmedInput
        } else {
            trimmedInput.substring(0, firstSpaceIndex)
        }

        return when (commandWord.lowercase()) {
            "exit" -> Command.Exit
            "help" -> Command.Help
            "list" -> Command.ListUsers
            "create" -> parseCreateCommand(trimmedInput)
            "update" -> parseUpdateCommand(trimmedInput)
            "delete" -> parseDeleteCommand(trimmedInput)
            "find" -> parseFindCommand(trimmedInput)
            "sort" -> Command.SortUsersByName
            "filter" -> parseFilterCommand(trimmedInput)
            "search" -> parseSearchCommand(trimmedInput)
            "stats" -> Command.Stats
            else -> null
        }
    }

    private fun parseCreateCommand(input: String): Command? {
        val pattern = """create\s+"([^"]+)"\s+(\S+)""".toRegex()
        val match = pattern.find(input)

        return match?.let {
            val (name, email) = it.destructured
            if (name.isNotBlank() && email.isNotBlank()) {
                Command.CreateUser(name, email)
            } else {
                null
            }
        }
    }

    private fun parseUpdateCommand(input: String): Command? {
        val pattern = """update\s+(\d+)\s+"([^"]+)"\s+(\S+)""".toRegex()
        val match = pattern.find(input)

        return match?.let {
            val (idStr, name, email) = it.destructured
            val id = idStr.toIntOrNull()
            if (id != null && name.isNotBlank() && email.isNotBlank()) {
                Command.UpdateUser(id, name, email)
            } else {
                null
            }
        }
    }

    private fun parseDeleteCommand(input: String): Command? {
        val pattern = """delete\s+(\d+)""".toRegex()
        val match = pattern.find(input)

        return match?.let {
            val id = it.groupValues[1].toIntOrNull()
            id?.let { Command.DeleteUser(it) }
        }
    }

    private fun parseFindCommand(input: String): Command? {
        val pattern = """find\s+(\d+)""".toRegex()
        val match = pattern.find(input)

        return match?.let {
            val id = it.groupValues[1].toIntOrNull()
            id?.let { Command.FindUserById(it) }
        }
    }

    private fun parseFilterCommand(input: String): Command? {
        val pattern = """filter\s+(\S+)""".toRegex()
        val match = pattern.find(input)

        return match?.let {
            val domain = it.groupValues[1]
            if (domain.isNotBlank()) Command.FilterByDomain(domain) else null
        }
    }

    private fun parseSearchCommand(input: String): Command? {
        val pattern = """search\s+(.+)""".toRegex()
        val match = pattern.find(input)

        return match?.let {
            var query = it.groupValues[1].trim()
            query = query.removeSurrounding("\"")
            if (query.isNotBlank()) Command.SearchByName(query) else null
        }
    }
}