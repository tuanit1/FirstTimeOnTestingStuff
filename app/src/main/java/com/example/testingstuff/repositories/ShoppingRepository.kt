package com.example.testingstuff.repositories

import androidx.lifecycle.LiveData
import com.example.testingstuff.data.local.ShoppingItem
import com.example.testingstuff.data.remote.responses.ImageResponse
import com.example.testingstuff.utils.Resource
import retrofit2.Response

interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}