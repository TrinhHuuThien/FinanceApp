package com.example.appqlchitieu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appqlchitieu.dao.*
import com.example.appqlchitieu.model.*

@Database(
    entities = [
        Expense::class,
        Category::class,
        Wallet::class,
        Budget::class,
        User::class,
        AIChat::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun walletDao(): WalletDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao
    abstract fun aiChatDao(): AIChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()   // tránh crash khi tăng version
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
