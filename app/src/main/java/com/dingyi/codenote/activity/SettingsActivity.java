package com.dingyi.codenote.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dingyi.codenote.R;
import com.dingyi.codenote.base.BaseActivity;
import com.dingyi.codenote.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("设置");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager=getSupportFragmentManager();

        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.setttings,new SettingsFragment());

        fragmentTransaction.commit();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finishAndRemoveTask();
        return super.onOptionsItemSelected(item);
    }
}