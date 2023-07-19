package com.example.mvvmnetwork.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.mvvmnetwork.databinding.FragmentArticleBinding


class ArticleFragment : Fragment() {
    lateinit var _binding: FragmentArticleBinding
    var url: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater,container,false)
        url = arguments?.getString("article_url")
        Toast.makeText(this.requireContext(),url!!+"",Toast.LENGTH_SHORT).show()
        _binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(arguments?.getString("article_url")!!)
        }
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}