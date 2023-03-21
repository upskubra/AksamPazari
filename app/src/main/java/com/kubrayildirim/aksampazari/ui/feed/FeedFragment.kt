package com.kubrayildirim.aksampazari.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kubrayildirim.aksampazari.R
import com.kubrayildirim.aksampazari.adapter.FeedAdapter
import com.kubrayildirim.aksampazari.data.model.Product
import com.kubrayildirim.aksampazari.databinding.FragmentFeedBinding
import com.kubrayildirim.aksampazari.util.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {


    private var _binding: FragmentFeedBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var fAuth: FirebaseAuth
    private val viewModel: FeedViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomNavigation()

        viewModel.fetchProduct().observe(viewLifecycleOwner){
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { productList -> setRecyclerView(productList) }
                }
                Status.ERROR -> {
                    Snackbar.make(view, it.message.toString(), Snackbar.LENGTH_LONG).show()
                }
                Status.LOADING -> {
                    Snackbar.make(view, "Loading", Snackbar.LENGTH_LONG).show()
                }
            }
        }


    }

    private fun setRecyclerView(list : List<Product>){
        binding.rvFeed.adapter = FeedAdapter(list)
    }

    fun setBottomNavigation(){
        binding.bnbFeed.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.feedFragment -> {
                    if (findNavController().currentDestination?.id == R.id.feedFragment) {
                        findNavController().navigate(R.id.action_feedFragment_self)
                        return@setOnItemSelectedListener true
                    } else {
                        findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.profileFragment -> {
                    if (findNavController().currentDestination?.id == R.id.profileFragment) {
                        findNavController().navigate(R.id.action_profileFragment_self)
                        return@setOnItemSelectedListener true
                    } else {
                        findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
                        return@setOnItemSelectedListener true
                    }
                }
                else -> false
            }
        }
    }
}
