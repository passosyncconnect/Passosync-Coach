package com.pasosync.pasosynccoach.ui.fragments


import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs


import com.google.android.material.snackbar.Snackbar
import com.pasosync.pasosynccoach.R
import com.pasosync.pasosynccoach.databinding.FragmentArticleBinding
import com.pasosync.pasosynccoach.ui.MainActivity
import com.pasosync.pasosynccoach.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var binding:FragmentArticleBinding

    private lateinit var viewModel: MainViewModels
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentArticleBinding.bind(view)
        viewModel = (activity as MainActivity).viewmodel
        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()

            loadUrl(article.url)
        }


    }
}