package domain.repository

import domain.model.User
import domain.util.Result

interface UserRepository {
    fun addUser(user: User): Result<Unit>
    fun getUsers(): List<User>
    fun getUserById(id: Int): User?
    fun updateUser(user: User): Result<Unit>
    fun deleteUser(id: Int): Result<Unit>
    fun getUsersSortedByName(): List<User>
    fun getUsersByEmailDomain(domain: String): List<User>
    fun isEmailUnique(email: String): Boolean
}