package cl.newsapp.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<News>
)
