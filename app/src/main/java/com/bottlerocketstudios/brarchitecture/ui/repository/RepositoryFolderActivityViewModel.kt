package com.bottlerocketstudios.brarchitecture.ui.repository

import android.app.Application
import com.bottlerocketstudios.brarchitecture.infrastructure.repository.BitbucketRepository
import com.bottlerocketstudios.brarchitecture.ui.RepoViewModel

class RepositoryFolderActivityViewModel (app: Application, repo: BitbucketRepository) : RepoViewModel(app, repo) {
    
}
