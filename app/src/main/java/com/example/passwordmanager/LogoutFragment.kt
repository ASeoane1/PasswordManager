package com.example.passwordmanager

import HomeViewModel
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView


class LogoutFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_logout, container, false)

        val buttonLogout = view.findViewById<Button>(R.id.buttonLogout)

        buttonLogout.setOnClickListener {
            //Empty shared preference for login data
            val userDataPreference = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
            with(userDataPreference.edit()) {
                putString("token", "")
                putString("userId", "")
                apply()
            }

            //Modify Drawer options
            val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)

            val headerView = navView.getHeaderView(0)
            val subtitle = headerView.findViewById<TextView>(R.id.textEmail)
            subtitle.text = "Login to start"

            val menu = navView?.menu
            val menuItem = menu?.findItem(R.id.nav_login)
            val itemLogout = menu?.findItem(R.id.nav_logout)

            itemLogout?.isVisible = false

            val homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
            homeViewModel.updateToken("token")
            homeViewModel.updateUserId("userId")

            menuItem?.setTitle("Login")

            Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.nav_home)
        }
        return view
    }

}
