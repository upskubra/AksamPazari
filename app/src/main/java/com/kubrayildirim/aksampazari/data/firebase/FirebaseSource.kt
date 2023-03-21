package com.kubrayildirim.aksampazari.data.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kubrayildirim.aksampazari.data.model.User
import javax.inject.Inject

class FirebaseSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun signUpUser(email:String,password:String,fullName:String) = firebaseAuth.createUserWithEmailAndPassword(email,password)


    fun signInUser(email: String,password: String) = firebaseAuth.signInWithEmailAndPassword(email,password)

    fun signInWithGoogle(acct: GoogleSignInAccount) = firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken,null))

    fun saveUser(email: String,name:String)=firestore.collection("users").document(email).set(User(email = email,fullName = name))

    fun fetchUser()=firestore.collection("users").get()

    fun fetchProduct()=firestore.collection("product").get()

    fun sendForgotPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)

}