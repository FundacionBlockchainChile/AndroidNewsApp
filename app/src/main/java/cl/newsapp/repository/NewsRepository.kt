package cl.newsapp.repository

import cl.newsapp.model.NewsResponse
import cl.newsapp.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/everything")
    suspend fun getNewsPaged(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 1,  // Tamaño de página predeterminado
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ): NewsResponse
}

class NewsRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NewsApi::class.java)

    // Obtener noticias paginadas
    suspend fun getNewsPaged(query: String, page: Int, pageSize: Int) = api.getNewsPaged(query, page, pageSize)
}
