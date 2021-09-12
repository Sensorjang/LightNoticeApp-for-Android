package com.dingyi.codenote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dingyi.codenote.bean.Notice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingyi
 * @time 2020-7-13
 */


public class NoticeHelper {
   private  DBHelper mdb;

   private SQLiteDatabase db;

    /**
     *
     * @param context 活动的context
     */
   public NoticeHelper(Context context){
       mdb=new DBHelper(context.getApplicationContext());//调用整个程序的context
       db=mdb.getWritableDatabase();//拿到读写db
   }


    /**
     *  查询所有笔记
     * @return 所有的notice对象
     */
   public List<Notice> queryAll() {

       Cursor result=db.rawQuery("select * from notice order by id desc",null);
       result.moveToFirst();
       List <Notice> rl=new ArrayList<>();

       boolean ok=true;

       if (result.getCount()==0){
           result.close();
           return  rl;
       }

       while (ok){
           rl.add(new Notice(result.getInt(0),result.getString(1),result.getString(2)));
           ok=result.moveToNext();
       }



       result.close();

       return  rl;

   }

   //关闭数据库连接
   public void close(){
       db.close();
       mdb.close();
   }

    /**
     * 查询全部笔记条数
     * @return 数据总条数
     */
   public Long getCount(){
       Cursor a=db.rawQuery("select count(*) from notice",null);
       a.moveToFirst();

       Long b=a.getLong(0);
       a.close();
       return b;
   }

    /**
     * 更新一条笔记
     * @param notice 笔记对象
     * @return 是否更新成功
     */
   public boolean update(Notice notice){

       try {
           db.execSQL("update notice set content=?,time=? where id=?",new String[]{notice.getContent(),notice.getTime(),notice.getId()+""});
       }catch (Exception e){
           Log.e("error by sql",e.getMessage());
           return  false;
       }
       return true;


   }

    /**
     *  通过id获取数据
     * @param id 笔记的id
     * @return 返回笔记对象
     */

   public Notice getNoticeById(String id){
       Cursor a=db.rawQuery("select * from notice where id="+id,null);
       a.moveToFirst();
       if (a.getCount()==0) {
           a.close();
           return null;
       }
       Notice noctie=new Notice(a.getInt(0),a.getString(1),a.getString(2));
       a.close();
       return noctie;
   }

    /**
     *  删除一条数据
     * @param id 笔记的id
     * @return 返回笔记对象
     */

   public boolean remove(String id){
       try {
           db.execSQL("delete from notice where id=" + id);
       }catch (Exception e){
           Log.e("error by sql",e.getMessage());
           return  false;
       }
      return true;
   }



    /**
     *  搜索笔记
     * @param s 搜索的笔记内容
     * @return 笔记列表list
     */
   public List<Notice>  queryByContent(String s){
       Cursor result=db.rawQuery("select * from notice where content like '%"+s+"%'"+" order by id desc",null);
       result.moveToFirst();
       List <Notice> rl=new ArrayList<>();

       boolean ok=true;
       if (result.getCount()==0){
           result.close();
           return  rl;
       }
       while (ok){
           rl.add(new Notice(result.getInt(0),result.getString(1),result.getString(2)));
           ok=result.moveToNext();
       }

       return  rl;
   }





    /**
     *  添加一条笔记
     * @param notice 笔记对象
     * @return 是否添加成功
     */
   public boolean add(Notice notice){

       try {

           db.execSQL("insert into notice (id,content,time) values (null,?,?)", new String[]{ notice.getContent(), notice.getTime()});

       }catch (Exception e){
           Log.e("error by sql",e.getMessage());
           return  false;
       }

       return true;

   }

}
