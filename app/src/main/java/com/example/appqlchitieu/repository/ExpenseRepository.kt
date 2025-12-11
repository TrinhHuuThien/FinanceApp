package com.example.appqlchitieu.repository

import com.example.appqlchitieu.dao.ExpenseDao
import com.example.appqlchitieu.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // ✅ NEW: Tất cả giao dịch theo user
    fun allExpenses(userId: Int): Flow<List<Expense>> =
        expenseDao.getAllExpenses(userId)

    suspend fun getAllOnce(userId: Int) =
        expenseDao.getAllExpensesOnce(userId)

    suspend fun insert(expense: Expense) =
        expenseDao.insertExpense(expense)

    suspend fun update(expense: Expense) =
        expenseDao.updateExpense(expense)

    suspend fun delete(expense: Expense) =
        expenseDao.deleteExpense(expense)

    // ✅ NEW: getById theo user
    suspend fun getById(userId: Int, id: Int): Expense? =
        expenseDao.getExpenseById(userId, id)

    // ✅ NEW: Lọc theo category + user
    fun byCategory(userId: Int, categoryId: Int): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(userId, categoryId)

    // ✅ NEW: Lọc theo date range + user
    fun byDateRange(userId: Int, start: Long, end: Long): Flow<List<Expense>> =
        expenseDao.getExpensesByDateRange(userId, start, end)

    // ✅ NEW: Tổng chi/thu theo user
    fun totalExpense(userId: Int): Flow<Double> =
        expenseDao.getTotalExpense(userId).map { it ?: 0.0 }

    fun totalIncome(userId: Int): Flow<Double> =
        expenseDao.getTotalIncome(userId).map { it ?: 0.0 }

    // ✅ Tiện ích theo user (giữ lại cho bạn đỡ sửa nhiều nơi)
    fun expensesBetween(userId: Int, start: Long, end: Long): Flow<List<Expense>> =
        allExpenses(userId).map { it.filter { e -> e.date in start..end } }

    fun totalExpenseBetween(userId: Int, start: Long, end: Long): Flow<Double> =
        expensesBetween(userId, start, end)
            .map { list -> list.filter { it.type == "expense" }.sumOf { it.amount } }

    fun totalIncomeBetween(userId: Int, start: Long, end: Long): Flow<Double> =
        expensesBetween(userId, start, end)
            .map { list -> list.filter { it.type == "income" }.sumOf { it.amount } }
}
