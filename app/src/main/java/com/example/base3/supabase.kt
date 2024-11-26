package com.example.base3

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime

const val key = ""
const val url = ""

val supaClient = createSupabaseClient(
    supabaseUrl = url,
    supabaseKey = key
) {
    install(Postgrest)
}

fun translateTimeToSupabase(): String {
    val now = ZonedDateTime.now(ZoneId.of("UTC"))
    return "${now.year}-${now.monthValue}-${now.dayOfMonth} ${now.hour}:${now.minute}:${now.second}+00"
}

fun translateTimeToKotlin(supaTime: String): String {
    return ZonedDateTime.parse(supaTime).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().toString()
}

@Serializable
data class User (
    @SerialName("user_id") val id: Int? = null,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("access_level") val accessLevel: Int = 0
)

suspend fun getUsers(): List<User> {
    return supaClient.from("users").select().decodeList<User>()
}

suspend fun isUserExists(email: String): Boolean {
    var isOk: Boolean = true
    try {
        val existingUser = supaClient.from("users").select(Columns.raw("email")) {
            filter { eq("email", email) }
        }.decodeSingleOrNull<User>()

        isOk = existingUser != null

    } catch (e: Exception) {
        println("Ошибка проверки существования пользователя: ${e.message}")
    }
    return isOk
}

suspend fun addUser(user: User): Boolean {
    return try {
        supaClient.from("users").insert(user)
        true
    } catch (e: Exception) {
        println("Ошибка добавления пользователя: ${e.message}")
        false
    }
}

suspend fun loginUser(email: String, password: String): Boolean {
    return try {
        val users = supaClient
            .from("users")
            .select(Columns.raw("email, password")) {
                filter {
                    eq("email", email);
                }
            }.decodeAs<List<Map<String, String>>>()

        if (users.isNotEmpty()) {
            val storedPassword = users[0]["password"]
            if (storedPassword == password) {
                true
            } else {
                false
            }
        } else {
            false
        }
    } catch (e: Exception) {
        println("Ошибка входа: ${e.message}")
        false
    }
}