package com.kekadoc.test.companies.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kekadoc.test.companies.model.CompanyLoader
import com.kekadoc.test.companies.model.CompanyPreview

object HomeFragmentModelFactory : ViewModelProvider.NewInstanceFactory() {

    private var model: HomeFragmentModel? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(HomeFragmentModel::class.java)) throw RuntimeException()
        if (model == null) model = HomeFragmentModel()
        return model as T
    }

}

class HomeFragmentModel : ViewModel() {

    private val companyLoader = CompanyLoader.create()

    private val companiesData = object : MutableLiveData<List<CompanyPreview>>(emptyList()) {
        override fun onActive() {
            refreshData()
        }
    }
    private var activeLoadingAll: CompanyLoader.Loading<*, *>? = null
    private val loadingProcessAll = MutableLiveData(false)

    fun getCompanies(): LiveData<List<CompanyPreview>> = companiesData
    fun getLoadingProcess(): LiveData<Boolean> = loadingProcessAll

    fun refreshData() {
        activeLoadingAll?.cancel()
        loadingProcessAll.postValue(true)
        activeLoadingAll = companyLoader.loadAll().apply {
            onComplete {
                companiesData.value = it
                activeLoadingAll = null
                loadingProcessAll.postValue(false)
            }
            onFail {
                companiesData.value = data
                activeLoadingAll = null
                loadingProcessAll.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        activeLoadingAll?.cancel()
        activeLoadingAll = null
    }
}