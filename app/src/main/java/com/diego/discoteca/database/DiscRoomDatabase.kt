package com.diego.discoteca.database

import android.content.Context
import androidx.room.*
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.DATABASE_NAME

@Database(
    entities = [DatabaseDisc::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(AddByConverters::class)
abstract class DiscRoomDatabase : RoomDatabase() {

    abstract val discDatabaseDao: DiscDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: DiscRoomDatabase? = null

        fun getInstance(context: Context): DiscRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiscRoomDatabase::class.java,
                    DATABASE_NAME
                )
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
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
