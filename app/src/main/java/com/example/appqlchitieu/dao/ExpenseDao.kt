package com.example.appqlchitieu.dao

import androidx.room.*
import com.example.appqlchitieu.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    //  Tất cả giao dịch theo user
    @Query("SELECT * FROM expense_table WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpenses(userId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expense_table WHERE userId = :userId")
    suspend fun getAllExpensesOnce(userId: Int): List<Expense>

    //  Lấy 1 giao dịch theo id + user
    @Query("SELECT * FROM expense_table WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getExpenseById(userId: Int, id: Int): Expense?

    //  Lọc theo danh mục + user
    @Query("""
        SELECT * FROM expense_table
        WHERE userId = :userId AND categoryId = :categoryId
        ORDER BY date DESC
    """)
    fun getExpensesByCategory(userId: Int, categoryId: Int): Flow<List<Expense>>

    // Lọc theo khoảng ngày + user
    @Query("""
        SELECT * FROM expense_table
        WHERE userId = :userId AND date BETWEEN :start AND :end
        ORDER BY date DESC
    """)
    fun getExpensesByDateRange(userId: Int, start: Long, end: Long): Flow<List<Expense>>

    //  Tổng chi theo user
    @Query("SELECT SUM(amount) FROM expense_table WHERE userId = :userId AND type = 'expense'")
    fun getTotalExpense(userId: Int): Flow<Double?>

    //  Tổng thu theo user
    @Query("SELECT SUM(amount) FROM expense_table WHERE userId = :userId AND type = 'income'")
    fun getTotalIncome(userId: Int): Flow<Double?>

    //  Đếm giao dịch theo danh mục + user
    @Query("SELECT COUNT(*) FROM expense_table WHERE userId = :userId AND categoryId = :categoryId")
    suspend fun countByCategory(userId: Int, categoryId: Int): Int
}
