package com.diego.discoteca.network

import com.diego.discoteca.data.model.NetworkDiscDiscogs
import com.diego.discoteca.util.Constants.API_DISCOGS_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Get your key and secret here : www.discogs.com/developers

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val logger = HttpLoggingInterceptor().apply {
    level = Level.BASIC
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(logger)
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(API_DISCOGS_URL)
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface DiscogsApiService {

    @GET("database/search")
    suspend fun getSearchBarcode(
        @Query("type") type: String,
        @Query("barcode") barcode: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("key") key: String,
        @Query("secret") secret: String
    ): NetworkDiscDiscogs

    @GET("database/search")
    suspend fun getSearchDisc(
        @Query("type") type: String,
        @Query("artist") artist: String,
        @Query("release_title") release_title: String,
        @Query("year") year: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("key") key: String,
        @Query("secret") secret: String
    ): NetworkDiscDiscogs
}

object DiscogsApi {
    val retrofitService: DiscogsApiService by lazy {
        retrofit.create(DiscogsApiService::class.java)
    }
}

