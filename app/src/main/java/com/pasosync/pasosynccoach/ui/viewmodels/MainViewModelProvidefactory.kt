package com.pasosync.pasosynccoach.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pasosync.pasosynccoach.repositories.MainRepos
import com.pasosync.pasosynccoach.repositories.MainRepository

class MainViewModelProvidefactory(
    val app: Application,
    val mainRepository: MainRepository,
    private val repos: MainRepos
):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModels(app,mainRepository,repos) as T
    }
}