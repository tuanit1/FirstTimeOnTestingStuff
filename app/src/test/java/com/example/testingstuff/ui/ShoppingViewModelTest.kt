package com.example.testingstuff.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.testingstuff.repositories.FakeShoppingRepository
import com.example.testingstuff.utils.Constants
import com.example.testingstuff.utils.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShoppingViewModelTest {
    private lateinit var viewModel: ShoppingViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty field, returns error`() = runTest {
        viewModel.insertShoppingItem("", "5", "3.0")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitData()
        assertThat(value).isSameInstanceAs(Resource.Error::class.java)
    }

    @Test
    fun `insert shopping item with too long name, returns error`() = runTest {
        val string = buildString {
            for (i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem(string, "5", "3.0")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitData()
        assertThat(value).isSameInstanceAs(Resource.Error::class.java)
    }

    @Test
    fun `insert shopping item with too long price, returns error`() = runTest {
        val string = buildString {
            for (i in 1..Constants.MAX_PRICE_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem("name", "5", string)
        val value = viewModel.insertShoppingItemStatus.getOrAwaitData()
        assertThat(value).isSameInstanceAs(Resource.Error::class.java)
    }

    @Test
    fun `insert shopping item with too high amount, returns error`() = runTest {
        viewModel.insertShoppingItem("name", "99999999999999999", "3.0")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitData()
        assertThat(value).isSameInstanceAs(Resource.Error::class.java)
    }

    @Test
    fun `insert shopping item with valid input, returns success`() = runTest {
        viewModel.insertShoppingItem("name", "5", "3.0")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitData()
        assertThat(value).isSameInstanceAs(Resource.Success::class.java)
    }

    private suspend fun <T> LiveData<T>.getOrAwaitData(): T? {
        var data: T? = null
        val observer = object : Observer<T> {
            override fun onChanged(o: T) {
                data = o
                removeObserver(this)
            }
        }

        observeForever(observer)
        delay(2000)
        removeObserver(observer)
        return data
    }

}