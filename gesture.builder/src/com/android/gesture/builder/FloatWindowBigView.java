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
     * ��¼���������Ŀ�� 
     */  
    public static int viewWidth;  
  
    /** 
     * ��¼���������ĸ߶� 
     */  
    public static int viewHeight;  
    
    private final File mStoreFile = new File(
			Environment.getExternalStorageDirectory(), "gestures");

	// ���ƿ�
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
                // ����ر���������ʱ���Ƴ���������������ֹͣService  
                MyWindowManager.removeBigWindow(context);  
                MyWindowManager.removeSmallWindow(context);  
                Intent intent = new Intent(getContext(), FloatWindowService.class);  
                context.stopService(intent);  
            }  
        });  
        back.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // ������ص�ʱ���Ƴ���������������С������  
                MyWindowManager.removeBigWindow(context);  
                MyWindowManager.createSmallWindow(context);  
            }  
        });  
        Button app = (Button)findViewById(R.id.app);
        app.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // ������ص�ʱ���Ƴ���������������С������  
            	MyWindowManager.removeBigWindow(context);
            	GestureBuilderActivity tb = new GestureBuilderActivity();
                Intent intent = new Intent();   
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, tb.getClass());       
                context.startActivity(intent);
            }  
        });

        
        
        //����������ʶ��
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.myGestures1);

		gestures.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);

		gestures.setFadeOffset(2000); // ��ʻ�ÿ���εļ��ʱ��
		gestures.setGestureColor(Color.CYAN);// ������ɫ
		gestures.setGestureStrokeWidth(6);// ���ʴ�ϸֵ
		
		// ����ʶ��ļ�����
		gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
			@Override
			public void onGesturePerformed(GestureOverlayView overlay,
					Gesture gesture) {
				// �����ƿ��в�ѯƥ������ݣ�ƥ��Ľ�����ܰ���������ƵĽ����ƥ��ȸߵĽ��������ǰ��
				ArrayList<Prediction> predictions = mGestureLib
						.recognize(gesture); 	
				if (predictions.size() > 0) {
					Prediction prediction = (Prediction) predictions.get(0);
					// ƥ�������
					if (prediction.score > 1.0) { // Խƥ��score��ֵԽ�����Ϊ10
						
//						Toast.makeText(FloatWindowBigView.this,
//								prediction.name, Toast.LENGTH_SHORT).show();
						
						String Name = prediction.name;
						
						if(Name.charAt(0) == 'h' && Name.charAt(1) == 't' && Name.charAt(2) == 't' && Name.charAt(3) == 'p' && Name.charAt(4) == ':' && Name.charAt(5) == '/' && Name.charAt(6) == '/'){
							/*
							 * ���Ƶ���������ַ������Ӧ�������
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
							 * ������Ӧ�ó���
							 */
							
							// ��ȡPackageManager�Ķ���
							PackageManager pm = context.getPackageManager();
							// �õ�ϵͳ��װ�����г������PackageInfo����
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
