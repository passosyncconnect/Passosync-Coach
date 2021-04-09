package com.pasosync.pasosynccoach.ui.fragments


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected


import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FragmentArticleBinding
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var binding: FragmentArticleBinding

    private lateinit var viewModel: MainViewModels
    val args: ArticleFragmentArgs by navArgs()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v:View=inflater.inflate(R.layout.fragment_article,container,false)
        val webview:WebView=v.findViewById(R.id.webView)
        webview.settings.javaScriptEnabled=true
        webview.webViewClient= WebViewClient()
        webview.loadUrl(args.article.url)


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        viewModel = (activity as MainActivity).viewmodel
        val article = args.article
        Log.d("Article", "onViewCreated:${article.url} ")
//        binding.webView.apply {
//            webViewClient = WebViewClient()
//            this.settings.javaScriptEnabled = false
//            this.settings.builtInZoomControls=false
//            this.loadUrl(article.url)
//
//        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}