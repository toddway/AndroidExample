package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.myapplication.databinding.AddThingLayoutBinding
import javax.inject.Inject

class AddThingLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    @Inject @FromStore lateinit var viewModel : AddThingViewModel
    @Inject lateinit var lifecycleOwner: LifecycleOwner
    private val binding by lazy { AddThingLayoutBinding.bind(this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) return
        appComponent().addThingFactory().create(this).inject(this)
        viewModel.thingLiveData.observe(lifecycleOwner, Observer {
            binding.textView.text = "$it"
            binding.textView.animateUp()
        })
        binding.generateThingButton.setOnClickListener {
            viewModel.generateThingButtonClicked()
        }
        binding.root.setOnClickListener {
            findNavController().navigate(R.id.action_add_thing_layout_fragment_to_page_2_fragment)
        }
    }

    class LayoutFragment : Fragment(R.layout.add_thing_layout)
}

class HelloWorldFragment : Fragment(R.layout.hello_world_layout)

fun View.animateUp() = apply {
    alpha = 1f
    translationY = 0f
    animate().alpha(0f).translationY(-300f).setDuration(900L).setStartDelay(200L).start()
}



