package com.kubrayildirim.aksampazari.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kubrayildirim.aksampazari.data.firebase.FirebaseRepository
import com.kubrayildirim.aksampazari.util.NetworkControl
import com.kubrayildirim.aksampazari.data.model.Product
import com.kubrayildirim.aksampazari.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val networkControl: NetworkControl,
) : ViewModel() {
    private val productData = MutableLiveData<Resource<List<Product>>>()

    fun fetchProduct() : LiveData<Resource<List<Product>>> {
        if (networkControl.isConnected()) {
            productData.postValue(Resource.loading(null))
            repository.fetchProduct().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val product = task.result?.documents?.map {
                        Product(
                            it.data?.getValue("first_price").toString(),
                            it.data?.getValue("last_price").toString(),
                            it.data?.getValue("name").toString(),
                            it.data?.getValue("restaurant_name").toString(),
                            it.data?.getValue("photo_url").toString(),
                        )
                    }
                    productData.postValue(Resource.success(product!!))
                } else {
                    productData.postValue(Resource.error(null, task.exception?.message.toString()))
                }
            }
        } else {
            productData.postValue(Resource.error(null, "No internet connection"))
        }
        return productData
    }

}