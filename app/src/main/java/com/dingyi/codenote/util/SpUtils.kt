package com.dingyi.codenote.util

import android.content.Context
import android.content.SharedPreferences

import com.dingyi.codenote.base.BaseActivity

fun getBoolean(context: Context,name :String,value :String) :Boolean{
    return context.getSharedPreferences(name,Context.MODE_PRIVATE).getBoolean(value,false)
}

fun getString(context: Context,name :String,value :String) : String? {
    return context.getSharedPreferences(name,Context.MODE_PRIVATE).getString(value,"")
}


fun getInt(context: Context,name :String,value :String) : Int? {
    return context.getSharedPreferences(name,Context.MODE_PRIVATE).getInt(value,20)
}