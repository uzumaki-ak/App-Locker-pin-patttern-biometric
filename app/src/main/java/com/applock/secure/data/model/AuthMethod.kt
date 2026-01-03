package com.applock.secure.data.model

/**
 * Enum representing available authentication methods
 */
enum class AuthMethod {
    PIN,          // 4-8 digit numeric PIN
    PATTERN,      // Pattern lock
    BIOMETRIC,    // Fingerprint or Face ID
    NONE          // No authentication (initial state)
}