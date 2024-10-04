package cl.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.newsapp.ui.theme.NewsAppTheme
import cl.newsapp.view.HomeView
import cl.newsapp.viewmodel.NewsViewModel
import androidx.navigation.compose.rememberNavController
import cl.newsapp.view.NewsDetailView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                val viewModel: NewsViewModel = viewModel()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeView(viewModel, navController)
                    }

                    composable("newsDetail/{newsUrl}") { backStackEntry ->
                        val newsUrl = backStackEntry.arguments?.getString("newsUrl")
                        if (newsUrl != null) {
                            NewsDetailView(newsUrl, viewModel, navController)
                        }
                    }
                }
            }
        }
    }
}
