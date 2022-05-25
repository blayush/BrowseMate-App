package com.example.browsemate

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.browsemate.databinding.ActivityMainBinding
import com.example.browsemate.databinding.FeaturesDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    // companion objects are static objects which are only created once through the lifecycle of activity
    companion object{
        var tabsList : ArrayList<Fragment> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabsList.add(HomePageFragment())
        //tabsList.add(SurfFragment())
        binding.myPager.adapter = TabsAdapter(supportFragmentManager,lifecycle)

        // to fix left swipe problem
        binding.myPager.isUserInputEnabled = false

        initializeViews()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {
        var surfFragmentRef:SurfFragment? = null
        try {
            surfFragmentRef = tabsList[binding.myPager.currentItem] as SurfFragment
        }catch (e:Exception){}
        when{
            surfFragmentRef?.binding?.webView?.canGoBack() == true -> surfFragmentRef.binding.webView.goBack()
            binding.myPager.currentItem!=0 -> {
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1
            }
            else -> super.onBackPressed()
        }
    }

    // for ViewPager2
    private inner class TabsAdapter(fa: FragmentManager, lc:Lifecycle) : FragmentStateAdapter(fa,lc) {
        override fun getItemCount(): Int = tabsList.size

        override fun createFragment(position: Int): Fragment = tabsList[position]
    }


    // for changing tabs
    @SuppressLint("NotifyDataSetChanged")
    fun changeTabs(url: String, fragment: Fragment){
        tabsList.add(fragment)
        binding.myPager.adapter?.notifyDataSetChanged()
        binding.myPager.currentItem = tabsList.size - 1
    }

    // for checking internet state
    fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    // to initialize views
    private fun initializeViews(){
        binding.settingsButton.setOnClickListener{
            val view = layoutInflater.inflate(R.layout.features_dialog,binding.root,false)
            val dialogBinding=FeaturesDialogBinding.bind(view)
            val dialog = MaterialAlertDialogBuilder(this).setView(view).create()

            dialog.window?.apply {
                attributes.gravity=Gravity.BOTTOM
                attributes.y=52
                //setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
            }
            dialog.show()
        }
    }
}