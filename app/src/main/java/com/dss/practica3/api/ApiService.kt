package com.dss.practica3.api

import com.dss.practica3.models.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Get all products
    @GET("/products")
    fun getAllProducts(): Call<List<Product>>
}
