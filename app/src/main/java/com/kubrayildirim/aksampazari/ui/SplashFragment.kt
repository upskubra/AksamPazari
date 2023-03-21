package com.kubrayildirim.aksampazari.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kubrayildirim.aksampazari.R
import com.kubrayildirim.aksampazari.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private val fragmentScope = CoroutineScope(Dispatchers.Main)

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var fAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root

        fragmentScope.launch {
            delay(100)
            if (fAuth.currentUser != null) {
                // pass the other nav graph to the splash fragment
                findNavController().navigate(R.id.action_splashFragment_to_feedFragment)
            } else findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        fragmentScope.cancel()
        super.onPause()
    }
}