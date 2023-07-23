package com.example.mvvmnetwork.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmnetwork.R
import com.example.mvvmnetwork.data.Resource
import com.example.mvvmnetwork.data.viewmodels.NewsViewModel
import com.example.mvvmnetwork.data.viewmodels.NewsViewModelFactory
import com.example.mvvmnetwork.databinding.FragmentNewsListBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class NewsListFragment : Fragment() {
    val TAG = "BreakingNewsFragment"
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
        setupRecyclerView()
        val viewModelFactory = NewsViewModelFactory(activity?.application!!)
        viewModel = ViewModelProvider(this,viewModelFactory).get(NewsViewModel::class.java)
        viewModel?.newsList?.observe(viewLifecycleOwner,{ response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    newsAdapter.submitList(response.data?.articles)
                }
                is Resource.Error->{
                    hideProgressBarError()
                    response.message?.let { message->
                        _binding?.tvMsg?.text = message
                        Toast.makeText(activity,"Error $message",Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })
        newsAdapter.setOnItemClickListener {
            openArticle(it.url)
        }
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

    fun showProgressBar(){
        _binding?.rvNews?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.VISIBLE
        _binding?.tvMsg?.visibility = View.VISIBLE
    }
    fun hideProgressBar(){
        _binding?.rvNews?.visibility = View.VISIBLE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.tvMsg?.visibility = View.GONE
    }
    fun hideProgressBarError(){
        _binding?.rvNews?.visibility = View.GONE
        _binding?.progressBar?.visibility = View.GONE
        _binding?.tvMsg?.visibility = View.VISIBLE
        _binding?.imgMsg?.visibility = View.VISIBLE
    }

    fun setupRecyclerView(){
        newsAdapter = NewsListAdapter()
        _binding?.rvNews?.layoutManager = LinearLayoutManager(context)
        _binding?.rvNews?.adapter = newsAdapter
    }
}