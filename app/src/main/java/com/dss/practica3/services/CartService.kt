package com.dss.practica3.services

import android.content.Context
import com.dss.practica3.database.AppDatabase
import com.dss.practica3.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CartService {
    private lateinit var db: AppDatabase

    fun initialize(context: Context) {
        db = AppDatabase.getDatabase(context)
    }

    suspend fun addItem(productId: Long, quantity: Int) {
        withContext(Dispatchers.IO) {
            val item = db.cartItemDao().getAll().find { it.productId == productId }
            if (item != null) {
                item.quantity += quantity
                db.cartItemDao().insert(item)
            } else {
                db.cartItemDao().insert(CartItem(productId, quantity))
            }
        }
    }

    suspend fun removeItem(productId: Long, quantity: Int) {
        withContext(Dispatchers.IO) {
            val item = db.cartItemDao().getAll().find { it.productId == productId }
            if (item != null) {
                if (item.quantity > 1) {
                    item.quantity -= quantity
                    db.cartItemDao().insert(item)
                } else {
                    db.cartItemDao().delete(item)
                }
            }
        }
    }

    suspend fun getItems(): List<CartItem> = withContext(Dispatchers.IO) {
        db.cartItemDao().getAll()
    }

    suspend fun getItemQuantity(productId: Long): Int = withContext(Dispatchers.IO) {
        db.cartItemDao().getAll().find { it.productId == productId }?.quantity ?: 0
    }
}