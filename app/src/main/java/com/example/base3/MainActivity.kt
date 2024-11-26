package com.example.base3

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.base3.ui.theme.Base3Theme
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Base3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    //color = MaterialTheme.colorScheme.background
                    color = Color.Gray
                ) {
                    Login()
                }
            }
        }
    }

    fun darkenColor(color: Color, percent: Float = 0.2f): Color {
        val r = color.red * (1 - percent)
        val g = color.green * (1 - percent)
        val b = color.blue * (1 - percent)
        return Color(r, g, b)
    }

    @Composable
    fun MyButton(
        modik: Modifier,
        text: String,
        color: Color,
        shape: Shape = RoundedCornerShape(0.dp),
        onCLick: () -> Unit
    ) {
        Button(
            modifier = modik,
            colors = ButtonDefaults.buttonColors(color),
            shape = shape,
            onClick = { onCLick() }
        ) {
            Text(text = text)
        }
    }

    @Composable
    fun MyTextField(
        value: String,
        placeholder: String = "",
        visualTransform: VisualTransformation = VisualTransformation.None,
        input: KeyboardOptions = KeyboardOptions.Default,
        onChange: (String) -> Unit
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(text = placeholder) },
            keyboardOptions = input,
            visualTransformation = visualTransform,
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun Login() {
        val compScope = rememberCoroutineScope()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var isLogin by remember { mutableStateOf(false) }

        LaunchedEffect(isLogin) {
            if (isLogin) {
                Toast.makeText(this@MainActivity, "Вы вошли", Toast.LENGTH_SHORT).show()
                setContent {
                    Base3Theme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Main()
                        }
                    }
                }
            }
        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    //Размеры регистрационного окошка можете указать
//                    .fillMaxWidth(0.9f)
//                    .fillMaxHeight(0.4f)
                    // Тут указан размер на весь экран
                    .fillMaxSize()
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp)),
            )
        }

        Column(Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 40.dp),


            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MyTextField(
                value = email,
                placeholder = "email",
                visualTransform = VisualTransformation.None,
                input = KeyboardOptions(keyboardType = KeyboardType.Email),
            ) { email = it }
            Spacer(Modifier.height(10.dp))
            MyTextField(
                value = password,
                placeholder = "password",
                visualTransform = PasswordVisualTransformation(),
                input = KeyboardOptions(keyboardType = KeyboardType.Password),
            ) { password = it }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth()) {
                MyButton(
                    text = "SignIn",
                    color = Color(0xFF4CAF50),
                    modik = Modifier.fillMaxWidth(0.5f).height(40.dp)
                ) {
                    compScope.launch {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            if (loginUser(email, password)) {
                                isLogin = true
                            } else {
                                Toast.makeText(this@MainActivity, "Неверный email или пароль!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Email или пароль не введены!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Spacer(Modifier.width(5.dp))
                MyButton(
                    text = "SignUp",
                    color = Color.LightGray,
                    modik = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    val name = "user_${Random.nextInt(1000000)}"
                    compScope.launch {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            if (!isUserExists(email)) {

                                val newUser: User = User(
                                    name = name,
                                    email = email,
                                    password = password,
                                    createdAt = translateTimeToSupabase(),
                                    accessLevel = 0
                                )

                                if (addUser(newUser)) {
                                    isLogin = true
                                } else {
                                    Toast.makeText(this@MainActivity, "Пасхалко", Toast.LENGTH_SHORT).show()
                                }

                            } else {
                                Toast.makeText(this@MainActivity, "Такой Email уже зарегистрирован!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Email или пароль не введены!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun Main() {

        val compScope = rememberCoroutineScope()
        var users by remember { mutableStateOf<List<User>>(emptyList()) }

        LaunchedEffect(Unit) {
            compScope.launch {
                users = getUsers().sortedBy { it.id }
            }
        }

        Column(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                // Добавляем заголовок
                Text(
                    text = "Вы вошли",

                    modifier = Modifier
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally) 
                    // Центрируем текст
                )
            LazyColumn(Modifier.padding(15.dp).weight(1f)) {
                items(users) { user ->
                    Box(
                        Modifier
                            .background(
                                color = darkenColor(Color.White, 0.125f),
                                shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()

                            .padding(10.dp),
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                        ) {

                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)

            ) {
                MyButton(
                    text = "Обновить",
                    color = Color.Gray,
                    modik = Modifier.height(40.dp)

                ) {
                    compScope.launch {
                        users = getUsers().sortedBy { it.id }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}}


