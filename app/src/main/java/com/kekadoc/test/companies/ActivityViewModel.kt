package com.kekadoc.test.companies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kekadoc.test.companies.model.Company
import com.kekadoc.test.companies.model.CompanyLoader

class ActivityViewModel : ViewModel() {

    companion object {
        private const val TAG: String = "ActivityViewModel-TAG"
    }

    private val companyLoader = CompanyLoader.create()
    private val companiesData = object : MutableLiveData<List<Company>>(emptyList()) {
        override fun onActive() {
            refreshData()
        }
    }
    private var activeLoading: CompanyLoader.Loading? = null
    private val loadingProcess = MutableLiveData(false)

    fun getCompanies(): LiveData<List<Company>> = companiesData
    fun getLoadingProcess(): LiveData<Boolean> = loadingProcess

    fun refreshData() {
        loadingProcess.postValue(true)
        activeLoading = companyLoader.load().apply {
            onComplete {
                companiesData.value = it
                activeLoading = null
                loadingProcess.postValue(false)
            }
            onFail {
                companiesData.value = data
                activeLoading = null
                loadingProcess.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        activeLoading?.cancel()
        activeLoading = null
    }

}