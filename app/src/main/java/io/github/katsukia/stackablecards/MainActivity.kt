package io.github.katsukia.stackablecards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.katsukia.stackablecards.ui.theme.SampleMaterialTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleMaterialTheme {
                val sheetState = rememberModalBottomSheetState()
                var showBottomSheet by remember { mutableStateOf(false) }

                var animationFactors by remember { mutableStateOf(CardAnimationFactors()) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showBottomSheet = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Open animation settings")
                        }
                    }
                ) { innerPadding ->
                    CardList(
                        count = 100,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        animationFactors = animationFactors,
                        content = { index ->
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "Card $index",
                            )
                        },
                    )

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            AnimationSettingsSheet(
                                animationFactors = animationFactors,
                                onAnimationFactorsChange = { animationFactors = it },
                                onResetClick = { animationFactors = CardAnimationFactors() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleMaterialTheme {
        Greeting("Android")
    }
}