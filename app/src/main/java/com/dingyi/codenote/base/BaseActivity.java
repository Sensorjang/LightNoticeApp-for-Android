package com.dingyi.codenote.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;



/**
 * @author dingyi
 * @time 2020-7-13
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestPermissions();
        //TODO 适配多主题
    }
    
    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("申请权限")
                    .setMessage("程序正在申请一些必要的读写权限，请同意")
                    .setNegativeButton("申请", (dialog, which) -> {

                            this.requestPermissions(new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                            },400);
                    })
                    .show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {

            requestPermissions();

        }

    }


    public int getThemeColorPrimary(){
        TypedArray typedArray=getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorPrimary
        });
        int r=typedArray.getColor(0,0xff2196f3);
        typedArray.recycle();
        return  r;
    }
}
