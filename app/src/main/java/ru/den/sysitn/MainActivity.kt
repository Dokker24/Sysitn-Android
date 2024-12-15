package ru.den.sysitn

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.opencv.android.OpenCVLoader
import ru.den.sysitn.screens.Home
import ru.den.sysitn.ui.theme.SysitnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (OpenCVLoader.initLocal()) {
            Log.d("OpenCV", "Work")
        } else {
            Log.e("OpenCV", "Error")
        }

        setContent {
            SysitnTheme {
                Home()
            }
        }
    }
}