package com.example.fingerprintbiometric

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.Locale

@Composable
fun BiometricManager(
    context: Context,
    onSuccess: () -> Unit,
    onHardwareUnavailable: () -> Unit,
    onNoBiometrics: () -> Unit,
    onRequestNewCredentials: () -> Unit,
    onSecurityUpdateRequired: () -> Unit,
    onVersionNotSupported: () -> Unit
) {

    val biometricManager = BiometricManager.from(context)

    when(biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
        BiometricManager.BIOMETRIC_SUCCESS -> onSuccess()
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> onHardwareUnavailable()
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> onNoBiometrics()
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> onRequestNewCredentials()
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> onSecurityUpdateRequired()
        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> onVersionNotSupported()
        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> onVersionNotSupported()
    }
}


@Composable
fun PromptDialog(
    activity: FragmentActivity,
    onAuthError: (String) -> Unit,
    onAuthSuccess: (String) -> Unit,
    onRetry: () -> Unit
) {

    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int,
                                               errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                val err =  errString.toString().lowercase(Locale.ROOT)

                if (err.contains("cancel"))
                    onAuthError("Please use the normal login")
                else onAuthError(errString.toString())
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess("Auth success")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onRetry()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Login")
        .setSubtitle("Login using your fingerprint")
        .setNegativeButtonText("Cancel")
        .build()

    biometricPrompt.authenticate(promptInfo)
}