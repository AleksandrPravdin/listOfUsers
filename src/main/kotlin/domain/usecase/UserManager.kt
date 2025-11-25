package domain.usecase

import domain.model.User
import domain.util.Result
import domain.repository.UserRepository

class UserManager(private val userRepository: UserRepository) {

    fun createUser(name: String, email: String): Result<User> {
        if (name.isBlank()) {
            return Result.Error.InvalidEmail
        }

        if (!isValidEmail(email)) {
            return Result.Error.InvalidEmail
        }

        if (!userRepository.isEmailUnique(email)) {
            return Result.Error.DuplicateEmail
        }

        val newId = generateNextId()
        val user = User(newId, name, email)

        return userRepository.addUser(user).let { result ->
            when (result) {
                is Result.Success -> Result.Success(user)
                is Result.Error -> result
            }
        }
    }

    fun getUsers(): List<User> {
        return userRepository.getUsers()
    }

    fun getUserById(id: Int): Result<User> {
        return userRepository.getUserById(id)?.let { user ->
            Result.Success(user)
        } ?: Result.Error.UserNotFound
    }

    fun updateUser(id: Int, name: String, email: String): Result<User> {
        val existingUser = userRepository.getUserById(id)

        if (existingUser == null) {
            return Result.Error.UserNotFound
        }

        if (!isValidEmail(email)) {
            return Result.Error.InvalidEmail
        }

        if (!userRepository.isEmailUnique(email) && existingUser.email != email) {
            return Result.Error.DuplicateEmail
        }

        val updatedUser = User(id, name, email)

        return userRepository.updateUser(updatedUser).let { result ->
            when (result) {
                is Result.Success -> Result.Success(updatedUser)
                is Result.Error -> result
            }
        }
    }

    fun deleteUser(id: Int): Result<Unit> {
        return userRepository.deleteUser(id)
    }

    fun getUsersSortedByName(): List<User> {
        return userRepository.getUsersSortedByName()
    }

    fun getUsersByEmailDomain(domain: String): List<User> {
        return userRepository.getUsersByEmailDomain(domain)
    }

    fun searchUsersByName(query: String): List<User> {
        return userRepository.getUsers().filter { user ->
            user.name.contains(query, ignoreCase = true)
        }
    }

    fun getUserCount(): Int {
        return userRepository.getUsers().size
    }

    private fun generateNextId(): Int {
        val users = userRepository.getUsers()
        return if (users.isEmpty()) 1 else users.maxOf { it.id } + 1
    }

    private fun isValidEmail(email: String): Boolean {
        if (!email.contains('@')) return false

        val parts = email.split('@')
        if (parts.size != 2) return false

        val localPart = parts[0]
        val domainPart = parts[1]

        if (localPart.isBlank()) return false

        if (domainPart.isBlank()) return false
        if (!domainPart.contains('.')) return false
        if (domainPart.indexOf('.') == 0) return false
        if (domainPart.endsWith('.')) return false

        return true
    }
}