package com.diego.discoteca.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.diego.discoteca.model.CountFormatMedia
import kotlinx.coroutines.flow.Flow

/* addBy: Int ->
   1 : AddBy.MANUALLY
   2 : AddBy.SCAN
   3 : AddBy.SEARCh */

@Dao
interface DiscDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLong(databaseDisc: DatabaseDisc): Long

    @Update
    suspend fun update(databaseDisc: DatabaseDisc)

    @Query("DELETE FROM disc_table WHERE id = :discId")
    suspend fun deleteById(discId: Long)

    @Query("SELECT COUNT(id) FROM disc_table")
    fun countAllDiscs(): Flow<Int>

    @Query("SELECT * from disc_table WHERE id = :key")
    fun getDiscWithId(key: Long): LiveData<DatabaseDisc>

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE (name LIKE '%' || :searchQuery || '%' OR nameNormalize LIKE '%' || :searchQuery || '%' ) " +
                "OR (title LIKE '%' || :searchQuery || '%' OR titleNormalize LIKE '%' || :searchQuery || '%' ) " +
                "OR year LIKE '%' || :searchQuery || '%' " +
                "ORDER BY nameNormalize ASC"
    )
    fun getAllDiscsByName(searchQuery: String): Flow<List<DatabaseDisc>>

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE (name LIKE '%' || :searchQuery || '%' OR nameNormalize LIKE '%' || :searchQuery || '%' ) " +
                "OR (title LIKE '%' || :searchQuery || '%' OR titleNormalize LIKE '%' || :searchQuery || '%' ) " +
                "OR year LIKE '%' || :searchQuery || '%' " +
                "ORDER BY titleNormalize ASC"
    )
    fun getAllDiscsByTitle(searchQuery: String): Flow<List<DatabaseDisc>>

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE (name LIKE '%' || :searchQuery || '%' OR nameNormalize LIKE '%' || :searchQuery || '%' ) " +
                "OR (title LIKE '%' || :searchQuery || '%' OR titleNormalize LIKE '%' || :searchQuery || '%' ) " +
                "OR year LIKE '%' || :searchQuery || '%' " +
                "ORDER BY year ASC"
    )
    fun getAllDiscsByYear(searchQuery: String): Flow<List<DatabaseDisc>>

    @Query("SELECT id, COUNT (id) AS countMedia, formatMedia FROM disc_table GROUP BY formatMedia")
    fun getCountFormatMediaList(): Flow<List<CountFormatMedia>>

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE barcode = :barcode " +
                "ORDER BY id ASC " +
                "LIMIT :limit " +
                "OFFSET :offset"
    )
    suspend fun getPagedListBarcode(barcode: String, limit: Int, offset: Int): List<DatabaseDisc>

    @Query("SELECT * FROM disc_table WHERE barcode = :barcode")
    suspend fun getListDiscDbScan(barcode: String): List<DatabaseDisc>

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE (nameNormalize LIKE '%' || :name || '%' OR titleNormalize LIKE '%' || :title || '%') " +
                "AND year = :year " +
                "AND addBy = :addBy "
    )
    suspend fun getDiscDbManually(
        name: String,
        title: String,
        year: String,
        addBy: Int
    ): DatabaseDisc?

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE name = :name " +
                "AND title = :title " +
                "AND year = :year " +
                "AND idDisc = :idDisc " +
                "AND addBy = :addBy "
    )
    suspend fun getDiscDbSearch(
        idDisc: Int,
        name: String,
        title: String,
        year: String,
        addBy: Int
    ): DatabaseDisc?

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE (nameNormalize LIKE '%' || :name || '%' OR titleNormalize LIKE '%' || :title || '%') " +
                "AND year = :year " +
                "ORDER BY nameNormalize ASC"
    )
    suspend fun getListDiscDbPresent(
        name: String,
        title: String,
        year: String
    ): List<DatabaseDisc>

    @Query(
        "SELECT * FROM disc_table " +
                "WHERE (nameNormalize LIKE '%' || :name || '%' OR titleNormalize LIKE '%' || :title || '%') " +
                "AND year = :year " +
                "AND (addBy = :addByManual OR addBy = :addBySearch) " +
                "ORDER BY nameNormalize ASC"
    )
    suspend fun getListDiscDbManuallySearch(
        name: String,
        title: String,
        year: String,
        addByManual: Int,
        addBySearch: Int
    ): List<DatabaseDisc>
}