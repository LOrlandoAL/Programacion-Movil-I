package com.example.miprimeraaplicacion

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.miprimeraaplicacion.ui.theme.MiPrimeraAplicacionTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiPrimeraAplicacionTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun OnboardingScreen (modifier: Modifier = Modifier,
                      onContinueClick: ()-> Unit
){
    var  shouldShowOnboarding: MutableState<Boolean>  = remember {
        mutableStateOf(true)
    }
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.welcome_to_de_basic_compose_lab))
        Button(modifier = Modifier.padding(vertical = 24.dp) ,
            onClick = onContinueClick
        ) {
            Text(text = "Continue")
        }
    }

}

@Composable
fun Greetings(modifier: Modifier = Modifier,
              names: List<String> = List(10,
                  init = {
                      "$it"
                  }
              )
){
    LazyColumn {
        items(items = names){
            Greeting(name = it)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val extraPadding by animateDpAsState(
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Surface (
        modifier.padding(vertical = 4.dp, horizontal = 8.dp), color = MaterialTheme.colorScheme.primary
    ) {
        Row (modifier = Modifier
            .padding(24.dp)) {
            Column (
                modifier =
                Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp))

            ) {
                Text(text = "Hola Mundo ",
                )
                Text(text = "#$name",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                if (expanded) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " +
                                "padding theme elit, sed do bouncy. ").repeat(4),
                    )
                }
            }
            val contentDescription = if (expanded) stringResource(R.string.show_less) else stringResource(
                R.string.show_more
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore
                    , contentDescription = contentDescription)
            }
        }


    }
}
@Composable
fun MyApp(modifier: Modifier = Modifier, names: List<String> = listOf("World", "Android", "Compose","oTRO", "maS", "mAS OTRO")
){
    val shouldShowOnboarding: MutableState<Boolean> = rememberSaveable{
        mutableStateOf(true)
    }

    Surface(modifier=modifier,
        color = MaterialTheme.colorScheme.background) {
        if (shouldShowOnboarding.value) {
            OnboardingScreen(onContinueClick = {
                shouldShowOnboarding.value = false
            })
        } else {
            Greetings(modifier = modifier)
        }
    }
    //OnboardingScreen()
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "GreetingPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    MiPrimeraAplicacionTheme {
        Greetings()
    }
}
