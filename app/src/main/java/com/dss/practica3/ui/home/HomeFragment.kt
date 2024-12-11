package com.dss.practica3.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.adapters.ProductAdapter
import com.dss.practica3.api.ApiClient
import com.dss.practica3.api.ApiService
import com.dss.practica3.databinding.FragmentHomeBinding
import com.dss.practica3.models.Product
import com.dss.practica3.services.CartService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), ProductAdapter.OnAddToCartClickListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configurar RecyclerView para productos
        recyclerView = binding.recyclerViewProducts
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchProducts()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchProducts() {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos: $productList")
                    productList?.let {
                        productAdapter = ProductAdapter(it, this@HomeFragment)
                        recyclerView.adapter = productAdapter
                    }
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
            }
        })
    }

    override fun onAddToCartClick(product: Product) {
        lifecycleScope.launch {
            CartService.addItem(product.id, 1)
            Log.d("CART_SERVICE", "Producto añadido al carrito: ${product.name}")
        }
    }
}