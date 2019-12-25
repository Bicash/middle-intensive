package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName:String,
        email:String,
        password:String
    ):User{
        val user = User.makeUser(fullName, email = email, password = password)
        map.forEach{
            if (it.value.login == user.login)
                throw IllegalArgumentException("A user with this email already exists")
        }
        return User.makeUser(fullName, email = email, password = password)
            .also { user -> map[user.login] = user }
    }

    fun loginUser(login:String, password: String) : String?{
        return map[Regex("[-()\\s]").replace(login, "")]?.run {
            if (phone != null) {
                if (checkAccessCode(password)) this.userInfo
                else null
            } else {
                if (checkPassword(password)) this.userInfo
                else null
            }
        }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        val user = User.makeUser(fullName, phone = rawPhone)
        map.forEach{
            if (it.value.login == user.login)
                throw IllegalArgumentException("A user with this phone already exists")
        }
        return user.also { user -> map[user.login] = user }
    }

    fun requestAccessCode(login: String) {
        map.forEach{
            if (it.value.login == Regex("[-()\\s]").replace(login, ""))
                it.value.changeAccessCode()
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }
}