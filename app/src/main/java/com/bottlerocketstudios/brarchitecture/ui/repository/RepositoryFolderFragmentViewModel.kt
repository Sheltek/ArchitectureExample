package com.bottlerocketstudios.brarchitecture.ui.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.bottlerocketstudios.brarchitecture.data.model.RepoFile
import com.bottlerocketstudios.brarchitecture.data.repository.BitbucketRepository
import com.bottlerocketstudios.brarchitecture.infrastructure.coroutine.DispatcherProvider
import com.bottlerocketstudios.brarchitecture.ui.BaseViewModel
import com.xwray.groupie.Section
import kotlinx.coroutines.launch

class RepositoryFolderFragmentViewModel(app: Application, private val repo: BitbucketRepository, private val dispatcherProvider: DispatcherProvider) : BaseViewModel(app) {
    val srcFiles: LiveData<List<RepoFile>?> = MutableLiveData()
    val filesGroup = Section()
    var path: String? = null

    private val filesObserver = Observer<List<RepoFile>?> { files ->
        filesGroup.setHeader(FolderHeaderViewModel(path ?: "", files?.size ?: 0))
        val map = files?.map { RepoFileViewModel(it) }
        map?.let {
            filesGroup.update(map)
        }
    }

    init {
        srcFiles.observeForever(filesObserver)
    }

    fun loadRepo(workspaceSlug: String, repoId: String, hash: String, path: String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            srcFiles.postValue(repo.getSourceFolder(workspaceSlug, repoId, hash, path))
            this@RepositoryFolderFragmentViewModel.path = path
        }
    }

    override fun onCleared() {
        super.onCleared()
        srcFiles.removeObserver(filesObserver)
    }
}
