package com.example.browsemate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
            override fun onQueryTextSubmit(url: String): Boolean {

                if (mainActivityRef.checkForInternet(requireContext()))
                    mainActivityRef.changeTabs(SurfFragment(url))
                else
                    Snackbar.make(binding.root, "Not Connected to Internet \uD83D\uDE10", 3500)
                        .show()

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean = false
        })
        mainActivityRef.binding.GoButton.setOnClickListener {
            if (mainActivityRef.checkForInternet(requireContext()))
                mainActivityRef.changeTabs(SurfFragment(mainActivityRef.binding.topSearchBarInput.text.toString()))
            else
                Snackbar.make(binding.root, "Not Connected to Internet \uD83D\uDE10", 3500).show()

        }
        mainActivityRef.binding.topSearchBarInput

        MainActivity.bookmarkList.add(Bookmark("Google","www.google.com"))
        MainActivity.bookmarkList.add(Bookmark("Facebook","www.facebook.com"))
        MainActivity.bookmarkList.add(Bookmark("Amazon","www.amazon.com"))
        MainActivity.bookmarkList.add(Bookmark("Zomato","www.zomato.com"))
        MainActivity.bookmarkList.add(Bookmark("Bing!","www.bing.com"))
        MainActivity.bookmarkList.add(Bookmark("Yahoo!","www.yahoo.com"))

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager= GridLayoutManager(requireContext(),5)
        binding.recyclerView.adapter=BookmarkAdapter(requireContext())
    }

}