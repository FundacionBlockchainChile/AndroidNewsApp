package cl.newsapp.viewmodel

import androidx.lifecycle.viewModelScope
import cl.newsapp.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingData
import cl.newsapp.data.NewsDataSource
import cl.newsapp.model.News

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository()

    private val currentQuery = MutableStateFlow("")

    // Paginación de noticias con búsqueda dinámica
    fun getNewsPager(query: String): Flow<PagingData<News>> {
        return Pager(PagingConfig(pageSize = 1, enablePlaceholders = false)) {  // Solo un artículo por página
            NewsDataSource(repository, query)
        }.flow.cachedIn(viewModelScope)
    }

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Buscar noticia por URL (u otro identificador)
    fun getNewsByUrl(url: String): News? {
        return _newsList.value.find { it.url == url }
    }
}
