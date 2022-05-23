package com.example.browsemate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.browsemate.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {

    private lateinit var binding:FragmentHomePageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home_page,container,false)
        binding=FragmentHomePageBinding.bind(view)
        return view
    }

}