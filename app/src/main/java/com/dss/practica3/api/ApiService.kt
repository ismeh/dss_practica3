package com.dss.practica3.api

import com.dss.practica3.models.Product
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Get all products
    @GET("/api/products")
    fun getAllProducts(): Call<List<Product>>

    @GET("/api/products-by-id")
    fun getProductsById(@Query("ids") ids: String): Call<List<Product>>

    @GET("/api/cart/checkout")
    fun checkout(@Query("ids") ids: String): Call<ResponseBody>
}
