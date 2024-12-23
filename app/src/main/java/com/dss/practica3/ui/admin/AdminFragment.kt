package com.dss.practica3.ui.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dss.practica3.R
import com.dss.practica3.api.ApiClient
import com.dss.practica3.api.ApiService
import com.dss.practica3.databinding.FragmentAdminBinding
import com.dss.practica3.models.Product
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFragment : Fragment(), AdminAdapter.OnEditItemClickListener,
    AdminAdapter.OnDeleteItemClickListener {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adminAdapter: AdminAdapter
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =
            requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("token", null)
        val username = sharedPreferences.getString("username", null)
        Log.i("Admin", "Initializing: ")

        if (token == null || username == null) {
            showLoginScreen()
        } else {
            checkPrivileges(token)
        }
    }

    private fun showLoginScreen() {
        Log.i("Admin", "showLoginScreen: ")

        binding.loginLayout.visibility = View.VISIBLE
        binding.adminLayout.visibility = View.GONE
        binding.userLayout.visibility = View.GONE
        binding.logoutButton.visibility = View.GONE

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        Log.i("Admin", "login: ")

        apiService.login(username, password).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                Log.i("Admin", "response: ")

                if (response.isSuccessful) {
                    Log.i("Admin", "response.isSuccessful: ")

                    val token = response.body()?.get("token")
                    sharedPreferences.edit().putString("token", token)
                        .putString("username", username).apply()
                    checkPrivileges(token!!)
                } else {
                    Log.i("Admin", "NOT response.isSuccessful: ")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("Admin", "Login failed: ${t.message}")
                Log.i("Admin", "LOGIN FAILURE: ")
            }
        })
    }

    private fun checkPrivileges(token: String) {
        Log.i("Admin", "checkPrivileges: ")

        apiService.checkPrivileges(token).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val hasPrivileges = response.body() ?: false
                    if (hasPrivileges) {
                        showAdminScreen()
                    } else {
                        showUserScreen()
                    }
                } else {
                    // Handle error
                    Log.e(
                        "AdminFragment",
                        "Error checking privileges: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("AdminFragment", "Error checking privileges")
            }
        })
    }

    private fun fetchProducts() {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos: $productList")
                    productList?.let {
                        adminAdapter.updateProducts(it)
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

    private fun showAdminScreen() {
        Log.i("Admin", "showAdminScreen: ")

        binding.loginLayout.visibility = View.GONE
        binding.adminLayout.visibility = View.VISIBLE
        binding.userLayout.visibility = View.GONE
        binding.logoutButton.visibility = View.VISIBLE

        recyclerView = binding.adminRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adminAdapter = AdminAdapter(emptyList(), this@AdminFragment, this@AdminFragment)
        recyclerView.adapter = adminAdapter

        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos: $productList")
                    productList?.let {
                        adminAdapter.updateProducts(it)
                    }
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
            }
        })

        binding.addButton.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_product, null)
            val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
            val editTextPrice = dialogView.findViewById<EditText>(R.id.editTextPrice)

            AlertDialog.Builder(requireContext())
                .setTitle("Añadir Producto")
                .setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val newName = editTextName.text.toString()
                    val newPrice = editTextPrice.text.toString().toDoubleOrNull()

                    if (newName.isNotEmpty() && newPrice != null) {
                        val token = sharedPreferences.getString("token", null).toString()
                        lifecycleScope.launch {
                            apiService.addProduct(token, newName, newPrice)
                                .enqueue(object : Callback<Integer> {
                                    override fun onResponse(
                                        call: Call<Integer>,
                                        response: Response<Integer>
                                    ) {
                                        if (response.isSuccessful) {
                                            Log.d(
                                                "API_RESPONSE",
                                                "Producto añadido: ${response.body()}"
                                            )
                                            fetchProducts() // Actualizar la lista de productos
                                        } else {
                                            Log.e("API_ERROR", "Error code: ${response.code()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<Integer>, t: Throwable) {
                                        Log.e("API_ERROR", "Failure: ${t.message}")
                                    }
                                })
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun showUserScreen() {
        Log.i("Admin", "showUserScreen: ")

        binding.loginLayout.visibility = View.GONE
        binding.adminLayout.visibility = View.GONE
        binding.userLayout.visibility = View.VISIBLE
        binding.logoutButton.visibility = View.VISIBLE

        val username = sharedPreferences.getString("username", "")
        binding.usernameTextView.text = username

        binding.logoutButton.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            showLoginScreen()
        }
    }

    override fun onEditItemClick(product: Product) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_product, null)
        val textViewId = dialogView.findViewById<TextView>(R.id.textViewId)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextPrice = dialogView.findViewById<EditText>(R.id.editTextPrice)

        textViewId.text = "ID: ${product.id}"
        editTextName.setText(product.name)
        editTextPrice.setText(product.price.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = editTextName.text.toString()
                val newPrice = editTextPrice.text.toString().toDoubleOrNull()

                if (newName.isNotEmpty() && newPrice != null) {
                    val token = sharedPreferences.getString("token", null).toString()
                    lifecycleScope.launch {
                        apiService.editProduct(token, product.id, newName, newPrice)
                            .enqueue(object : Callback<Integer> {
                                override fun onResponse(
                                    call: Call<Integer>,
                                    response: Response<Integer>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d(
                                            "API_RESPONSE",
                                            "Producto editado: ${response.body()}"
                                        )
                                        fetchProducts() // Actualizar la lista de productos
                                    } else {
                                        Log.e("API_ERROR", "Error code: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<Integer>, t: Throwable) {
                                    Log.e("API_ERROR", "Failure: ${t.message}")
                                }
                            })
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDeleteItemClick(product: Product) {
        val token = sharedPreferences.getString("token", null).toString()

        lifecycleScope.launch {
            apiService.deleteProduct(token, product.id).enqueue(object : Callback<Integer> {
                override fun onResponse(call: Call<Integer>, response: Response<Integer>) {
                    if (response.isSuccessful) {
                        Log.d("API_RESPONSE", "Producto eliminado " + response.body())
                        fetchProducts() // Actualizar la lista de productos
                    } else {
                        Log.e("API_ERROR", "Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Integer>, t: Throwable) {
                    Log.e("API_ERROR", "Failure: ${t.message}")
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}