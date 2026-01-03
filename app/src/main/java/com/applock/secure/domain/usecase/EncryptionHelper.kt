package com.applock.secure.domain.usecase

import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for encryption operations
 * Provides SHA-256 hashing for PINs and Patterns
 */
@Singleton
class EncryptionHelper @Inject constructor() {

    /**
     * Hash input string using SHA-256
     * Used for PIN and Pattern storage
     */
    fun hashSHA256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Verify hash matches input
     */
    fun verifyHash(input: String, storedHash: String): Boolean {
        val inputHash = hashSHA256(input)
        return inputHash == storedHash
    }
}