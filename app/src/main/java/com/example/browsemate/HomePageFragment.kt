package com.example.browsemate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.browsemate.databinding.FragmentHomePageBinding
import com.google.android.material.snackbar.Snackbar


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
        val mainActivityRef = requireActivity() as MainActivity
        mainActivityRef.binding.topSearchBarInput.setText("")
        binding.homesearchVIew.setQuery("",false)
        mainActivityRef.binding.iconImageView.setImageResource(R.drawable.ic_search)
        binding.homesearchVIew.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(url: String?): Boolean {

                if(mainActivityRef.checkForInternet(requireContext()))
                    mainActivityRef.changeTabs(url!!,SurfFragment(url))
                else
                    Snackbar.make(binding.root,"Not Connected to Internet \uD83D\uDE10",3500).show()

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean = false
        })
        mainActivityRef.binding.GoButton.setOnClickListener{
            if(mainActivityRef.checkForInternet(requireContext()))
                mainActivityRef.changeTabs(mainActivityRef.binding.topSearchBarInput.text.toString(),SurfFragment(mainActivityRef.binding.topSearchBarInput.text.toString()))
            else
                Snackbar.make(binding.root,"Not Connected to Internet \uD83D\uDE10",3500).show()

        }
        mainActivityRef.binding.topSearchBarInput
    }

}