package com.dingyi.codenote.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.dingyi.codenote.base.BaseActivity
import com.google.android.material.snackbar.Snackbar

fun showSnackbar(view : View, string: String){
    Snackbar.make(view, string, 2500).show()
}

fun recyclerViewForEach(recyclerView: RecyclerView,b:( (View) -> Unit ) ) {
   recyclerView.forEach { b(it) }
}
