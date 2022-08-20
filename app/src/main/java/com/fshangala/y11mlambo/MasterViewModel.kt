package com.fshangala.y11mlambo

import androidx.lifecycle.MutableLiveData

class MasterViewModel:Y11ViewModel() {
    var browserLoading = MutableLiveData<Boolean>(false)
}