package com.pasosync.pasosynccoach.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pasosync.pasosynccoach.Repositories.MainRepository

class MainViewModelProvidefactory(
    val mainRepository: MainRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModels(mainRepository) as T
    }
}