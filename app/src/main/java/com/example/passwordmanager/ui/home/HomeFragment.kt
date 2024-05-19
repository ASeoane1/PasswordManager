package com.example.passwordmanager.ui.home

import HomeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.DocumentListAdapter
import com.example.passwordmanager.R
import com.example.passwordmanager.RetrofitClient
import com.example.passwordmanager.UserDocumentsRequest
import com.example.passwordmanager.UserDocumentsResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        var tokenText = ""
        var userIdText = ""

        homeViewModel.token.observe(viewLifecycleOwner, Observer { token ->
            tokenText = token
        })

        homeViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            userIdText = userId
        })

        val buttonGet = root.findViewById<FloatingActionButton>(R.id.button_add)

        buttonGet.setOnClickListener {
            if (tokenText.isNotEmpty() && userIdText.isNotEmpty()) {
                val requestBody = UserDocumentsRequest(user = userIdText)

                val apiService = RetrofitClient.getApiServiceWithToken(tokenText)
                apiService.getUserDocuments(requestBody).enqueue(object : Callback<UserDocumentsResponse> {
                    override fun onResponse(call: Call<UserDocumentsResponse>, response: Response<UserDocumentsResponse>) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            val documents = apiResponse?.documents ?: emptyList()

                            val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            val adapter = DocumentListAdapter(requireContext(),documents, tokenText, userIdText)
                            recyclerView.adapter = adapter
                        } else {
                            Toast.makeText(requireContext(), "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserDocumentsResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Fallo en la llamada a la API", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Token y User ID no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
}
