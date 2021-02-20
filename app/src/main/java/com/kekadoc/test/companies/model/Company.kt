package com.kekadoc.test.companies.model

import com.google.gson.annotations.SerializedName

data class Company(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("img") var imageUrl: String?,
)