package com.diego.discoteca.database

import androidx.room.*
import com.diego.discoteca.util.AddBy

@Database(
    entities = [DatabaseDisc::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(AddByConverters::class)
abstract class DiscRoomDatabase : RoomDatabase() {

    abstract fun discDatabaseDao(): DiscDatabaseDao
}

class AddByConverters {

    @TypeConverter
    fun fromAddBy(addBy: AddBy): Int {
        return addBy.code
    }

    @TypeConverter
    fun toAddBy(value: Int): AddBy {
        return AddBy.getByCode(value)
    }
}
