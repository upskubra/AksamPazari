package com.kubrayildirim.aksampazari.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kubrayildirim.aksampazari.R
import com.kubrayildirim.aksampazari.databinding.FragmentProfileBinding
import javax.inject.Inject


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null

    @Inject
    lateinit var fAuth: FirebaseAuth
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOutButton.setOnClickListener {
            fAuth = FirebaseAuth.getInstance()
            fAuth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        setBottomNavigation()


    }

    private fun setBottomNavigation(){
        binding.bnbProfile.setOnItemSelectedListener {
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