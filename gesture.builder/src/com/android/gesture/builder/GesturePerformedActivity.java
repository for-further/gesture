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

	// ���ƿ�
	GestureLibrary mGestureLib;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gesture_perform);
		// ���ƻ���
		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures_overlay);

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
						Toast.makeText(GesturePerformedActivity.this,
								prediction.name, Toast.LENGTH_SHORT).show();
						
						String Name = prediction.name;
						
						if(Name.charAt(0) == 'h' && Name.charAt(1) == 't' && Name.charAt(2) == 't' && Name.charAt(3) == 'p' && Name.charAt(4) == ':' && Name.charAt(5) == '/' && Name.charAt(6) == '/'){
							/*
							 * ���Ƶ���������ַ������Ӧ�������
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
							intent.setData(Uri.parse("content://mms-sms/conversations/" + phone));//��Ϊ����
							startActivity(intent);
						}else{
							/*
							 * ������Ӧ�ó���
							 */
							
							// ��ȡPackageManager�Ķ���
							PackageManager pm = getPackageManager();
							// �õ�ϵͳ��װ�����г������PackageInfo����
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
								"û��ƥ�������", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					System.out.println("!!!!!");
					Toast.makeText(GesturePerformedActivity.this,
							"û��ƥ�������", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (mGestureLib == null) {
			mGestureLib = GestureLibraries.fromFile(mStoreFile);
			mGestureLib.load();
		}
	}

}
