package data.repository

import domain.model.User
import domain.util.Result
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = UserRepositoryImpl()
    }

    @Test
    @DisplayName("addUser should prevent duplicate IDs")
    fun `addUser should fail when duplicate id`() {
        val user1 = User(1, "Ivan", "ivan1@test.com")
        val user2 = User(1, "Jane", "ivan2@test.com")
        repository.addUser(user1)

        val result = repository.addUser(user2)

        assertTrue(result is Result.Error.DuplicateId)
        assertEquals(1, repository.getUsers().size)
    }

    @Test
    @DisplayName("getUsersSortedByName should return alphabetically sorted list")
    fun `getUsersSortedByName should return sorted list`() {
        repository.addUser(User(3, "Ivan", "ivan@test.com"))
        repository.addUser(User(1, "Alice", "alice@test.com"))
        repository.addUser(User(2, "Bob", "bob@test.com"))

        val sorted = repository.getUsersSortedByName()

        assertEquals(listOf("Alice", "Bob", "Ivan"), sorted.map { it.name })
        assertEquals(1, sorted[0].id)
        assertEquals(2, sorted[1].id)
        assertEquals(3, sorted[2].id)
    }

    @Test
    @DisplayName("getUsersByEmailDomain should filter correctly")
    fun `getUsersByEmailDomain should filter correctly`() {
        repository.addUser(User(1, "Ivan", "ivan@gmail.com"))
        repository.addUser(User(2, "Jane", "jane@yahoo.com"))
        repository.addUser(User(3, "Bob", "bob@gmail.com"))

        val gmailUsers = repository.getUsersByEmailDomain("gmail.com")
        val yahooUsers = repository.getUsersByEmailDomain("yahoo.com")
        val emptyUsers = repository.getUsersByEmailDomain("nonexistent.com")

        assertEquals(2, gmailUsers.size)
        assertTrue(gmailUsers.all { it.email.endsWith("@gmail.com") })

        assertEquals(1, yahooUsers.size)
        assertTrue(yahooUsers.all { it.email.endsWith("@yahoo.com") })

        assertEquals(0, emptyUsers.size)
    }

    @Test
    @DisplayName("deleteUser should remove user and free up email")
    fun `deleteUser should work correctly`() {
        repository.addUser(User(1, "Ivan", "ivan@test.com"))
        repository.addUser(User(2, "Jane", "jane@test.com"))

        val deleteResult = repository.deleteUser(1)

        assertTrue(deleteResult is Result.Success)
        assertEquals(1, repository.getUsers().size)
        assertNull(repository.getUserById(1))
        assertNotNull(repository.getUserById(2))

        val reuseEmailResult = repository.addUser(User(3, "New User", "ivan@test.com"))
        assertTrue(reuseEmailResult is Result.Success,
            "Should allow reusing email of deleted user")
    }
}