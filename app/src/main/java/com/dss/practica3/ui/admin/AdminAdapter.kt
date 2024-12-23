package com.dss.practica3.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.databinding.AdminItemBinding
import com.dss.practica3.models.Product

class AdminAdapter(
    private var products: List<Product>,
    private val editItemClickListener: OnEditItemClickListener,
    private val deleteItemClickListener: OnDeleteItemClickListener
) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    interface OnEditItemClickListener {
        fun onEditItemClick(product: Product)
    }

    interface OnDeleteItemClickListener {
        fun onDeleteItemClick(product: Product)
    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val binding = AdminItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class AdminViewHolder(private val binding: AdminItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.textViewName.text = product.name
            binding.textViewPrice.text = "$${product.price}"

            binding.editItem.setOnClickListener {
                editItemClickListener.onEditItemClick(product)
            }

            binding.deleteItem.setOnClickListener {
                deleteItemClickListener.onDeleteItemClick(product)
            }
        }
    }
}