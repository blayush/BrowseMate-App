package com.example.browsemate

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.example.browsemate.databinding.FragmentSurfBinding

class SurfFragment(private var webUrl : String) : Fragment() {

    lateinit var binding: FragmentSurfBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_surf,container,false)
        binding= FragmentSurfBinding.bind(view)

        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        binding.webView.apply {
            val mainActivityRef = requireActivity() as MainActivity


            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            webViewClient = object : WebViewClient() {

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    if(MainActivity.desktopModeStatus) evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                            " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null)


                }
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    mainActivityRef.binding.topSearchBarInput.setTextColor(Color.parseColor("#696969"))
                    mainActivityRef.binding.topSearchBarInput.setText(url)
                    binding.webView.zoomOut()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    mainActivityRef.binding.progressBarIndicator.progress=0
                    mainActivityRef.binding.progressBarIndicator.visibility=View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    mainActivityRef.binding.progressBarIndicator.visibility=View.GONE
                }
            }
            webChromeClient = object : WebChromeClient(){
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    binding.webView.visibility=View.GONE
                    binding.customView.visibility=View.VISIBLE
                    binding.customView.addView(view)
                    mainActivityRef.binding.root.transitionToEnd()
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    binding.webView.visibility=View.VISIBLE
                    binding.customView.visibility=View.GONE
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    mainActivityRef.binding.progressBarIndicator.progress=newProgress
                }

                @SuppressLint("ClickableViewAccessibility")
                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    super.onReceivedIcon(view, icon)
                    try{
                        mainActivityRef.binding.iconImageView.setImageBitmap(icon)
                    }catch (e:Exception){}

                    binding.webView.setOnTouchListener{ _, motionEvent ->
                        mainActivityRef.binding.root.onTouchEvent(motionEvent)
                        return@setOnTouchListener false
                    }
                }


            }

            when {
                URLUtil.isValidUrl(webUrl) -> loadUrl(webUrl)
                webUrl.contains(".com", ignoreCase = true) -> loadUrl(webUrl)
                else -> loadUrl("https://www.google.com/search?q=$webUrl")
            }

        }
    }

    override fun onPause() {
        super.onPause()
        binding.webView.apply {
            clearMatches()
            clearFormData()
            clearHistory()
            clearSslPreferences()
            clearCache(true)

            CookieManager.getInstance().removeAllCookies(null)
            WebStorage.getInstance().deleteAllData()

        }
    }
}