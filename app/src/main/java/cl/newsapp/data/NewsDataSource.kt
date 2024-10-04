package cl.newsapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cl.newsapp.model.News
import cl.newsapp.repository.NewsRepository

class NewsDataSource(private val repository: NewsRepository, private val query: String) : PagingSource<Int, News>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        return try {
            val currentPage = params.key ?: 1  // Comienza en la página 1
            val response = repository.getNewsPaged(query, currentPage, 3) 
            val news = response.articles

            LoadResult.Page(
                data = news,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (news.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, News>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}



