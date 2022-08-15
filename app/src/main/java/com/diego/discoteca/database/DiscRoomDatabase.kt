package com.diego.discoteca.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diego.discoteca.util.DATABASE_NAME

@Database(
    entities = [DatabaseDisc::class],
    version = 1
)
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