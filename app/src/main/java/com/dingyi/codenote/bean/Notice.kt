package com.dingyi.codenote.bean

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Notice(var id: Int, var content: String, var time: String) {


    override fun toString(): String {
        return "Notice{" +
                "content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", id=" + id +
                '}'
    }

    fun getSimpleContent() : String? {
        val type=object : TypeToken<List<EditBean>>(){}.type
        val list= Gson().fromJson<List<EditBean>>(content,type)
        val str=StringBuilder()
        return list.let {
            it.forEach {
                if (it.isImage) {
                    str.append("[图片]")
                }else {
                  str.append(it.text)
                }

            }
          return str.toString()
        }
        return null
    }

}