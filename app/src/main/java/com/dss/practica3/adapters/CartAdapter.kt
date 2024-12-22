package com.dss.practica3.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.databinding.CartItemBinding
import com.dss.practica3.models.CartItem
import com.dss.practica3.models.Product

class CartAdapter(
    private var products: List<Product>,
    private val listener: OnAddToCartClickListener,
    private val listener2: OnRemoveToCartClickListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = emptyList()

    interface OnAddToCartClickListener {
        fun onAddToCartClick(product: Product)
    }

    interface OnRemoveToCartClickListener {
        fun onRemoveToCartClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, cartItems)
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>, newCartItems: List<CartItem>) {
        products = newProducts
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, cartItems: List<CartItem>) {
            binding.textViewName.text = product.name
            binding.textViewPrice.text = "$${product.price}"
            val cartItem = cartItems.find { it.productId == product.id }
            val quantity = cartItem?.quantity ?: 1
            binding.textViewQuantity.text = quantity.toString()

            binding.addItemToCart.setOnClickListener {
                listener.onAddToCartClick(product)
                binding.textViewQuantity.text =
                    (binding.textViewQuantity.text.toString().toInt() + 1).toString()
            }

            binding.removeItemToCart.setOnClickListener {
                listener2.onRemoveToCartClick(product)
                if (binding.textViewQuantity.text.toString().toInt() > 1) {
                    binding.textViewQuantity.text =
                        (binding.textViewQuantity.text.toString().toInt() - 1).toString()
                } else {
                    binding.textViewQuantity.text =
                        (binding.textViewQuantity.text.toString().toInt() - 1).toString()
                    updateProducts(
                        products.filter { it.id != product.id },
                        cartItems.filter { it.productId != product.id })
                }
            }
        }
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { cartItem ->
            val product = products.find { it.id == cartItem.productId }
            product?.price?.times(cartItem.quantity) ?: 0.0
        }
    }

    fun getProducts(): List<Product> {
        return products
    }
}