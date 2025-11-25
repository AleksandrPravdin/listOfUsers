package domain.util


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    sealed class Error : Result<Nothing>() {
        object UserNotFound : Error()
        object DuplicateEmail : Error()
        object DuplicateId : Error()
        object InvalidEmail : Error()
    }
}