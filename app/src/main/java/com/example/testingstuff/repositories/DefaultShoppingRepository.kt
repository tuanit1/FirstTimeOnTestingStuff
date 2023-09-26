package com.example.testingstuff.repositories

import androidx.lifecycle.LiveData
import com.example.testingstuff.data.local.ShoppingDao
import com.example.testingstuff.data.local.ShoppingItem
import com.example.testingstuff.data.remote.PixabayAPI
import com.example.testingstuff.data.remote.responses.ImageResponse
import com.example.testingstuff.utils.Resource
import javax.inject.Inject

class DefaultShoppingRepository @Inject constructor(
    private val shoppingDao: ShoppingDao, //local datasource
    private val pixabayAPI: PixabayAPI //remote datasource
) : ShoppingRepository {
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> =
        shoppingDao.observeAllShoppingItems()

    override fun observeTotalPrice(): LiveData<Float> =
        shoppingDao.observeTotalPrice()

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return try {
            val response = pixabayAPI.searchForImage(imageQuery)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: run {
                    Resource.Error("An unknown error occurred")
                }
            } else {
                Resource.Error("An unknown error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}