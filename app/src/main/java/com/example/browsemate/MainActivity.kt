package com.example.browsemate

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.browsemate.databinding.ActivityMainBinding
import com.example.browsemate.databinding.FeaturesDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var printJob:PrintJob? = null
    lateinit var binding:ActivityMainBinding
    // companion objects are static objects which are only created once through the lifecycle of activity
    companion object{
        var tabsList : ArrayList<Fragment> = ArrayList()
        private var fullScreenStatus: Boolean=false
        var desktopModeStatus: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabsList.add(HomePageFragment())
        //tabsList.add(SurfFragment())
        binding.myPager.adapter = TabsAdapter(supportFragmentManager,lifecycle)

        // to fix left swipe problem
        binding.myPager.isUserInputEnabled = false

        initializeViews()
        changeFullscreen(false)
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
    fun changeTabs(fragment: Fragment){
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

            var surfFragmentRef:SurfFragment? = null
            try {
                surfFragmentRef = tabsList[binding.myPager.currentItem] as SurfFragment
            }catch (e:Exception){}

            val view = layoutInflater.inflate(R.layout.features_dialog,binding.root,false)
            val dialogBinding=FeaturesDialogBinding.bind(view)
            val dialog = MaterialAlertDialogBuilder(this).setView(view).create()

            dialog.window?.apply {
                attributes.gravity=Gravity.BOTTOM
                attributes.y=29
                //setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
            }
            dialog.show()

            if(fullScreenStatus){
                dialogBinding.fullscreenBtn.setIconTintResource(R.color.my_darkRed)
                dialogBinding.fullscreenBtn.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.my_lightRed))
            }

            if(desktopModeStatus){
                dialogBinding.desktopBtn.setIconTintResource(R.color.my_darkRed)
                dialogBinding.desktopBtn.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.my_lightRed))
            }

            dialogBinding.saveBtn.setOnClickListener {
                dialog.dismiss()
                if(surfFragmentRef != null)
                    saveAsPdf(web = surfFragmentRef.binding.webView)
                else Snackbar.make(binding.root, "No Webpage Opened!\uD83D\uDE03", 3000).show()
            }

            dialogBinding.backBtn.setOnClickListener{
                onBackPressed()
            }

            dialogBinding.forwardBtn.setOnClickListener {
                surfFragmentRef?.apply {
                    if(binding.webView.canGoForward())
                        binding.webView.goForward()
                }
            }

            dialogBinding.desktopBtn.setOnClickListener{

                surfFragmentRef?.binding?.webView?.apply {
                    desktopModeStatus = if(!desktopModeStatus) {
                        settings.userAgentString=null
                        dialogBinding.desktopBtn.setIconTintResource(R.color.my_darkRed)
                        dialogBinding.desktopBtn.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.my_lightRed))
                        true
                    } else {
                        settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Brave Chrome/83.0.4103.116 Safari/537.36"
                        settings.useWideViewPort=true
                        evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null)
                        dialogBinding.desktopBtn.setIconTintResource(R.color.black)
                        dialogBinding.desktopBtn.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.black))
                        false
                    }
                    reload()
                    dialog.dismiss()
                }

            }

            dialogBinding.fullscreenBtn.setOnClickListener{
                fullScreenStatus = if(!fullScreenStatus) {
                    changeFullscreen(true)
                    dialogBinding.fullscreenBtn.setIconTintResource(R.color.my_darkRed)
                    dialogBinding.fullscreenBtn.setTextColor(ContextCompat.getColor(this,R.color.my_lightRed))
                    true
                } else {
                    changeFullscreen(false)
                    dialogBinding.fullscreenBtn.setIconTintResource(R.color.black)
                    dialogBinding.fullscreenBtn.setTextColor(ContextCompat.getColor(this,R.color.black))
                    false
                }
            }


        }


    }
    private  fun saveAsPdf(web: WebView){
        val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "${URL(web.url).host}_${
            SimpleDateFormat("HH:mm d_MMM_yy", Locale.ENGLISH)
                .format(Calendar.getInstance().time)}"
        val printAdapter = web.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder()
        printJob = pm.print(jobName, printAdapter, printAttributes.build())
    }
    override fun onResume() {
        super.onResume()
        printJob?.let {
            when{
                it.isCompleted -> Snackbar.make(binding.root, "Successful -> ${it.info.label}", 4000).show()
                it.isFailed -> Snackbar.make(binding.root, "Failed -> ${it.info.label}", 4000).show()
            }
        }
    }
    private fun changeFullscreen(enable: Boolean){
        if(enable){
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, binding.root).show(WindowInsetsCompat.Type.systemBars())
        }
    }

}