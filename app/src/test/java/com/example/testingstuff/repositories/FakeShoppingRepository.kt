package com.example.testingstuff.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testingstuff.data.local.ShoppingItem
import com.example.testingstuff.data.remote.responses.ImageResponse
import com.example.testingstuff.utils.Resource


class FakeShoppingRepository : ShoppingRepository {

    private val shoppingItems = mutableListOf<ShoppingItem>()
    private val shoppingItemsLiveData = MutableLiveData<List<ShoppingItem>>(shoppingItems)
    private val totalPricesLiveData = MutableLiveData<Float>()
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        refreshLiveData()
    }

    private fun refreshLiveData() {
        shoppingItemsLiveData.postValue(shoppingItems)
        totalPricesLiveData.postValue(getTotalPrice())
    }

    private fun getTotalPrice(): Float {
        return shoppingItems.sumOf { it.amount * it.price.toDouble() }.toFloat()
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.remove(shoppingItem)
        refreshLiveData()
    }

    override fun observeAllShoppingItems(): LiveData<List<ShoppingItem>> {
        return shoppingItemsLiveData
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return totalPricesLiveData
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return if (shouldReturnNetworkError) {
            Resource.Error("Error")
        } else {
            Resource.Success(
                data = ImageResponse(
                    hits = listOf(),
                    total = 0,
                    totalHits = 0
                )
            )
        }
    }

}