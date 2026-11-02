package com.aquasense.app.data

import android.util.Base64
import com.aquasense.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AquaSenseApi(
    private val baseUrl: String = BuildConfig.AQUASENSE_API_URL,
    private val accessToken: String? = null,
) {
    suspend fun listDevices(): List<Device> {
        val response = request("GET", "/v1/devices")
        return response.getJSONArray("devices").toDevices()
    }

    suspend fun dashboard(deviceId: String): DashboardSnapshot {
        val json = request("GET", "/v1/dashboard/$deviceId")
        return json.toDashboard()
    }

    suspend fun guestSessions(deviceId: String): List<DispenseSession> {
        val response = request("GET", "/v1/sessions/guests?device_id=$deviceId&limit=25")
        return response.getJSONArray("sessions").toSessions()
    }

    suspend fun claimDevice(deviceId: String): Device =
        request("POST", "/v1/devices/claim", JSONObject().put("device_id", deviceId)).toDevice()

    suspend fun sendCommand(deviceId: String, command: String): CommandResult {
        val json = request(
            "POST",
            "/v1/devices/$deviceId/command",
            JSONObject().put("command", command),
        )
        return CommandResult(json.getString("command_id"), json.getString("status"))
    }

    private suspend fun request(method: String, path: String, body: JSONObject? = null): JSONObject =
        withContext(Dispatchers.IO) {
            if (baseUrl.isBlank()) throw ApiException("Add the backend URL before connecting.")
            val connection = (URL(baseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection)
            try {
                connection.requestMethod = method
                connection.connectTimeout = 8_000
                connection.readTimeout = 8_000
                connection.setRequestProperty("Accept", "application/json")
                if (!accessToken.isNullOrBlank()) {
                    connection.setRequestProperty("Authorization", "Bearer $accessToken")
                }
                if (body != null) {
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.outputStream.use { it.write(body.toString().toByteArray()) }
                }

                val code = connection.responseCode
                val input = if (code in 200..299) connection.inputStream else connection.errorStream
                val payload = input?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (code !in 200..299) {
                    throw ApiException(readError(payload, code))
                }
                if (payload.isBlank()) JSONObject() else JSONObject(payload)
            } catch (error: ApiException) {
                throw error
            } catch (error: IOException) {
                throw ApiException("Could not reach AquaSense. Check the server and network.")
            } finally {
                connection.disconnect()
            }
        }

    private fun readError(payload: String, code: Int): String = runCatching {
        val json = JSONObject(payload)
        json.optJSONObject("error")?.optString("message")
            ?.takeIf { it.isNotBlank() }
            ?: json.optString("message").takeIf { it.isNotBlank() }
            ?: "Request failed ($code)"
    }.getOrDefault("Request failed ($code)")
}

class AuthRepository(
    private val supabaseUrl: String = BuildConfig.SUPABASE_URL,
    private val anonKey: String = BuildConfig.SUPABASE_ANON_KEY,
) {
    suspend fun signIn(email: String, password: String): AuthResult =
        authRequest("/auth/v1/token?grant_type=password", JSONObject().apply {
            put("email", email)
            put("password", password)
        }).toAuthResult(email.substringBefore('@'))

    suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        val json = authRequest("/auth/v1/signup", JSONObject().apply {
            put("email", email)
            put("password", password)
            put("data", JSONObject().put("display_name", displayName))
        })
        val token = json.optString("access_token")
        if (token.isBlank()) {
            throw ApiException("Account created. Confirm your email, then sign in.")
        }
        return json.toAuthResult(displayName)
    }

    private suspend fun authRequest(path: String, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        if (supabaseUrl.isBlank() || anonKey.isBlank()) {
            throw ApiException("Supabase is not configured yet. Use the demo mode to explore AquaSense.")
        }
        val connection = URL(supabaseUrl.trimEnd('/') + path).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            connection.doOutput = true
            connection.setRequestProperty("apikey", anonKey)
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = connection.responseCode
            val input = if (code in 200..299) connection.inputStream else connection.errorStream
            val payload = input?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw ApiException(readAuthError(payload, code))
            JSONObject(payload)
        } catch (error: ApiException) {
            throw error
        } catch (_: IOException) {
            throw ApiException("Could not reach Supabase. Check your connection.")
        } finally {
            connection.disconnect()
        }
    }

    private fun readAuthError(payload: String, code: Int): String = runCatching {
        val json = JSONObject(payload)
        json.optString("error_description").ifBlank { json.optString("msg") }
            .ifBlank { "Authentication failed ($code)" }
    }.getOrDefault("Authentication failed ($code)")
}

private fun JSONArray.toDevices(): List<Device> = List(length()) { getJSONObject(it).toDevice() }

private fun JSONArray.toSessions(): List<DispenseSession> = List(length()) { getJSONObject(it).toSession() }

private fun JSONObject.toDevice() = Device(
    id = getString("id"),
    ownerId = optNullableString("owner_id"),
    name = optString("name", "My Purifier"),
    status = optString("status", "offline"),
    tankLevelMl = optNullableInt("tank_level_ml"),
    currentTds = optNullableInt("current_tds"),
    filterLifeLiters = optNullableDouble("filter_life_liters"),
    firmwareVersion = optNullableString("firmware_version"),
    lastSeenAt = optNullableString("last_seen_at"),
    role = optString("role", "member"),
)

private fun JSONObject.toDashboard() = DashboardSnapshot(
    deviceId = getString("device_id"),
    name = optString("name", "My Purifier"),
    status = optString("status", "offline"),
    tankLevelMl = optNullableInt("tank_level_ml"),
    currentTds = optNullableInt("current_tds"),
    ph = optNullableDouble("current_ph"),
    filterLifeLiters = optNullableDouble("filter_life_liters"),
    firmwareVersion = optNullableString("firmware_version"),
    lastSeenAt = optNullableString("last_seen_at"),
    waterConsumedTodayMl = optInt("water_consumed_today_ml", 0),
    waterConsumedWeekMl = optInt("water_consumed_week_ml", 0),
    sessionCountToday = optInt("session_count_today", 0),
)

private fun JSONObject.toSession() = DispenseSession(
    id = getString("id"),
    deviceId = getString("device_id"),
    userId = optNullableString("user_id"),
    volumeMl = optInt("volume_ml", 0),
    avgTdsOut = optNullableInt("avg_tds_out"),
    temperatureC = optNullableDouble("temperature_c"),
    startTs = getString("start_ts"),
    endTs = getString("end_ts"),
)

private fun JSONObject.toAuthResult(fallbackName: String): AuthResult {
    val user = optJSONObject("user")
    val metadata = user?.optJSONObject("user_metadata")
    return AuthResult(
        accessToken = getString("access_token"),
        userId = user?.optString("id").orEmpty(),
        displayName = metadata?.optString("display_name")?.takeIf { it.isNotBlank() } ?: fallbackName,
    )
}

private fun JSONObject.optNullableString(key: String): String? = optString(key).takeIf { it.isNotBlank() && it != "null" }
private fun JSONObject.optNullableInt(key: String): Int? = if (isNull(key)) null else optInt(key)
private fun JSONObject.optNullableDouble(key: String): Double? = if (isNull(key)) null else optDouble(key)
