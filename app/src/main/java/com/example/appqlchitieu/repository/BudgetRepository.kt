package com.example.appqlchitieu.repository

import com.example.appqlchitieu.dao.BudgetDao
import com.example.appqlchitieu.model.Budget
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.first

class BudgetRepository(private val dao: BudgetDao) {

    fun allBudgets(userId: Int) = dao.getAllBudgets(userId)

    suspend fun getAllOnce(userId: Int) =
        dao.getAllBudgetsOnce(userId)

    fun byCategory(userId: Int, categoryId: Int) =
        dao.getBudgetsByCategory(userId, categoryId)

    fun activeAt(userId: Int, currentDate: Long) =
        dao.getActiveBudgets(userId, currentDate)

    suspend fun insert(budget: Budget) = dao.insertBudget(budget)
    suspend fun update(budget: Budget) = dao.updateBudget(budget)
    suspend fun delete(budget: Budget) = dao.deleteBudget(budget)
}
