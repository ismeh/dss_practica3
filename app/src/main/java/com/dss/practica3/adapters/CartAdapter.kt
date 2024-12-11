package com.dss.practica3.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.databinding.CartItemBinding
import com.dss.practica3.models.Product

class CartAdapter(
    private var products: List<Product>,
    private val listener: OnAddToCartClickListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnAddToCartClickListener {
        fun onAddToCartClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.textViewName.text = product.name
            binding.addItemToCart.setOnClickListener {
                listener.onAddToCartClick(product)
            }
        }
    }
}