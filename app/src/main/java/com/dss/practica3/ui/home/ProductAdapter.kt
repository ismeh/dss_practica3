package com.dss.practica3.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.R
import com.dss.practica3.models.Product

class ProductAdapter(
    private val productList: List<Product>,
    private val listener: OnAddToCartClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnAddToCartClickListener {
        fun onAddToCartClick(product: Product)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val addItemToCart: Button = itemView.findViewById(R.id.editItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.textViewName.text = product.name
        holder.textViewPrice.text = "$${product.price}"
        holder.addItemToCart.setOnClickListener {
            listener.onAddToCartClick(product)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}