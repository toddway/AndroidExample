package com.example.myapplication

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import javax.inject.Provider

inline fun <reified T: ViewModel> viewModelFactory(crossinline createViewModel : () -> T) = object: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = createViewModel()
        return if (modelClass.isAssignableFrom(viewModel.javaClass)) { viewModel as T }
        else { throw IllegalArgumentException("Unknown ViewModel Class") }
    }
}

inline fun <reified T : ViewModel> Provider<T>.fromStore(owner: ViewModelStoreOwner)
        = ViewModelProvider(owner, viewModelFactory { get() }).get(T::class.java)

fun View.activity() = context as AppCompatActivity