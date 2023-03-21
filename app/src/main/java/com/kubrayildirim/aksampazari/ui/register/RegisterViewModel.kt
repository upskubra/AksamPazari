package com.kubrayildirim.aksampazari.ui.register

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.kubrayildirim.aksampazari.data.firebase.FirebaseRepository
import com.kubrayildirim.aksampazari.data.model.User
import com.kubrayildirim.aksampazari.util.NetworkControl
import com.kubrayildirim.aksampazari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val networkControl: NetworkControl,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val userLiveData = MutableLiveData<Resource<User>>()
    private val _saveUserLiveData = MutableLiveData<Resource<User>>()
    val saveUserLiveData = _saveUserLiveData
    fun signUpUser(email: String, password: String, fullName: String): LiveData<Resource<User>> {
        when {
            TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(fullName) -> {
                userLiveData.postValue(Resource.error(null, "field must not be empty"))
            }
            password.length < 8 -> {
                userLiveData.postValue(Resource.error(null, "password must not be less than 8"))
            }
            networkControl.isConnected() -> {
                userLiveData.postValue(Resource.loading(null))
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if (it.result?.signInMethods?.size == 0) {
                        repository.signUpUser(email, password, fullName)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firebaseAuth.currentUser?.sendEmailVerification()
                                    userLiveData.postValue(
                                        Resource.success(
                                            User(
                                                email = email,
                                                fullName = fullName
                                            )
                                        )
                                    )
                                } else {
                                    userLiveData.postValue(
                                        Resource.error(
                                            null,
                                            it.exception?.message.toString()
                                        )
                                    )
                                }
                            }
                    } else {
                        userLiveData.postValue(Resource.error(null, "email already exist"))
                    }
                }
            }
            else -> {
                userLiveData.postValue(Resource.error(null, "No internet connection"))
            }
        }
        return userLiveData
    }

    fun saveUser(email: String, name: String) {
        repository.saveUser(email, name).addOnCompleteListener {
            if (it.isSuccessful) {
                _saveUserLiveData.postValue(Resource.success(User(email, name)))
            } else {
                _saveUserLiveData.postValue(Resource.error(null, it.exception?.message.toString()))
            }
        }
    }
}