package com.example.testingstuff.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testingstuff.data.local.ShoppingItem
import com.example.testingstuff.data.remote.responses.ImageResponse
import com.example.testingstuff.repositories.ShoppingRepository
import com.example.testingstuff.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {
    val shoppingItemsViewState = repository.observeAllShoppingItems()
    val totalPriceViewState = repository.observeTotalPrice()

    private val _images = MutableLiveData<Resource<ImageResponse>>()
    val images: LiveData<Resource<ImageResponse>> = _images

    private val _curImageUrl = MutableLiveData<String>()
    val curImageUrl: LiveData<String> = _curImageUrl

    private val _insertShoppingItemStatus = MutableLiveData<Resource<ShoppingItem>>()
    val insertShoppingItemStatus: LiveData<Resource<ShoppingItem>> = _insertShoppingItemStatus

    private val _deleteShoppingItemStatus = MutableLiveData<Resource<ShoppingItem>>()
    val deleteShoppingItemStatus: LiveData<Resource<ShoppingItem>> = _deleteShoppingItemStatus

    fun setCurlImageUrl(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.deleteShoppingItem(shoppingItem)
    }

    fun insertShoppingItemIntoDb(shoppingItem: ShoppingItem) = viewModelScope.launch {
        repository.insertShoppingItem(shoppingItem)
    }

    fun insertShoppingItem(name: String, amountString: String, priceString: String) {

    }

    fun searchForImage(imageQuery: String) {

    }
}