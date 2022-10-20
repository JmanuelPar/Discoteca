package com.diego.discoteca.data.model

import com.squareup.moshi.Json

data class NetworkDiscDiscogs(
    val pagination: Pagination?,
    val results: List<Result>?
)

data class Pagination(
    val items: Int?,
    val page: Int?,
    val pages: Int?,
    @Json(name = "per_page") val perPage: Int?,
    val urls: Urls?
)

data class Result(
    val barcode: List<String>?,
    val catno: String?,
    val community: Community?,
    val country: String?,
    @Json(name = "cover_image") val coverImage: String?,
    val format: List<String>?,
    @Json(name = "format_quantity") val formatQuantity: Int?,
    val formats: List<Format>?,
    val genre: List<String>?,
    val id: Int?,
    val label: List<String>?,
    @Json(name = "master_id") val masterId: Int?,
    @Json(name = "master_url") val masterUrl: String?,
    @Json(name = "resource_url") val resourceUrl: String?,
    val style: List<String>?,
    val thumb: String?,
    val title: String?,
    val type: String?,
    val uri: String?,
    val year: String?
)

data class Urls(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

data class Community(
    val have: Int?,
    val want: Int?
)

data class Format(
    val descriptions: List<String>?,
    val name: String?,
    val qty: String?,
    val text: String?
)