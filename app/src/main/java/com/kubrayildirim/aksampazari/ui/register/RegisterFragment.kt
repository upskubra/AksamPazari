package com.kubrayildirim.aksampazari.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kubrayildirim.aksampazari.R
import com.kubrayildirim.aksampazari.databinding.FragmentRegisterBinding
import com.kubrayildirim.aksampazari.ui.login.LoginViewModel
import com.kubrayildirim.aksampazari.util.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    val RC_SIGN_IN = 123

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()
    private val lViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener {
            val emailText = binding.edtEmail.text?.toString()
            val passwordText = binding.edtPassword?.text.toString()
            val fullNameText = binding?.edtName?.text?.toString()
            viewModel.signUpUser(emailText.toString(), passwordText, fullNameText.toString())
                .observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            viewModel.saveUser(
                                it.data?.email.toString(),
                                it.data?.fullName.toString()
                            )
                            view.showsnackBar("User account registered")
                            if (findNavController().currentDestination?.id == R.id.registerFragment) {
                                NavHostFragment.findNavController(this).navigate(
                                    RegisterFragmentDirections.actionRegisterFragmentToFeedFragment()
                                )
                            }
                        }
                        Status.ERROR -> {
                            view.showsnackBar(it.message!!)
                        }
                        Status.LOADING -> {
                            view.showsnackBar("...")
                        }
                    }
                }
        }

        binding?.googleSignIn?.setOnClickListener {
            signIn()
        }

        try {
            viewModel.saveUserLiveData.observe(viewLifecycleOwner) {
                if (it.status == Status.SUCCESS) {
                    requireView().showsnackBar("success!")
                    if (findNavController().currentDestination?.id == R.id.registerFragment) {
                        NavHostFragment.findNavController(this).navigate(
                            RegisterFragmentDirections.actionRegisterFragmentToFeedFragment()
                        )
                        //Timber.d("display ${fAuth.currentUser?.displayName} ")
                    }
                } else if (it.status == Status.ERROR) {
                    requireView().showsnackBar(it.message!!)
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {


            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val account = task.getResult(ApiException::class.java)


                lViewModel.signInWithGoogle(account!!).observe(viewLifecycleOwner) {
                    if (it.status == Status.SUCCESS) {

                        viewModel.saveUser(
                            auth.currentUser?.email!!,
                            auth.currentUser?.displayName!!
                        )
                    } else if (it.status == Status.ERROR) {
                        requireView().showsnackBar(it.message!!)
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun View.showsnackBar(message: String) {
        Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
    }
}

