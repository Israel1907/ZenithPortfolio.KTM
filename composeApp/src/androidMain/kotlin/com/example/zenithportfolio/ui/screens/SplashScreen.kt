package com.example.zenithportfolio.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.material3.MaterialTheme
import com.example.zenithportfolio.ui.theme.Accent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(onSplashFinished: () -> Unit){
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onSplashFinished()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            Text(
                text = "💎",
                fontSize = 72.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zenith",
                color = Accent,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Portfolio",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tu portafolio crypto",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}