package com.dingyi.codenote.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;

import androidx.appcompat.widget.PopupMenu;


import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dingyi.codenote.R;
import com.dingyi.codenote.adapter.MainAdapter;
import com.dingyi.codenote.base.BaseActivity;
import com.dingyi.codenote.bean.Notice;
import com.dingyi.codenote.database.NoticeHelper;
import com.dingyi.codenote.util.DisplayUtils;

import com.dingyi.codenote.util.TextUtilsKt;

import java.util.List;
import java.util.Objects;

/**
 * @author dingyi
 * @time 2020-7-13
 */

public class MainActivity extends BaseActivity {

    private static final String TAG ="MainActivity" ;
    private View more_view;

    private RecyclerView recyclerView;

    private NoticeHelper db;

    private long backTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		Toolbar toolbar=findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		findViews();
		listInit();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.main_more :
                 showMorePop();
                 break;
            case R.id.main_add :
                startActivityForResult(new Intent(this,EditActivity.class),100);
                 break;
            case R.id.main_search :
                showSearchBar();
                 break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showSearchBar(){
        startSupportActionMode(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                EditText ed=new EditText(MainActivity.this);

                ed.setTextColor(0xffffffff);

                ed.setHint("输入待搜索的便签");

                ed.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));

                mode.setCustomView(ed);

                ed.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        search(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
               refresh();
            }
        });
    }

    private void showMorePop(){
        PopupMenu pop=new PopupMenu(MainActivity.this,more_view);

        pop.getMenu().add("设置").setOnMenuItemClickListener( s-> {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        });

        pop.show();

    }

    private void listInit()  {

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(new MainAdapter(this));


        ((MainAdapter) Objects.requireNonNull(recyclerView.getAdapter())).setOnItemClick(v -> {
            Intent intent=new Intent(this,EditActivity.class);


            TextView id=v.findViewById(R.id.main_item_id);

            intent.putExtra("id",id.getText().toString());

            startActivityForResult(intent,100);



        });


        ((MainAdapter) Objects.requireNonNull(recyclerView.getAdapter())).setOnDeleteCallBack(this::refresh);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int px= DisplayUtils.dp2px(MainActivity.this,10);
                outRect.set(px,px,px,px);
            }
        });//设置marign为10dp

       refresh();



    }

    private void refresh(){
        db=new NoticeHelper(this);//获取数据库帮助对象



        ((MainAdapter) Objects.requireNonNull(recyclerView.getAdapter())).clear();

        List<Notice> noticeList=db.queryAll();//查询到所有对象
        db.close();//关闭数据库连接

        for(Notice notice:noticeList){
            Log.i( "List: ",notice.getId()+"---------"+notice.getContent()+" "+notice.getTime());
            ((MainAdapter) recyclerView.getAdapter()).add(notice);
        }

    }

    private void findViews(){//find id 集合

        more_view=findViewById(R.id.main_more_click);
        recyclerView=findViewById(R.id.main_body_list);
    }


    private void search(String str){
        db=new NoticeHelper(this);//获取数据库帮助对象

        ((MainAdapter) Objects.requireNonNull(recyclerView.getAdapter())).clear();

        List<Notice> noticeList=db.queryByContent(str);
        db.close();//关闭数据库连接

        for(Notice notice:noticeList){
            ((MainAdapter) recyclerView.getAdapter()).add(notice);
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       super.onKeyDown(keyCode, event);
       if (keyCode==KeyEvent.KEYCODE_BACK && System.currentTimeMillis()-backTime<2.5*1000){
           this.finishAndRemoveTask();
           return true;
       } else if (keyCode==KeyEvent.KEYCODE_BACK) {
           backTime=System.currentTimeMillis();
           TextUtilsKt.showSnackbar(recyclerView,"再按一次退出");
           return false;
       }

       return false;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==100){

           refresh();
        }
    }



}
