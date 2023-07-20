package com.example.mvvmnetwork.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmnetwork.R
import com.example.mvvmnetwork.data.viewmodels.NewsViewModel
import com.example.mvvmnetwork.databinding.FragmentNewsListBinding

class NewsListFragment : Fragment(), NewsListAdapter.OnArticleClickedListener {
    private var _binding: FragmentNewsListBinding? = null
    private var viewModel: NewsViewModel? = null
    lateinit var newsAdapter: NewsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsListBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        viewModel?.getNewsList()
        newsAdapter = NewsListAdapter(this)
        viewModel?.newsList?.observe(viewLifecycleOwner,{
            _binding?.rvNews?.visibility = View.VISIBLE
            _binding?.progressBar?.visibility = View.GONE
            _binding?.tvMsg?.visibility = View.VISIBLE
            newsAdapter.submitList(it)
        })
        _binding?.rvNews?.layoutManager = LinearLayoutManager(context)
        _binding?.rvNews?.adapter = newsAdapter

    }

    fun openArticle(url: String){
        findNavController().navigate(
            R.id.action_newsList_to_articleFragment,
            bundleOf("article_url" to url)
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(url: String) = openArticle(url)
}