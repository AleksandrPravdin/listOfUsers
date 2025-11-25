package domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String
) {
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(id > 0) { "ID must be positive" }
        require(email.isNotBlank()) { "Email cannot be blank" }
    }
}