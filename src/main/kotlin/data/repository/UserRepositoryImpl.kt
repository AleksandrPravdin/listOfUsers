package data.repository

import domain.model.User
import domain.util.Result
import domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    private val users = mutableMapOf<Int, User>()
    private val emailSet = mutableSetOf<String>()

    override fun addUser(user: User): Result<Unit> = synchronized(this) {
        return when {
            users.containsKey(user.id) -> Result.Error.DuplicateId
            emailSet.contains(user.email) -> Result.Error.DuplicateEmail
            else -> {
                users[user.id] = user
                emailSet.add(user.email)
                Result.Success(Unit)
            }
        }
    }

    override fun getUsers(): List<User> = synchronized(this) {
        users.values.toList()
    }

    override fun getUserById(id: Int): User? = synchronized(this) {
        users[id]
    }

    override fun updateUser(user: User): Result<Unit> = synchronized(this) {
        return when {
            !users.containsKey(user.id) -> Result.Error.UserNotFound
            emailSet.contains(user.email) && getUserByEmail(user.email)?.id != user.id ->
                Result.Error.DuplicateEmail
            else -> {
                val oldUser = users[user.id]!!
                emailSet.remove(oldUser.email)
                users[user.id] = user
                emailSet.add(user.email)
                Result.Success(Unit)
            }
        }
    }

    override fun deleteUser(id: Int): Result<Unit> = synchronized(this) {
        return when (val user = users[id]) {
            null -> Result.Error.UserNotFound
            else -> {
                users.remove(id)
                emailSet.remove(user.email)
                Result.Success(Unit)
            }
        }
    }

    override fun getUsersSortedByName(): List<User> = synchronized(this) {
        users.values.sortedBy { it.name.lowercase() }
    }

    override fun getUsersByEmailDomain(domain: String): List<User> = synchronized(this) {
        users.values.filter { it.email.endsWith("@$domain") }
    }

    override fun isEmailUnique(email: String): Boolean = synchronized(this) {
        !emailSet.contains(email)
    }

    private fun getUserByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }
}