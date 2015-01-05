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
	private KeyguardManager keyguardManager = null;//������ϵͳ��������������
	private KeyguardManager.KeyguardLock keyguardLock = null;//���ڲٿ�����
	Intent toMainIntent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("service   create");
		//����intent
		toMainIntent = new Intent(LockService.this, LockScreen.class);//#����LockScreen.classΪҪ��ת���Ľ��棬�ȵ�����ʱҪ�򿪵Ľ���
		toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		//ע��㲥
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		registerReceiver(screenReceiver, intentFilter);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;//ճ�Խ���,���service��kill��ϵͳ���Զ�����service
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenReceiver);
		//�����˷���
		startActivity(new Intent(LockService.this,LockService.class));
	}
	
	private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

		@SuppressWarnings("static-access")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(TAG, "intent.action = " + action);

			if (action.equals("android.intent.action.SCREEN_ON") || action.equals("android.intent.action.SCREEN_OFF")) {

				//�ر�����
				keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);//�����������
				System.out.println("!!!!!");
				keyguardLock = keyguardManager.newKeyguardLock("");
				keyguardLock.disableKeyguard();
				Log.e("", "closed the keyGuard");
				
				//��������
				startActivity(toMainIntent);
			}

		}
	};

}
