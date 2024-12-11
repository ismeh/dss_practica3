package com.dss.practica3.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dss.practica3.models.CartItem

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items")
    fun getAll(): List<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cartItem: CartItem)

    @Delete
    fun delete(cartItem: CartItem)
}