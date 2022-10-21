package com.diego.discoteca.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diego.discoteca.util.AddBy

@Entity(tableName = "disc_table")
data class DatabaseDisc(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "year")
    val year: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "format")
    val format: String,

    @ColumnInfo(name = "formatMedia")
    val formatMedia: String,

    @ColumnInfo(name = "coverImage")
    val coverImage: String,

    @ColumnInfo(name = "barcode")
    val barcode: String,

    @ColumnInfo(name = "idDisc")
    val idDisc: Int,

    @ColumnInfo(name = "addBy")
    val addBy: AddBy,

    @ColumnInfo(name = "nameNormalize")
    val nameNormalize: String,

    @ColumnInfo(name = "titleNormalize")
    val titleNormalize: String
)
