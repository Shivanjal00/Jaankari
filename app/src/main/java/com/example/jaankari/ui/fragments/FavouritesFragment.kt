package com.example.jaankari.ui.fragments

import android.content.ClipData.Item
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jaankari.adapters.NewsAdapter
import com.example.jaankari.ui.NewsActivity
import com.example.jaankari.ui.NewsViewModel
import com.example.jaankari.R
import com.example.jaankari.databinding.FragmentFavouritesBinding
import com.example.jaankari.databinding.FragmentHeadlinesBinding
import com.google.android.material.snackbar.Snackbar

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentFavouritesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouritesBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setupFavouritesRecycle()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_favouritesFragment_to_articleFragment, bundle)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"Removed from Favourites",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.addToFavorites(article)
                    }

                    show()

                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }


        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })

    }

    private fun setupFavouritesRecycle(){
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)}
    }

}