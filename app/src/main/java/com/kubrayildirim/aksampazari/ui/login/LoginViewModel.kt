package com.kubrayildirim.aksampazari.ui.login

import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.kubrayildirim.aksampazari.data.firebase.FirebaseRepository
import com.kubrayildirim.aksampazari.util.NetworkControl
import com.kubrayildirim.aksampazari.data.model.User
import com.kubrayildirim.aksampazari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val networkControl: NetworkControl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val userLiveData = MutableLiveData<Resource<User>>()
    private val gMailUserLiveData = MutableLiveData<Resource<User>>()
    private val sendResetPasswordLiveData = MutableLiveData<Resource<User>>()

    fun signInUser(email: String, password: String): LiveData<Resource<User>> {
        when {
            TextUtils.isEmpty(email) && TextUtils.isEmpty(password) -> {
                userLiveData.postValue(Resource.error(null, "Enter email and password"))
            }
            networkControl.isConnected() -> {
                userLiveData.postValue(Resource.loading(null))
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    //check if email exists
                    if (it.result?.signInMethods?.size == 0) {
                        userLiveData.postValue(Resource.error(null, "Email does not exist"))
                    } else {
                        repository.signInUser(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseAuth.currentUser?.isEmailVerified?.let { verified ->
                                    if (verified) {
                                        repository.fetchUser().addOnCompleteListener { userTask ->
                                            if (userTask.isSuccessful) {
                                                userTask.result?.documents?.forEach {
                                                    if (it.data!!["email"] == email) {
                                                        val name = it.data?.getValue("fullName")
                                                        userLiveData.postValue(
                                                            Resource.success(
                                                                User(
                                                                    firebaseAuth.currentUser?.email!!,
                                                                    name?.toString()!!
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            } else {
                                                userLiveData.postValue(
                                                    Resource.error(
                                                        null, userTask.exception?.message.toString()
                                                    )
                                                )
                                            }
                                        }
                                    } else {
                                        userLiveData.postValue(
                                            Resource.error(
                                                null, "Email is not verified, check your email"
                                            )
                                        )
                                    }
                                }
                            } else {
                                userLiveData.postValue(
                                    Resource.error(
                                        null, task.exception?.message.toString()
                                    )
                                )
                                Toast.makeText(
                                    null, task.exception?.message.toString(), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            else -> {
                userLiveData.postValue(Resource.error(null, "No internet connection"))
            }
        }
        return userLiveData
    }

    fun signInWithGoogle(acct: GoogleSignInAccount): LiveData<Resource<User>> {
        repository.signInWithGoogle(acct).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                gMailUserLiveData.postValue(
                    Resource.success(
                        User(
                            firebaseAuth.currentUser?.email!!,
                            firebaseAuth.currentUser?.displayName!!
                        )
                    )
                )
            } else {
                gMailUserLiveData.postValue(Resource.error(null, "couldn't sign in user"))
            }

        }
        return gMailUserLiveData
    }

    fun sendResetPassword(email: String): LiveData<Resource<User>> {

        when {
            TextUtils.isEmpty(email) -> {
                sendResetPasswordLiveData.postValue(Resource.error(null, "Enter registered email"))
            }
            networkControl.isConnected() -> {
                repository.sendForgotPassword(email).addOnCompleteListener { task ->
                    sendResetPasswordLiveData.postValue(Resource.loading(null))
                    if (task.isSuccessful) {
                        sendResetPasswordLiveData.postValue(Resource.success(User()))
                    } else {
                        sendResetPasswordLiveData.postValue(
                            Resource.error(
                                null, task.exception?.message.toString()
                            )
                        )
                    }
                }
            }
            else -> {
                sendResetPasswordLiveData.postValue(Resource.error(null, "No internet connection"))
            }
        }
        return sendResetPasswordLiveData
    }
}