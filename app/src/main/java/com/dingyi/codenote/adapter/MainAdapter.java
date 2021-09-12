package com.dingyi.codenote.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.view.ActionMode;

import androidx.recyclerview.widget.RecyclerView;

import com.dingyi.codenote.R;
import com.dingyi.codenote.base.BaseActivity;
import com.dingyi.codenote.bean.Notice;
import com.dingyi.codenote.database.NoticeHelper;
import com.dingyi.codenote.util.TextUtilsKt;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.jvm.functions.Function1;


/**
 * @author dingyi
 * @time 2020-7-13
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHoler> {


    private static final String TAG =MainAdapter.class.getSimpleName() ;
    private BaseActivity activity;
    private HashMap<Integer, List<String>> list= new HashMap<>();

    private RecyclerView recyclerView;

    private boolean multiChoice=false;

    private List<Integer> multiChoiceList=new ArrayList<>();

    private OnItemClick onItemClick;

    private ActionMode actionMode;


    private OnDeleteCallBack onDeleteCallBack;

    public MainAdapter(BaseActivity activity){
        this.activity=activity;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHoler(LayoutInflater.from(activity).inflate(R.layout.activity_main_body_item, null));
    }

    /**
     处理退格方法
     @param e 新的notice对象
     **/
    public void add(Notice e){
        List<String> data=new ArrayList<>();
        data.add(e.getSimpleContent());
        data.add(e.getTime());
        data.add(e.getId()+"");
        list.put(list.size(), data);
        notifyItemChanged(list.size());
    }


    public OnDeleteCallBack getOnDeleteCallBack() {
        return onDeleteCallBack;
    }

    public void setOnDeleteCallBack(OnDeleteCallBack onDeleteCallBack) {
        this.onDeleteCallBack = onDeleteCallBack;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView=recyclerView;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {

        MaterialCardView view= (MaterialCardView) holder.view;

        view.setOnClickListener( s -> {

           if (multiChoice) {
             TextView i=view.findViewById(R.id.main_item_id);
             Integer id=Integer.parseInt(i.getText().toString());

             if (multiChoiceList.contains(id)) {
                 view.setChecked(false);

                 multiChoiceList.remove(id);
             }else {
                 multiChoiceList.add(id);
                 view.setChecked(true);

             }

           } else {
               onItemClick.onItemClik(s);
           }

        });

       view.setOnLongClickListener( s -> {
           if (!multiChoice){
               multiChoice = true;
               TextView i = view.findViewById(R.id.main_item_id);
               Integer id = Integer.parseInt(i.getText().toString());
               multiChoiceList.add(id);
               view.setChecked(true);
               showActionMode();
           } else {
               clearMultiChoice();
               actionMode.finish();
           }
           return true;
       });

       TextView content=view.findViewById(R.id.main_item_content);
       TextView date=view.findViewById(R.id.main_item_date);
       content.setText(Objects.requireNonNull(list.get(position)).get(0));
        ((TextView) view.findViewById(R.id.main_item_id)).setText(list.get(position).get(2));
       date.setText(Objects.requireNonNull(list.get(position)).get(1));
    }


    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();

    }

    public void clearMultiChoice(){


        multiChoiceList.clear();

        TextUtilsKt.recyclerViewForEach(recyclerView, a -> {
            ((MaterialCardView) a).setChecked(false);
            return null;
        });
       
    }

    public void selectAll(){
        TextUtilsKt.recyclerViewForEach(recyclerView, a -> {
            MaterialCardView view= ((MaterialCardView) a);
            TextView i=view.findViewById(R.id.main_item_id);
            Integer id=Integer.parseInt(i.getText().toString());

            multiChoiceList.add(id);
            view.setChecked(true);

            return null;
        });
    }

    private void delete() {
        NoticeHelper db=new NoticeHelper(activity);
        for (int it:multiChoiceList){
          db.remove(it+"");
        }

        clearMultiChoice();

        onDeleteCallBack.onDelete();
    }

    private void showActionMode(){
        activity.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode=mode;
                activity.getMenuInflater().inflate(R.menu.mian_select,menu);
                mode.setTitle("删除");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.main_select :
                        selectAll();
                        break;
                    case R.id.main_delete :
                        delete();
                        mode.finish();
                        break;
                }
                return true;
            }



            @Override
            public void onDestroyActionMode(ActionMode mode) {
               multiChoice=false;

               clearMultiChoice();
            }
        });
    }


    public interface OnItemClick {
        void onItemClik(View v);
    }


    public static class MyViewHoler extends RecyclerView.ViewHolder {

        public View view;

        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            view=itemView;

        }
    }

    public  interface  OnDeleteCallBack {
        void onDelete();
    }
}
