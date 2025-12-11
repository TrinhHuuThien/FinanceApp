package com.example.appqlchitieu.dao

import androidx.room.*
import com.example.appqlchitieu.model.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    //  Lấy danh sách ví theo user
    @Query("SELECT * FROM wallet_table WHERE userId = :userId")
    fun getAllWallets(userId: Int): Flow<List<Wallet>>

    @Query("SELECT * FROM wallet_table WHERE userId = :userId")
    suspend fun getAllWalletsOnce(userId: Int): List<Wallet>

    // Lấy 1 ví theo id + user
    @Query("SELECT * FROM wallet_table WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getWalletById(userId: Int, id: Int): Wallet?

    // Update số dư an toàn theo user + delta
    @Query("""
        UPDATE wallet_table
        SET balance = balance + :delta
        WHERE userId = :userId AND id = :walletId
    """)

    suspend fun updateBalanceDelta(userId: Int, walletId: Int, delta: Double)
}
