package com.dss.practica3.ui.cart

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
import com.dss.practica3.adapters.CartAdapter
import com.dss.practica3.api.ApiClient
import com.dss.practica3.api.ApiService
import com.dss.practica3.databinding.FragmentCartBinding
import com.dss.practica3.models.Product
import com.dss.practica3.services.CartService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment(), CartAdapter.OnAddToCartClickListener {

    private var _binding: FragmentCartBinding? = null
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cartViewModel =
            ViewModelProvider(this).get(CartViewModel::class.java)

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerViewCart
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with an empty list
        cartAdapter = CartAdapter(emptyList(), this)
        recyclerView.adapter = cartAdapter

        val root: View = binding.root
//        lifecycleScope.launch {
        fetchCart()
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchCart() {
//        val listCartIds: List<CartItem> = CartService.getItems()
//        val cartIds = listCartIds.joinToString("_") { it.productId.toString() }
        val cartIds = "1_2_3"
        apiService.getProductsById(cartIds).enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos del carrito: $productList")
                    productList?.let {
                        cartAdapter.updateProducts(it)
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