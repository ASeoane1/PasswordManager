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


class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val buttonLogin = view.findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = view.findViewById<Button>(R.id.buttonRegister)
        val textEmail = view.findViewById<EditText>(R.id.textEmailAddress)
        val textPassword = view.findViewById<EditText>(R.id.textPassword)

        buttonLogin.setOnClickListener {
            // Obtain text field values
            val email = textEmail.text.toString()
            val password = textPassword.text.toString()

            // Generate payload
            val jsonObject = JSONObject()
            jsonObject.put("email", email)
            jsonObject.put("password", password)

            val requestBody = jsonObject.toString()

            //val apiUrl = "https://proyecto-cumn-back-66f34t4snq-no.a.run.app/auth/authenticate"
            val apiUrl = "http://100.83.62.114:8080/auth/authenticate"
            val requestQueue = Volley.newRequestQueue(requireContext())

            val stringRequest = object : StringRequest(
                Method.POST, apiUrl,
                Response.Listener<String> { response ->
                    try {
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.getString("token")
                        val userId = jsonResponse.getString("user_id")

                        //Create shared preference for login data
                        val userDataPreference = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
                        with(userDataPreference.edit()) {
                            putString("token", token)
                            putString("userId", userId)
                            apply()
                        }



                        //Modify Drawer
                        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)

                        val headerView = navView.getHeaderView(0)
                        val subtitle = headerView.findViewById<TextView>(R.id.textEmail)
                        subtitle.text = email

                        val menu = navView?.menu
                        val itemLogin = menu?.findItem(R.id.nav_login)
                        val itemRegister = menu?.findItem(R.id.nav_register)
                        val itemLogout = menu?.findItem(R.id.nav_logout)

                        itemLogin?.setTitle("Switch Account")
                        itemLogout?.isVisible = true
                        itemRegister?.isVisible = false




                        val homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
                        homeViewModel.updateToken(token)
                        homeViewModel.updateUserId(userId)

                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                        textEmail.setText("")
                        textPassword.setText("")

                        findNavController().navigate(R.id.nav_home)


                    } catch (e: JSONException) {
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(requireContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show()
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

        buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }

        return view
    }


}