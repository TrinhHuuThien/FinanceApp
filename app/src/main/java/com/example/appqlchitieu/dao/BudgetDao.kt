package com.example.appqlchitieu.dao

import androidx.room.*
import com.example.appqlchitieu.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    // ALL budgets theo user
    @Query("""
        SELECT * FROM budget_table
        WHERE userId = :userId
        ORDER BY startDate DESC
    """)
    fun getAllBudgets(userId: Int): Flow<List<Budget>>

    @Query("SELECT * FROM budget_table WHERE userId = :userId")
    suspend fun getAllBudgetsOnce(userId: Int): List<Budget>


    // budgets theo category + user
    @Query("""
        SELECT * FROM budget_table
        WHERE userId = :userId AND categoryId = :categoryId
        ORDER BY startDate DESC
    """)
    fun getBudgetsByCategory(userId: Int, categoryId: Int): Flow<List<Budget>>

    // budgets còn hiệu lực + user
    @Query("""
        SELECT * FROM budget_table
        WHERE userId = :userId
          AND startDate <= :currentDate
          AND endDate >= :currentDate
        ORDER BY endDate ASC
    """)
    fun getActiveBudgets(userId: Int, currentDate: Long): Flow<List<Budget>>
}
