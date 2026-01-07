package com.applock.secure.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.applock.secure.data.model.AuthMethod
import com.applock.secure.util.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        AppConstants.PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // First time
    fun isFirstTime(): Boolean = prefs.getBoolean(AppConstants.KEY_FIRST_TIME, true)
    fun setFirstTime(isFirst: Boolean) = prefs.edit().putBoolean(AppConstants.KEY_FIRST_TIME, isFirst).apply()

    // Auth method
    fun getAuthMethod(): AuthMethod {
        val methodString = prefs.getString(AppConstants.KEY_AUTH_METHOD, AuthMethod.NONE.name)
        return AuthMethod.valueOf(methodString ?: AuthMethod.NONE.name)
    }

    fun setAuthMethod(method: AuthMethod) {
        prefs.edit().putString(AppConstants.KEY_AUTH_METHOD, method.name).apply()
    }

    // PIN hash
    fun getPinHash(): String? = prefs.getString(AppConstants.KEY_PIN_HASH, null)
    fun setPinHash(hash: String) = prefs.edit().putString(AppConstants.KEY_PIN_HASH, hash).apply()

    // Pattern hash
    fun getPatternHash(): String? = prefs.getString(AppConstants.KEY_PATTERN_HASH, null)
    fun setPatternHash(hash: String) = prefs.edit().putString(AppConstants.KEY_PATTERN_HASH, hash).apply()

    // Recovery PIN hash
    fun getRecoveryPinHash(): String? = prefs.getString(AppConstants.KEY_RECOVERY_PIN_HASH, null)
    fun setRecoveryPinHash(hash: String) = prefs.edit().putString(AppConstants.KEY_RECOVERY_PIN_HASH, hash).apply()

    // Security Question
    fun getSecurityQuestion(): String? = prefs.getString("security_question", null)
    fun setSecurityQuestion(question: String) = prefs.edit().putString("security_question", question).apply()

    // Security Answer Hash
    fun getSecurityAnswerHash(): String? = prefs.getString("security_answer_hash", null)
    fun setSecurityAnswerHash(hash: String) = prefs.edit().putString("security_answer_hash", hash).apply()

    // Biometric
    fun isBiometricEnabled(): Boolean = prefs.getBoolean(AppConstants.KEY_BIOMETRIC_ENABLED, false)
    fun setBiometricEnabled(enabled: Boolean) = prefs.edit().putBoolean(AppConstants.KEY_BIOMETRIC_ENABLED, enabled).apply()

    // Service enabled
    fun isServiceEnabled(): Boolean = prefs.getBoolean(AppConstants.KEY_SERVICE_ENABLED, false)
    fun setServiceEnabled(enabled: Boolean) = prefs.edit().putBoolean(AppConstants.KEY_SERVICE_ENABLED, enabled).apply()

    // Clear all
    fun clearAll() = prefs.edit().clear().apply()
}