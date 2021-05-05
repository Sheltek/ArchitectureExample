package com.bottlerocketstudios.brarchitecture.ui.snippet

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bottlerocketstudios.brarchitecture.R
import com.bottlerocketstudios.brarchitecture.databinding.CreateSnippetFragmentBinding
import com.bottlerocketstudios.brarchitecture.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateSnippetFragment : BaseFragment<CreateSnippetFragmentViewModel, CreateSnippetFragmentBinding>() {
    override val fragmentViewModel: CreateSnippetFragmentViewModel by viewModel()

    override fun getLayoutRes(): Int = R.layout.create_snippet_fragment

    override fun setupBinding(binding: CreateSnippetFragmentBinding) {
        super.setupBinding(binding)
        fragmentViewModel.done.observe(viewLifecycleOwner, Observer {
            findNavController().navigateUp()
        })
    }
}
