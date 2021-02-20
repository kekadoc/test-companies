package com.kekadoc.test.companies.model

import android.util.Log
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

const val defaultCompaniesUrl = "https://lifehack.studio/test_task/"

class CompanyLoader(private val companiesUrl: String) {

    companion object {

        private const val TAG: String = "CompanyLoader-TAG"

        fun create(companiesUrl: String = defaultCompaniesUrl): CompanyLoader {
            return CompanyLoader(companiesUrl)
        }

    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(companiesUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val companiesApi: CompaniesApi by lazy {
        retrofit.create(CompaniesApi::class.java)
    }

    fun loadAll(): Loading<List<CompanyPreview>, List<CompanyPreview>> {
        val call: Call<List<CompanyPreview>> = companiesApi.companies()
        return AllCompanyLoader(call)
    }
    fun load(id: Long): Loading<List<Company>, Company> {
        val messages: Call<List<Company>> = companiesApi.company(id)
        return SingleCompanyLoader(messages)
    }

    abstract inner class Loading<CData, Data> internal constructor(protected val call: Call<CData>) {

        private var onComplete: ((data: Data?) -> Unit)? = null
        private var onFail: ((error: Throwable) -> Unit)? = null

        var data: Data? = null

        init {
            call.enqueue(object : Callback<CData> {
                override fun onResponse(call: Call<CData>, response: Response<CData>) {
                    this@Loading.data = onComplete(response.body())
                    onComplete?.invoke(data)
                }

                override fun onFailure(call: Call<CData>, t: Throwable) {
                    onFail(t)
                    onFail?.invoke(t)
                    Log.e(TAG, "onFailure: ", t)
                }
            })
        }

        fun onComplete(l: ((data: Data?) -> Unit)?) {
            onComplete = l
        }
        fun onFail(l: ((error: Throwable) -> Unit)?) {
            onFail = l
        }

        fun cancel() = call.cancel()

        protected abstract fun onComplete(data: CData?): Data?
        protected open fun onFail(error: Throwable) {}

    }

    private inner class SingleCompanyLoader(call: Call<List<Company>>) : Loading<List<Company>, Company>(call) {
        override fun onComplete(data: List<Company>?): Company? {
            if (data == null || data.isEmpty()) return null
            val company = data.firstOrNull()
            company?.let {
                it.imageUrl = companiesUrl + it.imageUrl
            }
            return company
        }
    }
    private inner class AllCompanyLoader(call: Call<List<CompanyPreview>>) : Loading<List<CompanyPreview>, List<CompanyPreview>>(call) {
        override fun onComplete(data: List<CompanyPreview>?): List<CompanyPreview>? {
            data?.let {
                it.forEach { company ->
                    company.imageUrl = companiesUrl + company.imageUrl
                }
            }
            return data
        }
    }

}

interface CompaniesApi {
    @GET("test.php")
    fun companies(): Call<List<CompanyPreview>>

    @GET("test.php?")
    fun company(@Query("id") id: Long): Call<List<Company>>
}

data class CompanyPreview(
        @SerializedName("id") val id: Long? = 0,
        @SerializedName("name") val name: String? = null,
        @SerializedName("img") var imageUrl: String? = null
)
data class Company(
        @SerializedName("id") val id: Long? = 0,
        @SerializedName("name") val name: String? = null,
        @SerializedName("img") var imageUrl: String? = null,
        @SerializedName("description") var description: Any? = null,
        @SerializedName("lat") var lat: Double? = null,
        @SerializedName("lon") var lon: Double? = null,
        @SerializedName("www") var www: String? = null,
        @SerializedName("phone") var phone: String? = null
)