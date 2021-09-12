package com.dingyi.codenote;
import android.app.Application;

import com.dingyi.codenote.database.DBHelper;
import com.dingyi.codenote.database.NoticeHelper;
import com.dingyi.codenote.util.CrashHandler;

public class MyApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();

        new NoticeHelper(this).close();//初始化db
        CrashHandler.getInstance().init(this.getApplicationContext());
        
    }
    
    
}
