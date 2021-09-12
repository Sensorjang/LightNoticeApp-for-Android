package com.dingyi.codenote.view


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent


import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import com.bumptech.glide.Glide
import com.dingyi.codenote.R
import com.dingyi.codenote.base.BaseActivity
import com.dingyi.codenote.bean.EditBean
import com.dingyi.codenote.util.DisplayUtils
import com.dingyi.codenote.util.getInt
import com.dingyi.codenote.util.getString

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream


class  MyRichTextEditor(context: Context, attributes: AttributeSet) : ScrollView(context, attributes) {

    private var rootLayout :LinearLayout = LinearLayout(context)

    private lateinit var nowEditText :EditText;


    private var imgList= mutableMapOf<Int,String>()

    var imageLoaderLister: OnImageLoaderLister

    var imageClickLister: OnImageClickLister?=null

    var editTextBackground : Drawable? = null

    var text : String?=""

    get() {

       val list=ArrayList<EditBean>()
        rootLayout.forEach {
            if (it is ImageView) {
                list.add(EditBean(imgList[it.id].toString(),"",true))
            } else if (it is TextView) {
                list.add(EditBean("",it.text.toString(),false))
            }

        }
       return Gson().toJson(list)
    }
    set(value) {

        var type=object : TypeToken<List<EditBean>>(){}.type
        var list= Gson().fromJson<List<EditBean>>(value,type)
        list.let {
            if (it.isNotEmpty())  rootLayout.removeAllViews()
        }

        list.forEach {
            if (it.isImage) {
                addImage(it.path)
            }else {
                var edit=getAddEditText()
                edit.setText(it.text)
                rootLayout.addView(edit)
                edit.requestFocus();
            }

        }

        rootLayout.getChildAt(0).requestFocus();

        field=value
    }

    init {
        rootLayout.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
        rootLayout.orientation=LinearLayout.VERTICAL
        rootLayout.gravity= Gravity.LEFT or Gravity.CENTER
        addView(rootLayout)

        rootLayout.addView(getAddEditText())//首先添加一个editText
        imageLoaderLister=getDefaultImageLoad()//然后先使用默认的图片加载器
        initAttr(attributes)//加载自定义属性
    }

    companion object {
        const val TAG ="MyRichTextEditor"
    }

    private fun initAttr(attributes: AttributeSet){
        val styles=context.obtainStyledAttributes(attributes,R.styleable.MyRichTextEditor)

        editTextBackground= styles.getDrawable(R.styleable.MyRichTextEditor_editText_Background)
        styles.recycle()
    }


    //获取需要添加的EditText
    private fun getAddEditText() : EditText {
        val tmp=EditText(context)
        tmp.layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        tmp.onFocusChangeListener =OnFocusChangeListener{ v, hasFocus ->
            if (hasFocus && v is EditText){
                nowEditText=v
            }
        }

        tmp.background=editTextBackground



        context.let {
            getInt(it,"settings","editor_size")?.let {
                Log.d(TAG, "getAddEditText: "+it)
                tmp.textSize=it.toFloat();
            }
        }

        tmp.setOnKeyListener {
           v, keyCode, _ ->
           val  edit=v as? EditText?
               if (keyCode==KeyEvent.KEYCODE_DEL && edit?.selectionEnd==0 && rootLayout.indexOfChild(edit)>0){
                   backCursour(edit)
               }

           false
        }

        return tmp
    }

    private fun getAddImageView() :ImageView{

        val img=ImageView(context)
        img.setOnClickListener { v -> imageClickLister?.onImageClick((v as? ImageView)!!)  }
        return img
    }

    private fun insertImage(imageView: ImageView) {

        val indexa=nowEditText.text.toString().substring(nowEditText.selectionEnd,nowEditText.text.length)
        val indexb=nowEditText.text.toString().substring(0,nowEditText.selectionEnd)

        if (indexb.isEmpty() && rootLayout.indexOfChild(nowEditText)!=0) { //光标在顶部

            rootLayout.addView(imageView, rootLayout.indexOfChild(nowEditText) )//在当前EditText上面添加图片

        } else { //光标不在顶部
            nowEditText.setText(indexb)

            rootLayout.addView(imageView, rootLayout.indexOfChild(nowEditText) + 1)


            val addEditText = getAddEditText()

            addEditText.setText(indexa)

            rootLayout.addView(addEditText, rootLayout.indexOfChild(nowEditText) + 2)


            addEditText.requestFocus()

            addEditText.setSelection(0)
        }

    }

    fun addImage(path:String){

        val image=getAddImageView()


        val params=LinearLayout.LayoutParams(-1,-2)



        params.setMargins(0,DisplayUtils.dp2px(context,6f),0,DisplayUtils.dp2px(context,6f))

        image.layoutParams= params


        image.adjustViewBounds=true

        image.scaleType=ImageView.ScaleType.FIT_XY

        imgList[image.id]=path


        imageLoaderLister.onImageLoad(image, path)

        insertImage(image)

        Log.d(TAG, "addImage: "+text)

    }


    fun saveToBitmap(path: String) :Boolean{

        nowEditText.isCursorVisible=false

        kotlin.runCatching {
            isDrawingCacheEnabled=true

            var paint= Paint()

            paint.setColor(Color.WHITE)

            var bitmap=Bitmap.createBitmap(drawingCache.width,drawingCache.height,Bitmap.Config.ARGB_8888)

            var canvas=Canvas(bitmap)


            canvas.drawRect(0f,0f,drawingCache.width.toFloat(),drawingCache.height.toFloat(),paint)

            canvas.drawBitmap(drawingCache,0f,0f,paint)

            File(path).createNewFile()
            FileOutputStream(path).use {
                bitmap.compress(Bitmap.CompressFormat.PNG,100,it)
            }

            isDrawingCacheEnabled=false
            bitmap.recycle()
        }.onFailure {
            Log.e(TAG, "saveToBitmap: "+it.message)
            return false
        }.onSuccess {
            return true
        }

        nowEditText.isCursorVisible=true

        return false

    }

    /**
    处理退格方法

    @param v 当前操作的EditText
    **/
    private fun backCursour (v:EditText){

        val lastview=rootLayout.getChildAt(rootLayout.indexOfChild(v)-1)



        if (lastview is ImageView) {
            imgList.remove(lastview.id)
            rootLayout.removeView(lastview)
            return
        } else if(lastview is EditText){
            val index=lastview.selectionEnd

            lastview.append(v.text)
            rootLayout.removeView(v)
            lastview.setSelection(index)
            return
        }

    }

    /*
     图片点击接口
    */
    interface OnImageClickLister {
       fun onImageClick(imageView: ImageView)
    }

    /*
     图片加载方式接口，默认使用glide实现
    */

    interface OnImageLoaderLister {
        fun onImageLoad(imageView: ImageView, path:String)
    }

    /*
    默认的图片加载方式实现接口
    */

    private fun getDefaultImageLoad() :OnImageLoaderLister {
      return object:OnImageLoaderLister {
          override fun onImageLoad(imageView: ImageView, path: String) {
             Glide.with(context).load(path).placeholder(R.drawable.ic_launcher_background).into(imageView)
          }

      }
    }




}



