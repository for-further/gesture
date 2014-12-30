package com.android.gesture.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class GesturePerformedActivity extends Activity {

	private final File mStoreFile = new File(
			Environment.getExternalStorageDirectory(), "gestures");

	// 手势库
	GestureLibrary mGestureLib;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gesture_perform);
		// 手势画板
		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures_overlay);

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
						Toast.makeText(GesturePerformedActivity.this,
								prediction.name, Toast.LENGTH_SHORT).show();
						
						String Name = prediction.name;
						
						if(Name.charAt(0) == 'h' && Name.charAt(1) == 't' && Name.charAt(2) == 't' && Name.charAt(3) == 'p' && Name.charAt(4) == ':' && Name.charAt(5) == '/' && Name.charAt(6) == '/'){
							/*
							 * 手势的名字是网址，打开相应的浏览器
							 */
							System.out.println("open " + Name);
							Intent intent = new Intent();
					        intent.setAction("android.intent.action.VIEW");
					        Uri content_url = Uri.parse(Name);
					        intent.setData(content_url);
					        startActivity(intent);
						}else if(Name.indexOf("make phone call") == 0){
							String phone = "";
							int flag = 0;
							for(int i=0; i<Name.length() - 1; i++){
								if(flag == 1) phone += Name.charAt(i);
								if(Name.charAt(i) == '(') flag = 1;
							}
							System.out.println(phone);
							Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:" + phone));
							startActivity(dialIntent);
						}else if(Name.indexOf("send message") == 0){
							String phone = "";
							int flag = 0;
							for(int i=0; i<Name.length() - 1; i++){
								if(flag == 1) phone += Name.charAt(i);
								if(Name.charAt(i) == '(') flag = 1;
							}
							System.out.println(phone);
							Intent intent = new Intent(Intent.ACTION_VIEW); 
							intent.setType("vnd.android-dir/mms-sms");  
							intent.setData(Uri.parse("content://mms-sms/conversations/" + phone));//此为号码
							startActivity(intent);
						}else{
							/*
							 * 手势是应用程序
							 */
							
							// 获取PackageManager的对象
							PackageManager pm = getPackageManager();
							// 得到系统安装的所有程序包的PackageInfo对象
							List<PackageInfo> packs = pm.getInstalledPackages(0);
							for(int i=0; i<packs.size(); i++){
								PackageInfo pi = packs.get(i);
								if(pi.applicationInfo.loadLabel(getPackageManager()).toString().equals(Name)){
									GesturePerformedActivity.this.startActivity(getPackageManager()
											.getLaunchIntentForPackage(
													pi.packageName));
								}
							}
						}
//						for(int i=0; i<100000000; i++);
//						Intent in = new Intent(GesturePerformedActivity.this, GestureBuilderActivity.class);
//						startActivity(in);
						/******/
					}else{
						System.out.println("!!!!!");
						Toast.makeText(GesturePerformedActivity.this,
								"没有匹配的手势", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					System.out.println("!!!!!");
					Toast.makeText(GesturePerformedActivity.this,
							"没有匹配的手势", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (mGestureLib == null) {
			mGestureLib = GestureLibraries.fromFile(mStoreFile);
			mGestureLib.load();
		}
	}

}
