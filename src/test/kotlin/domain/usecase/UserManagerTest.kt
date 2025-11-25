package domain.usecase

import data.repository.UserRepositoryImpl
import domain.util.Result
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserManagerTest {

    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var userManager: UserManager

    @BeforeEach
    fun setUp() {
        userRepository = UserRepositoryImpl()
        userManager = UserManager(userRepository)
    }

    @Test
    @DisplayName("createUser should success with valid data and generate unique ID")
    fun `createUser with valid data should return success`() {
        val result = userManager.createUser("Ivan Trankov", "ivan@example.com")

        assertTrue(result is Result.Success)
        val user = (result as Result.Success).data
        assertEquals("Ivan Trankov", user.name)
        assertEquals("ivan@example.com", user.email)
        assertEquals(1, user.id)

        assertEquals(1, userManager.getUserCount())
        assertEquals(user, userManager.getUserById(1).let {
            (it as Result.Success).data
        })
    }

    @Test
    @DisplayName("createUser should fail with duplicate email")
    fun `createUser with duplicate email should return error`() {
        userManager.createUser("Ivan Trankov", "ivan@example.com")

        val result = userManager.createUser("Jane Smith", "ivan@example.com")

        assertTrue(result is Result.Error.DuplicateEmail)
        assertEquals(1, userManager.getUserCount())
    }

    @Test
    @DisplayName("createUser should fail with invalid email format")
    fun `createUser with invalid email should return error`() {
        val invalidEmails = listOf(
            "invalid-email",
            "invalid@",
            "@domain.com",
            "invalid@domain",
            "invalid@domain."
        )

        invalidEmails.forEach { email ->
            val result = userManager.createUser("Test User", email)
            assertTrue(result is Result.Error.InvalidEmail,
                "Expected InvalidEmail for: $email")
        }

        assertEquals(0, userManager.getUserCount())
    }

    @Test
    @DisplayName("updateUser should success and maintain data integrity")
    fun `updateUser should work correctly`() {
        userManager.createUser("Ivan Trankov", "john@example.com")
        userManager.createUser("Jane Smith", "jane@example.com")

        val updateResult = userManager.updateUser(1, "Ivan Updated", "ivan.updated@example.com")

        assertTrue(updateResult is Result.Success)
        val updatedUser = (updateResult as Result.Success).data
        assertEquals("Ivan Updated", updatedUser.name)
        assertEquals("ivan.updated@example.com", updatedUser.email)
        assertEquals(1, updatedUser.id)

        val retrievedUser = userManager.getUserById(1).let { (it as Result.Success).data }
        assertEquals("Ivan Updated", retrievedUser.name)
        assertEquals("ivan.updated@example.com", retrievedUser.email)

        val jane = userManager.getUserById(2).let { (it as Result.Success).data }
        assertEquals("Jane Smith", jane.name)
        assertEquals("jane@example.com", jane.email)
    }
}