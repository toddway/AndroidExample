package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.TabsLayoutBinding

class TabsLayout(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    private val binding by lazy { TabsLayoutBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) return
        binding.tabBar.setupWithNavController(binding.tabPageContainer.findNavController())
    }

    class TabFragment1 : Fragment() {
        private val viewModel : AddThingViewModel by viewModelStore {
            requireContext().appComponent().addThingViewModel().get()
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            return ComposeView(requireContext()).apply { setContent { AddThing(viewModel) } }
        }
    }
    class TabFragment2 : Fragment(R.layout.last_thing_layout)
}