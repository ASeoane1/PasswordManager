package com.example.passwordmanager.ui.home

import HomeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.token.observe(viewLifecycleOwner, Observer { token ->
            val tokenText = root.findViewById<TextView>(R.id.text_home2)
            tokenText.text = token
        })

        homeViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            val id = root.findViewById<TextView>(R.id.text_home)
            id.text = userId
        })

        return root
    }
}

