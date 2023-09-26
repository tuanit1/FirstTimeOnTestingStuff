package com.example.testingstuff.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(JUnit4::class)
@SmallTest
class ShoppingDaoTest {
    private lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.shoppingDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertShoppingItem() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 2,
            price = 2f,
            imageUrl = "abc",
            id = 1
        )
        dao.insertShoppingItem(shoppingItem)
        val list = dao.observeAllShoppingItems().getOrAwaitData()
//        val insertedItem = dao.findById(1)
        assertThat(list).contains(shoppingItem)
    }

    @Test
    fun deleteShoppingItem() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 2,
            price = 2f,
            id = 1,
            imageUrl = "abc"
        )
        dao.insertShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)

        val insertedItem = dao.findById(1)
        assertThat(insertedItem).isNull()
    }

    @Test
    fun getTotalPriceSum() = runTest {
        val shoppingItem = ShoppingItem(
            name = "name",
            amount = 2,
            price = 10f,
            imageUrl = "abc"
        )
        val shoppingItem1 = ShoppingItem(
            name = "name",
            amount = 4,
            price = 5.5f,
            imageUrl = "abc"
        )
        val shoppingItem2 = ShoppingItem(
            name = "name",
            amount = 0,
            price = 100f,
            imageUrl = "abc"
        )

        dao.run {
            insertShoppingItem(shoppingItem)
            insertShoppingItem(shoppingItem1)
            insertShoppingItem(shoppingItem2)
        }

        val totalPriceSum = dao.getTotalPrice()

        assertThat(totalPriceSum).isEqualTo(2 * 10f + 4 * 5.5f)
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

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)

        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            this.removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}