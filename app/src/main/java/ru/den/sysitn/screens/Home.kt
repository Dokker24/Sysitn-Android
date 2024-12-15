package ru.den.sysitn.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import kotlinx.coroutines.launch

@Composable
fun Home(
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        viewModel.imageUri = it
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            Modifier
                .padding(10.dp)
                .size(250.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF535353)
            )
        ) {
            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(viewModel.imageUri)
                        .build(),
                    modifier = Modifier
                        .size(248.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = " ok"
                )
                IconButton(
                    onClick = {
                        scope.launch {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        tint = if (viewModel.imageUri == null) Color.White else Color.Transparent,
                        contentDescription = "den"
                    )
                }
            }
        }
        Button(
            onClick = {
                if (viewModel.imageUri != null) {
                    viewModel.classify(context)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            )
        ) {
            Text(
                "Прогноз",
                color = Color.White
            )
        }
        if (viewModel.pneumoniaP != 0.0f) {
            Text(
                text = if (viewModel.pneumoniaP >= viewModel.normalP) "Пневмония" else "Норма",
                color = if (viewModel.pneumoniaP >= viewModel.normalP) Color.Red else Color.Green,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "Вероятность пневмонии: ${String.format("%.2f", viewModel.pneumoniaP * 100)}%",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Вероятность нормы: ${String.format("%.2f", viewModel.normalP * 100)}%",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
