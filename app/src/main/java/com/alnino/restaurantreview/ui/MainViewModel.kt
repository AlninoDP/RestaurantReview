package com.alnino.restaurantreview.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alnino.restaurantreview.data.response.CustomerReviewsItem
import com.alnino.restaurantreview.data.response.PostReviewResponse
import com.alnino.restaurantreview.data.response.Restaurant
import com.alnino.restaurantreview.data.response.RestaurantResponse
import com.alnino.restaurantreview.data.retrofit.ApiConfig
import com.alnino.restaurantreview.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()
    val listReview: LiveData<List<CustomerReviewsItem>> = _listReview

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText



    companion object {
        private const val TAG = "MainViewmodel"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    init {
        findRestaurant()
    }

    private fun findRestaurant() {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
        client.enqueue(object : Callback<RestaurantResponse> {

            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        _restaurant.value = responseBody.restaurant
                        _listReview.value = responseBody.restaurant.customerReviews
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })

    }

    fun postReview(review: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)

        client.enqueue(
            object : Callback<PostReviewResponse> {

                override fun onResponse(
                    call: Call<PostReviewResponse>,
                    response: Response<PostReviewResponse>
                ) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        _listReview.value = responseBody.customerReviews
                        _snackBarText.value = Event(responseBody.message.toString())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(p0: Call<PostReviewResponse>, p1: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${p1.message}")
                }

            }
        )
    }
}