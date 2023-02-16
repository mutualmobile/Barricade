package com.mutualmobile.barricadeSample.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.barricade.Barricade
import com.mutualmobile.barricadeSample.data.JokeApi
import com.mutualmobile.barricadeSample.data.models.JokeResponse
import com.mutualmobile.barricadeSample.states.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LandingScreenVM : ViewModel() {
    var barricadeStatus: Boolean by mutableStateOf(false)
        private set

    var jokeResponseState: ResponseState<JokeResponse> by mutableStateOf(ResponseState.Empty)
        private set

    var jokeCategoriesState: ResponseState<List<String>> by mutableStateOf(ResponseState.Empty)
        private set

    private val barricade = Barricade.getInstance()

    fun setBarricadeEnabled(isBarricadeEnabled: Boolean) {
        barricade.isEnabled = isBarricadeEnabled
        barricadeStatus = barricade.isEnabled
    }

    fun fetchJoke() {
        jokeCategoriesState = ResponseState.Empty
        jokeResponseState = ResponseState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val networkResponse = JokeApi.instance.getRandomJoke()
            if (networkResponse.isSuccessful) {
                networkResponse.body()?.let { nnNetworkResponse ->
                    jokeResponseState = ResponseState.Success(data = nnNetworkResponse)
                } ?: run {
                    jokeResponseState = ResponseState.Failure(reason = "Response body is empty")
                }
            } else {
                jokeResponseState = ResponseState.Failure(reason = networkResponse.message())
            }
        }
    }

    fun fetchJokeCategories() {
        jokeResponseState = ResponseState.Empty
        jokeCategoriesState = ResponseState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val networkResponse = JokeApi.instance.getJokeCategories()
            if (networkResponse.isSuccessful) {
                networkResponse.body()?.let { nnNetworkResponse ->
                    jokeCategoriesState = ResponseState.Success(data = nnNetworkResponse)
                } ?: run {
                    jokeCategoriesState = ResponseState.Failure(reason = "Response body is empty")
                }
            } else {
                jokeCategoriesState = ResponseState.Failure(reason = networkResponse.message())
            }
        }
    }
}
