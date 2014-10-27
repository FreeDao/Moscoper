package com.skycober.mineral;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import cn.jpush.android.api.JPushInterface;

import com.skycober.mineral.util.SettingUtil;

/**
 * For developer startup JPush SDK
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication myapp;
    private List<Activity> activityList;
    public MyApplication(){
    	activityList = new ArrayList<Activity>();
    }

    @Override
    public void onCreate() {    	     
         super.onCreate();
         JPushInterface.setDebugMode(true); 	
         JPushInterface.init(this);   
       if (SettingUtil.getInstance(getApplicationContext()).getValue(SettingUtil.KEY_ACCEPT_PUSH_NOTIFICATION, true)) {
   		JPushInterface.resumePush(getApplicationContext());

	}else{
		JPushInterface.stopPush(getApplicationContext());
	}
        
    }
    
    public static MyApplication getInstance(){
    	if(myapp == null){
    		myapp = new MyApplication();
    	}
    	
    	return myapp;
    }
    
    public void addActivity(Activity a){
    	activityList.add(a);
    }
    
    public void exitActivity(){
    	for(Activity a:activityList){
    		a.finish();
    	}
    }
}
