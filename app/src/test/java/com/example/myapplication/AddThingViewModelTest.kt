package com.example.myapplication

import com.example.thingdata.ThingsLocalDatasource
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Test

@ExperimentalCoroutinesApi
class AddThingViewModelTest {

    @Test
    fun `when button is clicked, then generate thing is called`() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        val d = mockk<ThingsLocalDatasource>(relaxed = true)
        val viewModel = AddThingViewModel(d, d)

        viewModel.generateThingButtonClicked()

        coVerify { d.generateThing() }
    }
}

