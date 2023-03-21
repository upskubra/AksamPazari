package com.kubrayildirim.aksampazari.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.kubrayildirim.aksampazari.R
import com.kubrayildirim.aksampazari.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.Intent
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.kubrayildirim.aksampazari.util.Status


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
 val RC_SIGN_IN = 123
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel:LoginViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.txtRegister.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.loginFragment) {
                NavHostFragment.findNavController(this)
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }

        binding.btnRegister.setOnClickListener {
            val emailText = binding.edtEmail.text.toString()
            val passwordText = binding.edtPassword.text.toString()
            viewModel.signInUser(emailText, passwordText).observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        view.showsnackBar("...")
                    }

                    Status.SUCCESS -> {
                        view.showsnackBar("Login successful")
                        if (findNavController().currentDestination?.id == R.id.loginFragment) {
                            NavHostFragment.findNavController(this)
                                .navigate(
                                    LoginFragmentDirections.actionLoginFragmentToFeedFragment()
                                )
                        }
                    }

                    Status.ERROR -> {
                        view.showsnackBar(it.message!!)
                    }
                }
            }
        }

        binding.googleSignIn.setOnClickListener {
            signIn()
        }
    }

      /*  //forget password
        val dialog = AlertDialog.Builder(requireContext())
        val inflater = (requireActivity()).layoutInflater
        val v = inflater.inflate(R.layout.forgot_password, null)
        dialog.setView(v)
            .setCancelable(false)
        val d = dialog.create()
        val emailEt = v.findViewById<TextInputEditText>(R.id.emailEt)
        val sendBtn = v.findViewById<MaterialButton>(R.id.sendEmailBtn)
        val dismissBtn = v.findViewById<MaterialButton>(R.id.dismissBtn)


        sendBtn.setOnClickListener {
            viewModel.sendResetPassword(emailEt.text.toString()).observeForever {
                if (it.status == Status.SUCCESS){
                    view.showsnackBar("reset email sent")
                }else{
                    view.showsnackBar(it.message.toString())
                }
            }
        }
        dismissBtn.setOnClickListener {
            d.dismiss()
        }


        binding?.txtForgotPassword?.setOnClickListener {
            d.show()
        }
    }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                viewModel.signInWithGoogle(account!!).observe(viewLifecycleOwner) {
                    if (it.status == Status.SUCCESS) {
                        if (findNavController().currentDestination?.id == R.id.loginFragment) {
                            NavHostFragment.findNavController(this).navigate(
                                LoginFragmentDirections.actionLoginFragmentToFeedFragment()
                                // LoginFragmentDirections.actionLoginFragmentToFeedFragment(it?.data?.fullName!!)

                            )
                        }
                    } else if (it.status == Status.ERROR) {
                        requireView().showsnackBar(it.message!!)
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun signIn() {

        val signInIntent: Intent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)

    }
    fun View.showsnackBar(message:String){
        Snackbar.make(this,message, Snackbar.LENGTH_LONG).show()
    }
}