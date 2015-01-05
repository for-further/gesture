package com.android.gesture.builder;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockService extends Service {
	
	private String TAG = "ScreenReceiver Log";
	private KeyguardManager keyguardManager = null;//用来对系统的屏保进行屏蔽
	private KeyguardManager.KeyguardLock keyguardLock = null;//用于操控锁屏
	Intent toMainIntent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("service   create");
		//设置intent
		toMainIntent = new Intent(LockService.this, LockScreen.class);//#设置LockScreen.class为要跳转到的界面，既当解锁时要打开的界面
		toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		//注册广播
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		registerReceiver(screenReceiver, intentFilter);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;//粘性进程,如果service被kill，系统将自动重启service
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenReceiver);
		//重启此服务
		startActivity(new Intent(LockService.this,LockService.class));
	}
	
	private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

		@SuppressWarnings("static-access")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(TAG, "intent.action = " + action);

			if (action.equals("android.intent.action.SCREEN_ON") || action.equals("android.intent.action.SCREEN_OFF")) {

				//关闭锁屏
				keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);//获得锁屏服务
				System.out.println("!!!!!");
				keyguardLock = keyguardManager.newKeyguardLock("");
				keyguardLock.disableKeyguard();
				Log.e("", "closed the keyGuard");
				
				//打开主界面
				startActivity(toMainIntent);
			}

		}
	};

}
