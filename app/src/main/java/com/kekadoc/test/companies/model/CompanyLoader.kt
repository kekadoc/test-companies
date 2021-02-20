package com.kekadoc.test.companies.model

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val defaultCompaniesUrl = "https://lifehack.studio/test_task/"

class CompanyLoader(private val companiesUrl: String) {

    companion object {
        private const val TAG: String = "CompanyLoader-TAG"

        @JvmStatic
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

    fun load(): Loading {
        val messages: Call<List<Company>> = companiesApi.companies()
        return Loading(messages)
    }

    inner class Loading(private val call: Call<List<Company>>) {

        private var onComplete: ((companies: List<Company>) -> Unit)? = null
        private var onFail: ((error: Throwable) -> Unit)? = null

        var data: List<Company> = emptyList()

        init {
            call.enqueue(object : Callback<List<Company>> {
                override fun onResponse(call: Call<List<Company>>, response: Response<List<Company>>) {
                    var data = response.body()
                    if (data == null) data = emptyList()
                    this@Loading.data = data
                    data.forEach {
                        it.imageUrl = companiesUrl + it.imageUrl
                    }
                    onComplete?.invoke(data)
                }
                override fun onFailure(call: Call<List<Company>>, t: Throwable) {
                    onFail?.invoke(t)
                    Log.e(TAG, "onFailure: ", t)
                }
            })
        }

        fun onComplete(l: ((companies: List<Company>) -> Unit)?) {
            onComplete = l
        }
        fun onFail(l: ((error: Throwable) -> Unit)?) {
            onFail = l
        }

        fun cancel() = call.cancel()

    }

}

interface CompaniesApi {
    @GET("test.php")
    fun companies(): Call<List<Company>>
}