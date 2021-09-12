package com.dingyi.codenote.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.dingyi.codenote.R;
import com.dingyi.codenote.base.BaseActivity;

import com.dingyi.codenote.bean.Notice;
import com.dingyi.codenote.database.NoticeHelper;
import com.dingyi.codenote.util.FileUtilKt;
import com.dingyi.codenote.util.SpUtilsKt;
import com.dingyi.codenote.util.TextUtilsKt;
import com.dingyi.codenote.view.MyRichTextEditor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.dingyi.codenote.util.FileUtilKt.getMovePath;

/**
 * @author dingyi
 * @time 2020-7-13
 */


public class EditActivity extends BaseActivity {

    private View more_view;

    private TextView date;

    private MyRichTextEditor editor;

    private long backTime;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("编辑便签");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        id=this.getIntent().getStringExtra("id");



        if (id==null){
            setTitle("新建便签");
            date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            id = "-1";//目的是为了让saveData()方法中的getNoticeById(id)无返回结果，从而触发“新建”动作

        } else {
            setTitle("编辑便签");
            NoticeHelper db=new NoticeHelper(this);
            Notice notice=db.getNoticeById(id);
            date.setText(notice.getTime());
            editor.setText(notice.getContent());
        }



    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                saveData();
                String msg =null;
                if(editor.getText().contains("[{\"isImage\":false,\"path\":\"\",\"text\":\"\"}]") && getTitle().equals("新建便签")) msg = "空内容便签不保存退出";
                else msg = "已为您保存便签";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
                break;
            case R.id.main_more :
                showMorePop();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showMorePop(){

        PopupMenu pop=new PopupMenu(this,more_view);

        MenuItem save = pop.getMenu().add("保存");

        save.setOnMenuItemClickListener( s-> {
            saveData();
            String msg =null;
            if(editor.getText().contains("[{\"isImage\":false,\"path\":\"\",\"text\":\"\"}]") && getTitle().equals("新建便签")) msg = "空内容便签不保存退出";
            else msg = "已为您保存便签";
            TextUtilsKt.showSnackbar(editor,msg);
            return true;
        });

        MenuItem add = pop.getMenu().add("添加");

        add.setOnMenuItemClickListener( s -> showAddPop());

        MenuItem share = pop.getMenu().add("分享");

        MenuItem image = pop.getMenu().add("长截图");

        share.setOnMenuItemClickListener( s -> {
            String path= FileUtilKt.getSavePicPath();
            editor.saveToBitmap(path);

            Intent intent=new Intent(Intent.ACTION_SEND);

            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.N) {
              intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider",new File(path)));
              intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
            }

            intent.setType("image/png");

            intent.putExtra(Intent.EXTRA_SUBJECT,"分享文件");


            startActivity(Intent.createChooser(intent,"分享到..."));


            return true;
        });

        image.setOnMenuItemClickListener( s -> {
            String path= FileUtilKt.getSavePicPath();
            editor.saveToBitmap(path);
            TextUtilsKt.showSnackbar(editor,"已保存到: "+path);
            return true;
        });

        pop.show();

    }

    private boolean showAddPop() {
        PopupMenu pop=new PopupMenu(this,more_view);

        MenuItem add=pop.getMenu().add("图片");

        add.setOnMenuItemClickListener( v -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ,2000);
            return true;
        });


        pop.show();
        return true;
    }


    private void findViews() {
        more_view=findViewById(R.id.main_more_click);
        editor=findViewById(R.id.editor);
        date=findViewById(R.id.edit_time);
    }

    private void saveData(){
        if(editor.getText().contains("[{\"isImage\":false,\"path\":\"\",\"text\":\"\"}]") && getTitle().equals("新建便签")) return;//新建便签界面没有任何内容则不保存
//        Log.i( "aaaaaaaaaaaaaaaaaa","--"+editor.getText()+"--");
        NoticeHelper db=new NoticeHelper(this);
        Notice tmp=db.getNoticeById(id);
        if (tmp==null){
            tmp=new Notice(Integer.parseInt(id), Objects.requireNonNull(editor.getText()),new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            db.add(tmp);
        } else {
            tmp.setContent(Objects.requireNonNull(editor.getText()));
            tmp.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            db.update(tmp);
        }

        Log.d("savedata", "saveData: "+tmp.toString());

        db.close();

        this.setResult(100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2000 && data!=null){
           Cursor query= getContentResolver().query(data.getData(),new String[]{ MediaStore.Images.Media.DATA},null,null,null);
           query.moveToFirst();
           String path=query.getString(0);
           if (SpUtilsKt.getBoolean(this, "settings", "imgset")){
               String v= getMovePath();
               FileUtilKt.copyFile(path,v);
               path=v;
           }

           editor.addImage(path);
           query.close();

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if (keyCode==KeyEvent.KEYCODE_BACK && System.currentTimeMillis()-backTime<2.5*1000){
            saveData();
            String msg = null;
            if(editor.getText().contains("[{\"isImage\":false,\"path\":\"\",\"text\":\"\"}]") && getTitle().equals("新建便签")) msg = "空内容便签不保存退出";
            else msg = "已为您保存便签";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            this.finishAndRemoveTask();
            return true;
        } else if (keyCode==KeyEvent.KEYCODE_BACK) {
            saveData();
            backTime=System.currentTimeMillis();
            TextUtilsKt.showSnackbar(editor,"再按一次退出");
            return false;
        }

        return false;
    }

}
