package com.kekadoc.test.companies.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kekadoc.test.companies.model.Company
import com.kekadoc.test.companies.model.CompanyLoader

class DashboardFragmentModel : ViewModel() {

    private val companyLoader = CompanyLoader.create()

    private val companyData =  MutableLiveData<Company>(null)
    private var activeLoading: CompanyLoader.Loading<*, *>? = null
    private val loadingProcess = MutableLiveData(false)

    fun getCompany(): LiveData<Company> = companyData
    fun getLoadingProcess(): LiveData<Boolean> = loadingProcess

    fun loadCompany(id: Long) {
        activeLoading?.cancel()
        loadingProcess.postValue(true)
        activeLoading = companyLoader.load(id).apply {
            onComplete {
                companyData.value = it
                activeLoading = null
                loadingProcess.postValue(false)
            }
            onFail {
                companyData.value = data
                activeLoading = null
                loadingProcess.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        activeLoading?.cancel()
        activeLoading = null
        loadingProcess.postValue(false)
    }

}