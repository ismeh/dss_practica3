package com.dss.practica3.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    val BASE_URL = "http://10.0.2.2:8080/"  //to access server for Android emulator

    val BASE_URL = "http://dss.vaelico.es" // our server
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

//    var retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl("https://api.github.com/")
//        .build()
//
//    var service: GitHubService = retrofit.create<T>(GitHubService::class.java)
