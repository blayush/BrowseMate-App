package com.example.browsemate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.browsemate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    // companion objects are static objects which are only created once through the lifecycle of activity
    companion object{
        var tabsList:ArrayList<Fragment> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabsList.add(HomePageFragment())
        binding.myPager.adapter = TabsAdapter(supportFragmentManager,lifecycle)
    }

    // for ViewPager2
    private inner class TabsAdapter(fa: FragmentManager, lc:Lifecycle) : FragmentStateAdapter(fa,lc) {
        override fun getItemCount(): Int = tabsList.size

        override fun createFragment(position: Int): Fragment = tabsList[position]
    }

}