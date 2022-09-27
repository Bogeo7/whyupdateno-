package com.example.app

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface imsiapi {
    @GET("/show/show_temptable.php")
    fun getRefrigeTable(
//        @Query("query") userName: String
    ): Call<JsonArray>

//    @GET("/update/update_ingredient.php?Uingredient=onion&")
//    fun minusOne(
////        @Query("Uingredient") Uingredient: String,
//        @Query("Ucnt") Ucnt: String,
//    ): Call<JsonArray>

    @GET("/json.php")
    fun getResult(

    ): Call<JsonArray>
}