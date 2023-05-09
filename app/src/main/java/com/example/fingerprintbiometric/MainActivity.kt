package com.example.fingerprintbiometric

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.fingerprintbiometric.ui.theme.FingerprintBiometricTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FingerprintBiometricTheme {
                // A surface container using the 'background' color from the theme
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    val context = LocalContext.current
                    val activity = (context.getActivity() as FragmentActivity)
                    var showDialog by remember { mutableStateOf(false) }
                    var isClicked by remember { mutableStateOf(false) }
                    var isSuccess by remember { mutableStateOf(false) }
                    var message by remember { mutableStateOf("") }

                    Button(
                        onClick = {
                            isClicked = true
                        }
                    ) {
                        Text(text = "login")
                    }

                    if (isClicked){

                        BiometricManager(
                            context = context,
                            onSuccess = { isSuccess = true },
                            onHardwareUnavailable = {
                                showDialog = true
                                message = "Your phone does not have a finger print sensor"
                                                    },
                            onNoBiometrics = {
                                showDialog = true
                                message = "No biometrics found"
                                             },
                            onRequestNewCredentials = {
                                showDialog = true
                                message = "request new credentials"
                                                      },
                            onSecurityUpdateRequired = {
                                showDialog = true
                                message = "update your security"
                                                       },
                            onVersionNotSupported = {
                                showDialog = true
                                message = "Your android version does not support"
                            }
                        )
                    }

                    if (isSuccess) {

                        PromptDialog(
                            activity = activity,
                            onAuthSuccess = { toast(it) },
                            onAuthError = {
                                showDialog = true
                                message = it
                                isSuccess = false
                                isClicked = false
                            },
                            onRetry = {
                                showDialog = false
                            }
                        )
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text(text = "Biometric Checker")
                            },
                            text = {
                                Text(text = message)
                            },
                            confirmButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text(text = "Use normal login")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    fun Context.getActivity(): AppCompatActivity? = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }


    private fun toast(
        message: String
    ){

        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_LONG
        ).show()

    }
}