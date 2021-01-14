package com.example.myapplication

import com.example.thingdata.ThingsLocalDatasource
import org.junit.Test

class MainViewModelTest {



    @Test
    fun `when`() {
        val d = ThingsLocalDatasource()
        val viewModel = MainViewModel(d, d)


        viewModel.generateThingButtonClicked()


    }
}

