package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
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

    class TabFragment1 : Fragment(R.layout.tab_1_layout)
    class TabFragment2 : Fragment(R.layout.last_thing_layout)
}