package com.android.gesture.builder;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatWindowBigView extends LinearLayout {  
	  
    /** 
     * 记录大悬浮窗的宽度 
     */  
    public static int viewWidth;  
  
    /** 
     * 记录大悬浮窗的高度 
     */  
    public static int viewHeight;  
    
    private final File mStoreFile = new File(
			Environment.getExternalStorageDirectory(), "gestures");

	// 手势库
	GestureLibrary mGestureLib;
  
    public FloatWindowBigView(final Context context) {  
        super(context);  
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);  
        View view = findViewById(R.id.big_window_layout);  
        viewWidth = view.getLayoutParams().width;  
        viewHeight = view.getLayoutParams().height;  
        Button close = (Button) findViewById(R.id.close);  
        Button back = (Button) findViewById(R.id.back);  
        close.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service  
                MyWindowManager.removeBigWindow(context);  
                MyWindowManager.removeSmallWindow(context);  
                Intent intent = new Intent(getContext(), FloatWindowService.class);  
                context.stopService(intent);  
            }  
        });  
        back.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗  
                MyWindowManager.removeBigWindow(context);  
                MyWindowManager.createSmallWindow(context);  
            }  
        });  
        Button app = (Button)findViewById(R.id.app);
        app.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗  
            	MyWindowManager.removeBigWindow(context);
            	GestureBuilderActivity tb = new GestureBuilderActivity();
                Intent intent = new Intent();   
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, tb.getClass());       
                context.startActivity(intent);
            }  
        });

        
        
        //以下是手势识别
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.myGestures1);

		gestures.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);

		gestures.setFadeOffset(2000); // 多笔画每两次的间隔时间
		gestures.setGestureColor(Color.CYAN);// 画笔颜色
		gestures.setGestureStrokeWidth(6);// 画笔粗细值
		
		// 手势识别的监听器
		gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
			@Override
			public void onGesturePerformed(GestureOverlayView overlay,
					Gesture gesture) {
				// 从手势库中查询匹配的内容，匹配的结果可能包括多个相似的结果，匹配度高的结果放在最前面
				ArrayList<Prediction> predictions = mGestureLib
						.recognize(gesture); 	
				if (predictions.size() > 0) {
					Prediction prediction = (Prediction) predictions.get(0);
					// 匹配的手势
					if (prediction.score > 1.0) { // 越匹配score的值越大，最大为10
						
//						Toast.makeText(FloatWindowBigView.this,
//								prediction.name, Toast.LENGTH_SHORT).show();
						
						String Name = prediction.name;
						
						if(Name.charAt(0) == 'h' && Name.charAt(1) == 't' && Name.charAt(2) == 't' && Name.charAt(3) == 'p' && Name.charAt(4) == ':' && Name.charAt(5) == '/' && Name.charAt(6) == '/'){
							/*
							 * 手势的名字是网址，打开相应的浏览器
							 */
							System.out.println("open " + Name);
							Intent intent = new Intent();
					        intent.setAction("android.intent.action.VIEW");
					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					        Uri content_url = Uri.parse(Name);
					        intent.setData(content_url);
					        context.startActivity(intent);
					        MyWindowManager.removeBigWindow(context);  
			                MyWindowManager.createSmallWindow(context); 
						}else{
							/*
							 * 手势是应用程序
							 */
							
							// 获取PackageManager的对象
							PackageManager pm = context.getPackageManager();
							// 得到系统安装的所有程序包的PackageInfo对象
							List<PackageInfo> packs = pm.getInstalledPackages(0);
							for(int i=0; i<packs.size(); i++){
								PackageInfo pi = packs.get(i);
								if(pi.applicationInfo.loadLabel(context.getPackageManager()).toString().equals(Name)){
									context.startActivity(context.getPackageManager()
											.getLaunchIntentForPackage(
													pi.packageName));
									MyWindowManager.removeBigWindow(context);  
					                MyWindowManager.createSmallWindow(context); 
								}
							}
						}
						
//						for(int i=0; i<100000000; i++);
//						Intent in = new Intent(GesturePerformedActivity.this, GestureBuilderActivity.class);
//						startActivity(in);
						/******/
					}
				}
			}
		});

		if (mGestureLib == null) {
			mGestureLib = GestureLibraries.fromFile(mStoreFile);
			mGestureLib.load();
		}

    }  
}  
