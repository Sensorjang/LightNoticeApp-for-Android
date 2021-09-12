package com.dingyi.codenote.util

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


val FILE_PIC_PATH=Environment.getExternalStorageDirectory().path+"/"+Environment.DIRECTORY_PICTURES+"/MyNotice/"


fun fileIsExist(path: String) :Boolean {
    return File(path).exists()
}

fun getSavePicPath() :String {
    File(FILE_PIC_PATH).mkdirs()
    Log.d("TAG", "getSavePicPath: "+ FILE_PIC_PATH)
    val path= FILE_PIC_PATH+System.currentTimeMillis()+"_save.png"
    File(path).parentFile.mkdirs()

    File(path).createNewFile()
    return path
}

fun getMovePath() :String{
    File(FILE_PIC_PATH).mkdirs()
    Log.d("TAG", "getSavePicPath: "+ FILE_PIC_PATH)
    val path= FILE_PIC_PATH+System.currentTimeMillis()+"_move.png"
    File(path).parentFile.mkdirs()

    File(path).createNewFile()
    return path
}

fun copyFile(path: String,toPath:String) :Boolean {
   runCatching {
       File(toPath).createNewFile()
       FileInputStream(path).use {input->
           FileOutputStream(toPath).use {
               input.copyTo(it)
           }
       }

   }.onFailure {
      return false
   }
    return true
}

fun writeFile(path: String,byteArray: ByteArray) :Boolean{
    runCatching {
        File(path).createNewFile()
        FileOutputStream(path).use {
            it.write(byteArray)
        }
    }.onFailure {
        return false

    }
    return true
}