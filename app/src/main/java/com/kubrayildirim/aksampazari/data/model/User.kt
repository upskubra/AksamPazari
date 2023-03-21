package com.kubrayildirim.aksampazari.data.model

data class User(val email: String = "", val fullName: String = "", val type: TYPE = TYPE.CUSTOMER) {

    enum class TYPE {
        CUSTOMER,
        RESTAURANT
    }
}
