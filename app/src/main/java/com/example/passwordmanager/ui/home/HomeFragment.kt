package com.example.passwordmanager.ui.home

import HomeViewModel
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.DocumentListAdapter
import com.example.passwordmanager.DocumentRequest
import com.example.passwordmanager.R
import com.example.passwordmanager.RetrofitClient
import com.example.passwordmanager.UpdateDocumentRequest
import com.example.passwordmanager.UserDocumentsRequest
import com.example.passwordmanager.UserDocumentsResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var tokenText = ""
    private var userIdText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val buttonGet = root.findViewById<FloatingActionButton>(R.id.button_add)

        homeViewModel.token.observe(viewLifecycleOwner, Observer { token ->
            tokenText = token
            checkAndFetchDocuments(root)
        })

        homeViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            userIdText = userId
            checkAndFetchDocuments(root)
        })

        buttonGet.setOnClickListener {
            if (tokenText.isNotEmpty() && userIdText.isNotEmpty()) {
                showDocumentNameDialog()
            } else {
                Toast.makeText(requireContext(), "Token y User ID cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun checkAndFetchDocuments(root: View) {
        if (tokenText.isNotEmpty() && userIdText.isNotEmpty()) {
            val requestBody = UserDocumentsRequest(user = userIdText)

            val apiService = RetrofitClient.getApiServiceWithToken(tokenText)
            apiService.getUserDocuments(requestBody).enqueue(object : Callback<UserDocumentsResponse> {
                override fun onResponse(call: Call<UserDocumentsResponse>, response: Response<UserDocumentsResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val documents = apiResponse?.documents ?: emptyList()

                        val mutableDocuments = documents.toMutableList()

                        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        val adapter = DocumentListAdapter(requireContext(), mutableDocuments, tokenText, userIdText)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(requireContext(), "Error in API", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDocumentsResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failure in API call", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showDocumentNameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Document Name")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val documentName = input.text.toString()
            addDocument(documentName)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addDocument(documentName: String) {

        val apiService = RetrofitClient.getApiServiceWithToken(tokenText)
        val data: Map<String, String> = mapOf("myUser" to "myPassword")
        val requestBody = UpdateDocumentRequest(user = userIdText, document_name = documentName, data = data)
        apiService.updateDocument(requestBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Document added successfully", Toast.LENGTH_SHORT).show()
                    checkAndFetchDocuments(view!!)
                } else {
                    Toast.makeText(requireContext(), "API error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "Failure i API call", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
