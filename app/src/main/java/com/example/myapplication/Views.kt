package com.example.myapplication

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import javax.inject.Provider

inline fun <reified VM : ViewModel> View.viewModelStore(
    noinline ownerProducer: () -> ViewModelStoreOwner = { findFragment() },
    noinline viewModelProducer : () -> VM
): Lazy<VM> = ViewModelLazy(
    VM::class,
    { ownerProducer().viewModelStore },
    {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val viewModel = viewModelProducer()
                return if (modelClass.isAssignableFrom(viewModel.javaClass)) { viewModel as T }
                else { throw IllegalArgumentException("Unknown ViewModel Class") }
            }
        }
    }
)

inline fun <reified VM : ViewModel> Fragment.viewModelStore(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline viewModelProducer : () -> VM
): Lazy<VM> = ViewModelLazy(
    VM::class,
    { ownerProducer().viewModelStore },
    {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val viewModel = viewModelProducer()
                return if (modelClass.isAssignableFrom(viewModel.javaClass)) { viewModel as T }
                else { throw IllegalArgumentException("Unknown ViewModel Class") }
            }
        }
    }
)