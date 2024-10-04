package cl.newsapp.view

import cl.newsapp.model.News
import cl.newsapp.viewmodel.NewsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: NewsViewModel, navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Obtener los resultados paginados basados en la búsqueda
    val newsPagingItems = if (searchQuery.isNotEmpty()) {
        viewModel.getNewsPager(searchQuery).collectAsLazyPagingItems()
    } else {
        null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar noticias") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = {
                        if (searchQuery.isNotEmpty()) {
                            coroutineScope.launch {
                                viewModel.getNewsPager(searchQuery)
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            )
        }
    ) { padding ->
        if (newsPagingItems != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(newsPagingItems) { news ->
                    if (news != null) {
                        NewsCard(news) {
                            val encodedUrl = URLEncoder.encode(news.url, StandardCharsets.UTF_8.toString())
                            navController.navigate("newsDetail/$encodedUrl")
                        }
                    }
                }

                // Indicador de carga al hacer scroll hacia abajo
                when (newsPagingItems.loadState.append) {
                    is androidx.paging.LoadState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentSize()) }
                    }
                    is androidx.paging.LoadState.Error -> {
                        item { Text(text = "Error al cargar noticias.") }
                    }
                    else -> Unit
                }
            }
        }
    }
}



@Composable
fun NewsCard(news: News, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }
            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Imagen destacada con shimmer durante la carga
            if (news.urlToImage != null) {
                SubcomposeAsyncImage(
                    model = news.urlToImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {  // 'painter.state' accediendo desde el scope correctamente
                        is AsyncImagePainter.State.Loading -> {
                            // Mostrar shimmer mientras la imagen se carga
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmer() // Efecto shimmer durante la carga
                                    .background(Color.LightGray)
                            )
                        }
                        is AsyncImagePainter.State.Error -> {
                            // Mostrar un contenido alternativo en caso de error
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Error al cargar imagen", color = Color.White)
                            }
                        }
                        else -> {
                            // Mostrar la imagen una vez que se haya cargado
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenedor para el título, descripción y otros detalles
            Column(modifier = Modifier.padding(16.dp)) {

                // Título de la noticia
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Descripción (truncada si es muy larga)
                Text(
                    text = news.description ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Línea inferior con la fecha y el botón "Leer más"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fecha de publicación
                    Text(
                        text = news.publishedAt.split("T")[0],
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )
                    )

                    // Botón para ver más detalles
                    TextButton(onClick = { onClick() }) {
                        Text(
                            text = "Leer más",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

