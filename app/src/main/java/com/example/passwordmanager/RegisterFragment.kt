package com.example.passwordmanager

import HomeViewModel
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import androidx.navigation.fragment.findNavController
import com.android.volley.DefaultRetryPolicy

class RegisterFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val buttonRegister = view.findViewById<Button>(R.id.buttonRegister)
        val buttonLogin = view.findViewById<Button>(R.id.buttonLogin)
        val textEmail = view.findViewById<EditText>(R.id.textEmailAddress)
        val textPassword = view.findViewById<EditText>(R.id.textPassword)
        val textPasswordRepeat = view.findViewById<EditText>(R.id.textPasswordRepeat)

        if((textPassword.text.toString() != textPasswordRepeat.text.toString()) && textPassword.text.toString() != ""){
            buttonRegister.setEnabled(false)
        }

        buttonRegister.setOnClickListener {
            // Obtain text field values
            val email = textEmail.text.toString()
            val password = textPassword.text.toString()
            val passwordRepeat = textPasswordRepeat.text.toString()

            if(password!=passwordRepeat && password != ""){
                Toast.makeText(requireContext(), "Provide a valid email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Generate payload
                val jsonObject = JSONObject()
                jsonObject.put("email", email)
                jsonObject.put("password", password)

                val requestBody = jsonObject.toString()

                //val apiUrl = "https://proyecto-cumn-back-66f34t4snq-no.a.run.app/auth/register"
                val apiUrl = "http://100.83.62.114:8080/auth/register"
                val requestQueue = Volley.newRequestQueue(requireContext())

                val stringRequest = object : StringRequest(
                    Method.POST, apiUrl,
                    Response.Listener<String> { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            val message = jsonResponse.getString("message")

                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                            textEmail.setText("")
                            textPassword.setText("")
                            textPasswordRepeat.setText("")

                            findNavController().navigate(R.id.nav_login)


                        } catch (e: JSONException) {
                            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(requireContext(), "Error during Sing Up", Toast.LENGTH_SHORT).show()
                        textPassword.setText("")
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json"
                    }

                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }

                //Configure timeout
                val timeout = 20000;
                stringRequest.setRetryPolicy(DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                // Queue request
                requestQueue.add(stringRequest)
            }

        }

        return view
    }
}