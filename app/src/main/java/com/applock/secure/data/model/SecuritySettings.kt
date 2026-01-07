package com.applock.secure.data.model

data class SecuritySettings(
    val authMethod: AuthMethod = AuthMethod.NONE,
    val pinHash: String? = null,
    val patternHash: String? = null,
    val recoveryPinHash: String? = null,
    val securityQuestion: String? = null,
    val securityAnswerHash: String? = null,
    val biometricEnabled: Boolean = false,
    val autoLockDelay: Long = 0L
)

// Predefined security questions
object SecurityQuestions {
    val questions = listOf(
        "What was your childhood nickname?",
        "What is your mother's maiden name?",
        "What was the name of your first pet?",
        "What city were you born in?",
        "What is your favorite food?",
        "What was your first car?",
        "What is your favorite movie?",
        "What is your father's middle name?"
    )
}