package com.bottlerocketstudios.brarchitecture.ui.snippet

import com.bottlerocketstudios.brarchitecture.data.repository.BitbucketRepository
import com.bottlerocketstudios.brarchitecture.domain.models.Status
import com.bottlerocketstudios.brarchitecture.infrastructure.util.exhaustive
import com.bottlerocketstudios.brarchitecture.navigation.NavigationEvent
import com.bottlerocketstudios.brarchitecture.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

class CreateSnippetFragmentViewModel(private val repo: BitbucketRepository) : BaseViewModel() {

    // Two way databinding
    val title = MutableStateFlow("")
    val filename = MutableStateFlow("")
    val contents = MutableStateFlow("")
    val private = MutableStateFlow(false)

    // One way databinding
    val failed: StateFlow<Boolean> = MutableStateFlow(false)
    val createEnabled: StateFlow<Boolean> = combine(title, filename, contents) { (title, filename, contents) ->
        !(title.isEmpty() || filename.isEmpty() || contents.isEmpty())
    }.groundState(false)

    fun onCreateClick() {
        failed.set(false)
        launchIO {
            when (repo.createSnippet(title.value, filename.value, contents.value, private.value)) {
                is Status.Success -> navigationEvent.postValue(NavigationEvent.Up)
                is Status.Failure -> failed.set(true)
            }.exhaustive
        }
    }
}
