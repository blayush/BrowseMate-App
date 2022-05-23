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

    override fun onResume() {
        super.onResume()

        binding.homesearchVIew.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(url: String?): Boolean {

                (requireActivity() as MainActivity).changeTabs(url!!,SurfFragment(url))

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean = false
        })
    }

}