package com.example.appqlchitieu.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private const val DB_NAME = "app_database"

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration() //  đổi schema là reset DB luôn
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()

            INSTANCE = instance
            instance
        }
    }
}
