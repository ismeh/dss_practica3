package com.dss.practica3.api

import com.dss.practica3.models.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Get all products
    @GET("/api/products")
    fun getAllProducts(): Call<List<Product>>

    @GET("/api/products-by-id")
    fun getProductsById(@Query("ids") ids: String): Call<List<Product>>

    @GET("/api/login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Map<String, String>>

    @GET("/api/checkPrivileges")
    fun checkPrivileges(@Query("token") token: String): Call<Boolean>

    @GET("/api/products/add")
    fun addProduct(
        @Query("token") token: String,
        @Query("name") Name: String,
        @Query("price") Price: Double
    ): Call<Integer>

    @GET("/api/products/edit")
    fun editProduct(
        @Query("token") token: String,
        @Query("id") Id: Long,
        @Query("name") Name: String,
        @Query("price") Price: Double
    ): Call<Integer>

    @GET("/api/products/delete")
    fun deleteProduct(@Query("token") token: String, @Query("id") Id: Long): Call<Integer>


}
