package com.example.plantilla_sem10.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

@Composable
fun AuthScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var isAuthenticated by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    if (isAuthenticated) {
        CommentScreen(auth.currentUser?.uid ?: "")
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0B9FF)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 8.dp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (isLogin) "Iniciar Sesión" else "Registrar",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!isLogin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    passwordError = false
                                },
                                label = { Text("Confirmar Contraseña") },
                                isError = passwordError,
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (passwordError) {
                                Text(
                                    text = "Las contraseñas no coinciden.",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(top = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (!isLogin && password != confirmPassword) {
                                    passwordError = true
                                    message = "Las contraseñas no coinciden."
                                    return@Button
                                } else {
                                    passwordError = false
                                }

                                val action = if (isLogin) {
                                    auth.signInWithEmailAndPassword(email.trim(), password)
                                } else {
                                    auth.createUserWithEmailAndPassword(email.trim(), password)
                                }

                                action.addOnSuccessListener {
                                    isAuthenticated = true
                                }.addOnFailureListener {
                                    val errorCode = (it as? FirebaseAuthException)?.errorCode
                                    message = traducirErrorFirebase(errorCode)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A83F9))
                        ) {
                            Text(
                                if (isLogin) "Ingresar" else "Registrar",
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = {
                            isLogin = !isLogin
                            message = ""
                            passwordError = false
                        }) {
                            Text(
                                if (isLogin) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Inicia sesión",
                                color = Color(0xFF7A83F9)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = message, color = Color.Red)
                    }
                }
            }
        }
    }
}

fun traducirErrorFirebase(errorCode: String?): String {
    return when (errorCode) {
        "ERROR_INVALID_EMAIL" -> "El formato del correo electrónico no es válido."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está registrado."
        "ERROR_USER_NOT_FOUND" -> "Usuario no encontrado."
        "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta."
        "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres."
        else -> "Error desconocido o sin código proporcionado."
    }
}
