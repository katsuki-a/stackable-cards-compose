package io.github.katsukia.stackablecards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.katsukia.stackablecards.ui.theme.SampleMaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleMaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var cardScaleFactor by remember { mutableFloatStateOf(CardDefaults.cardScaleFactor) }
                    var cardTranslationFactor by remember { mutableFloatStateOf(CardDefaults.cardTranslationFactor) }
                    var cardAlphaFactor by remember { mutableFloatStateOf(CardDefaults.cardAlphaFactor) }
                    var cardShadowAlphaFactor by remember { mutableFloatStateOf(CardDefaults.cardShadowAlphaFactor) }

                    Column(modifier = Modifier.padding(innerPadding)) {
                        CardList(
                            count = 100,
                            modifier = Modifier.weight(1f),
                            cardScaleFactor = cardScaleFactor,
                            cardTranslationFactor = cardTranslationFactor,
                            cardAlphaFactor = cardAlphaFactor,
                            cardShadowAlphaFactor = cardShadowAlphaFactor,
                            content = { index ->
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = "Card $index",
                                )
                            },
                        )
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(text = "cardScaleFactor: %.2f".format(cardScaleFactor))
                            Slider(
                                value = cardScaleFactor,
                                onValueChange = { cardScaleFactor = it },
                                valueRange = 0f..1f,
                            )
                            Text(text = "cardTranslationFactor: %.2f".format(cardTranslationFactor))
                            Slider(
                                value = cardTranslationFactor,
                                onValueChange = { cardTranslationFactor = it },
                                valueRange = 0f..1f,
                            )
                            Text(text = "cardAlphaFactor: %.2f".format(cardAlphaFactor))
                            Slider(
                                value = cardAlphaFactor,
                                onValueChange = { cardAlphaFactor = it },
                                valueRange = 0f..20f,
                            )
                            Text(text = "cardShadowAlphaFactor: %.2f".format(cardShadowAlphaFactor))
                            Slider(
                                value = cardShadowAlphaFactor,
                                onValueChange = { cardShadowAlphaFactor = it },
                                valueRange = 0f..1f,
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