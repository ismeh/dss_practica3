package com.dss.practica3.ui.cart

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.adapters.CartAdapter
import com.dss.practica3.api.ApiClient
import com.dss.practica3.api.ApiService
import com.dss.practica3.databinding.FragmentCartBinding
import com.dss.practica3.models.CartItem
import com.dss.practica3.models.Product
import com.dss.practica3.services.CartService
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class CartFragment : Fragment(), CartAdapter.OnAddToCartClickListener,
    CartAdapter.OnRemoveToCartClickListener {

    private var _binding: FragmentCartBinding? = null
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    private lateinit var textViewTotalPrice: TextView

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
        cartAdapter = CartAdapter(emptyList(), this, this)
        recyclerView.adapter = cartAdapter


        binding.buttonComprar.setOnClickListener {
            lifecycleScope.launch {
                checkout()
            }
        }

        val root: View = binding.root
        lifecycleScope.launch {
            fetchCart()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun fetchCart() {
        val listCartIds: List<CartItem> = CartService.getItems()
        val cartIds = listCartIds.joinToString("_") { it.productId.toString() }
        apiService.getProductsById(cartIds).enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos del carrito: $productList")
                    productList?.let {
                        cartAdapter.updateProducts(it, listCartIds)
                    }
                    val totalPrice = cartAdapter.getTotalPrice()
                    binding.buttonComprar.text = "Comprar por $${totalPrice}"
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
            }
        })
    }

    // todo: revisar
    private suspend fun checkout(){
        val listCartIds: List<CartItem> = CartService.getItems()
        val cartIds = listCartIds.joinToString("_") { it.productId.toString() }
        apiService.checkout(cartIds).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        // Guardar el archivo en el almacenamiento interno
                        val pdfFile = File(
                            context!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                            "ticket.pdf"
                        )
                        response.body()!!.byteStream().use { inputStream ->
                            FileOutputStream(pdfFile).use { outputStream ->
                                val buffer = ByteArray(4096)
                                var bytesRead: Int
                                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                }
                            }
                        }
                        // Abrir el PDF con un visor
                        val pdfUri = FileProvider.getUriForFile(
                            context!!,
                            context!!.packageName + ".provider",
                            pdfFile
                        )
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(pdfUri, "application/pdf")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        context!!.startActivity(Intent.createChooser(intent, "Abrir archivo PDF"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    // Manejo de errores en la respuesta
                    System.err.println("Error: " + response.message())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                // Manejo de errores de conexión
                t.printStackTrace()
            }

        })
    }

    override fun onAddToCartClick(product: Product) {
        lifecycleScope.launch {
            CartService.addItem(product.id, 1)
            Log.d("CART_SERVICE", "Producto añadido al carrito: ${product.name}")

            val listCartIds: List<CartItem> = CartService.getItems()
            cartAdapter.updateProducts(cartAdapter.getProducts(), listCartIds)

            val totalPrice = cartAdapter.getTotalPrice()
            binding.buttonComprar.text = "Comprar por $${totalPrice}"
        }
    }
    override fun onRemoveToCartClick(product: Product) {
        lifecycleScope.launch {
            CartService.removeItem(product.id, 1)
            Log.d("CART_SERVICE", "Producto borrado al carrito: ${product.name}")
            val listCartIds: List<CartItem> = CartService.getItems()
            cartAdapter.updateProducts(cartAdapter.getProducts(), listCartIds)
            val totalPrice = cartAdapter.getTotalPrice()
            binding.buttonComprar.text = "Comprar por $${totalPrice}"
        }
    }
}