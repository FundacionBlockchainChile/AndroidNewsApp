package cl.newsapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.newsapp.viewmodel.NewsViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailView(newsUrl: String, viewModel: NewsViewModel, navController: NavController) {
    // Decodificar la URL antes de usarla en el WebView
    val decodedUrl = URLDecoder.decode(newsUrl, StandardCharsets.UTF_8.toString())
    val selectedNews = remember { viewModel.getNewsByUrl(decodedUrl) }

    Scaffold(
        topBar = {
            TopAppBar(
                    title = {
                        if (selectedNews != null) {
                            Text(
                                text = selectedNews.title,
                                maxLines = 2,  // Permitir hasta 2 líneas para el título
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
        }
    ) {
        // Incluir el WebView en el contenido de Compose
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()  // Esto permite que la navegación ocurra dentro de la app
                    settings.javaScriptEnabled = true  // Habilitar JavaScript si la página lo necesita
                    loadUrl(decodedUrl)  // Cargar la URL de la noticia
                }
            }
        )
    }
}
