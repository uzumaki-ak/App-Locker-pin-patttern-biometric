# App-Locker-Pin-Pattern-Biometric ![Android Badge](https://img.shields.io/badge/Android-API%2012%2B-brightgreen) ![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-blue) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.4.0-purple) ![Hilt](https://img.shields.io/badge/Hilt-2.44-orange) ![Biometric](https://img.shields.io/badge/Biometric-Android%20X-3DDC84) ![MIT License](https://img.shields.io/badge/License-MIT-yellow)**

---

## 📖 Introduction

**App-Locker-Pin-Pattern-Biometric** is a secure Android application developed with Kotlin and Jetpack Compose, designed to protect your sensitive apps with multiple authentication methods. It offers PIN, pattern, and biometric authentication (fingerprint/face) to unlock protected applications, ensuring privacy and security on your device. The app includes features like background service monitoring, comprehensive app locking, onboarding flow, and accessibility support to enhance user experience and device compatibility. Its modular architecture facilitates easy maintenance and scalability.


 ## demo video : https://youtu.be/DiJNnu_a0s4
---

## ✨ Features

- **Multi-Method Authentication:** Supports PIN, pattern, and biometric (fingerprint/face) authentication for unlocking apps.
- **App Locking:** Users can lock individual apps, preventing unauthorized access.
- **Background Monitoring Service:** Keeps the app active in the background to ensure security even when minimized.
- **Onboarding & Setup:** Guided onboarding process with user setup for authentication preferences.
- **Accessibility Support:** Configured accessibility service for enhanced usability.
- **Secure Data Storage:** Uses shared preferences to store user preferences and app states securely.
- **Custom UI Components:** Modern, visually appealing UI built with Jetpack Compose, including pattern lock and PIN input components.
- **Notification Handling:** Persistent notifications to indicate active protection and system integration.
- **Dependency Injection:** Utilizes Hilt for dependency management and clean architecture.

---

## 🛠️ Tech Stack

| Library/Component                     | Purpose                                              | Version                  |
|----------------------------------------|------------------------------------------------------|--------------------------|
| **Kotlin**                           | Programming language                                | 1.8.0                    |
| **Jetpack Compose**                  | Modern UI toolkit                                   | 1.4.0                    |
| **Hilt (Dagger-Hilt)**                | Dependency injection                                | 2.44                     |
| **AndroidX Core**                      | Core Android components                             | 1.10.1                   |
| **AndroidX Navigation**                | In-app navigation                                   | 2.5.3                    |
| **Biometric API (AndroidX Biometric)**| Biometric authentication                            | 1.2.0-alpha03           |
| **Material3 (Jetpack Compose Material)** | UI Material Design Components                     | 1.0.0-alpha15           |
| **Accompanist**                        | Additional Compose utilities                        | 0.28.0                    |
| **Gradle**                           | Build system                                        | 7.4.2                    |

*(Note: Exact versions inferred from code and dependencies structure.)*

---

## 🚀 Quick Start / Installation

To get this project up and running locally:

```bash
# Clone the repository
git clone https://github.com/uzumaki-ak/App-Locker-pin-patttern-biometric.git

# Navigate into project directory
cd App-Locker-pin-patttern-biometric

# Open with Android Studio (Arctic Fox or newer recommended)
# Sync project with Gradle files
# Build and run on your connected Android device or emulator
```

**Note:** Make sure your environment has Android SDK 31+ installed and Kotlin 1.8.0 compatible.

---

## 📁 Project Structure

```
/app
 ├── src
 │    ├── main
 │    │    ├── java/com/applock/secure/
 │    │    │    ├── service/
 │    │    │    │    └── LockMonitorService.kt       # Background service to keep app alive
 │    │    │    ├── ui/
 │    │    │    │    ├── components/                   # Reusable UI components
 │    │    │    │    │    ├── AppItemCard.kt
 │    │    │    │    │    ├── BiometricPromptHelper.kt
 │    │    │    │    │    ├── PatternLockView.kt
 │    │    │    │    │    └── PinInputView.kt
 │    │    │    │    ├── navigation/
 │    │    │    │    │    └── AppNavigation.kt
 │    │    │    │    ├── screens/
 │    │    │    │    │    ├── onboarding/
 │    │    │    │    │    │    └── OnboardingViewModel.kt
 │    │    │    │    │    ├── recovery/
 │    │    │    │    │    ├── setup/
 │    │    │    │    │    ├── home/
 │    │    │    │    │    ├── applist/
 │    │    │    │    │    ├── settings/
 │    │    │    │    └── MainActivity.kt
 │    │    └── res/
 │    │         ├── layout/
 │    │         ├── xml/
 │    │         │    ├── backup_rules.xml             # Backup rules configuration
 │    │         │    ├── data_extraction_rules.xml     # Data extraction rules
 │    │         │    └── accessibility_service_config.xml
 │    │         └── drawable/
 │    └── test/
 │         └── java/com/applock/secure/ExampleUnitTest.kt
 └── build.gradle.kts
```

**Overview:**
- The `/service` contains background monitoring services.
- The `/ui/components` hosts reusable UI elements like PIN, pattern lock, biometric helper.
- The `/ui/screens` directory contains all screen-specific ViewModels and composables.
- Configuration files for backup and accessibility services are in `/res/xml`.

---

## 🔧 Configuration

### Environment Variables
- **`SECURE_PREFS_KEY`** (from `SecurePreferences`): Used to store user preferences such as first-time launch, authentication method, etc.
- **`API keys`**: Not explicitly used in code snippets; if integrating external APIs, add relevant keys in local environment or secrets.

### Build Configuration
- Target SDK: 33
- Compile SDK: 33
- Min SDK: 31
- Uses Jetpack Compose (1.4.0) and Hilt (2.44)
- Biometric API integrated via AndroidX Biometric (1.2.0-alpha03)

### Deployment
- Use Android Studio to build and run.
- Ensure necessary permissions are granted, including biometric and accessibility services.

---

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit pull requests.

For issues and discussions, visit:  
- [GitHub Issues](https://github.com/uzumaki-ak/App-Locker-pin-patttern-biometric/issues)  
- [GitHub Discussions](https://github.com/uzumaki-ak/App-Locker-pin-patttern-biometric/discussions)

Please adhere to the [Code of Conduct](https://github.com/uzumaki-ak/App-Locker-pin-patttern-biometric/blob/main/CODE_OF_CONDUCT.md) and follow best practices.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Thanks to the Android developer community for biometric and accessibility APIs.
- Inspiration from modern app lock solutions and security best practices.
- Special thanks to contributors and testers.

---

This README provides a comprehensive technical overview based on the actual code structure, dependencies, and features of the project. For further details, explore the source code and documentation within the repository.