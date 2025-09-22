package com.example.Globalens.Repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.Globalens.CheckNetwork
import com.example.Globalens.ViewModel.NewsViewModel

class ViewModelFactory(private val repository: NewsRepository,private val network: CheckNetwork) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)){
            return NewsViewModel(repository,network) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}