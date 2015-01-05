package com.android.gesture.builder;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class LockScreen extends Activity{;	
	private final File mStoreFile = new File(
			Environment.getExternalStorageDirectory(), "gestures");
	// ���ƿ�
	GestureLibrary mGestureLib;
	
	private TextView HHmm, Date, Battery;
	BatteryReceiver batteryReceiver;
	
	SharedPreferences mShared = null;
	public final static String SHARED_keyword = "first";//�ļ���
	public final static String KEY_keyword = "first";//�ؼ�����
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("LockScreen");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //����ȫ��
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.lock_screen);
		
		Bitmap background = SaveBitmap.getImageFromSDCard("background");//��SD����ȡ����������
		
		startService(new Intent(LockScreen.this, LockService.class));//��������

		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.mlock);
		LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
		HHmm = (TextView)findViewById(R.id.HHmm);
		Date = (TextView)findViewById(R.id.Date);
		Battery = (TextView)findViewById(R.id.Battery);
		
		//ע��㲥������java����
		//���˹㲥
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		//�����㲥�����߶���
		batteryReceiver = new BatteryReceiver();
		//ע��receiver(�����Ϣ�Ĺ㲥)
		registerReceiver(batteryReceiver, intentFilter);
		//ע��Home���Ĺ㲥
		registerReceiver(receiver, new IntentFilter(  
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		if(background != null){
			System.out.println("get   background");
			int sdk = android.os.Build.VERSION.SDK_INT;//�õ�SDK�汾
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {//android4.1��ǰ�İ汾
			    layout.setBackgroundDrawable(new BitmapDrawable(background));
			} else {
				layout.setBackground(new BitmapDrawable(background));
			}
		}
		
		setTime();//����ʱ��

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
				System.out.println("123");
				ArrayList<Prediction> predictions = mGestureLib
						.recognize(gesture); 	
				if (predictions.size() > 0) {
					Prediction prediction = (Prediction) predictions.get(0);
					// ƥ�������
					System.out.println(prediction.name);
					if (prediction.score > 1.0 && prediction.name.equals("��������")) {
						finish();
					}
				}
			}
		});
		if (mGestureLib == null) {
			mGestureLib = GestureLibraries.fromFile(mStoreFile);
			mGestureLib.load();
		}
	}
	
	//���ε�Back��
	public boolean onKeyDown(int keyCode ,KeyEvent event){
		
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			return true ;
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
			return true;
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
			return true;
		}else return super.onKeyDown(keyCode, event);
	}
	
	//use of Handler  
    //messge info  
    private static final int UPDATE_MY_Date = 1;  
    private static final int UPDATE_MY_Time = 2; 
    Message message = null;  
  
    //Handler UI modification  
    @SuppressLint("HandlerLeak")  
    private Handler handler = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            switch(msg.what){  
            case UPDATE_MY_Date:  
                String currentDate = (String)msg.obj;
                Date.setText(currentDate);
                System.out.println(currentDate);
                break;  
            case UPDATE_MY_Time:
            	String currentTime = (String)msg.obj; 
            	HHmm.setText(currentTime);
                System.out.println(currentTime);
            }  
        }  
    };  
	private Thread updateCurrentTime = null;  
	String mYear, mMonth, mDay, mWay;
	public void setTime(){
		updateCurrentTime = new Thread(){  
            @Override  
            public void run() {  
                //Time Controller  
                //Modify Time After 3000 ms  
            	Calendar c = Calendar.getInstance();  
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
                mYear = String.valueOf(c.get(Calendar.YEAR)); // ��ȡ��ǰ���  
                mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// ��ȡ��ǰ�·�  
                mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// ��ȡ��ǰ�·ݵ����ں���  
                mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
                if("1".equals(mWay)){  
                    mWay ="��";  
                }else if("2".equals(mWay)){  
                    mWay ="һ";  
                }else if("3".equals(mWay)){  
                    mWay ="��";  
                }else if("4".equals(mWay)){  
                    mWay ="��";  
                }else if("5".equals(mWay)){  
                    mWay ="��";  
                }else if("6".equals(mWay)){  
                    mWay ="��";  
                }else if("7".equals(mWay)){  
                    mWay ="��";  
                }  
                String currentTime =  mYear + "-" + mMonth + "-" + mDay+"    "+"����"+mWay; 
                message = handler.obtainMessage(UPDATE_MY_Date, currentTime);  
                handler.sendMessage(message);  
                
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");    
                currentTime=sdf.format(new java.util.Date()); //  new java.util.Date()��ȡʱ�� 
                message = handler.obtainMessage(UPDATE_MY_Time, currentTime);  
                handler.sendMessage(message);  
                //use Handler to control the time  
                handler.postDelayed(this, 3000);  // 3�����һ��
            }  
        };  
        updateCurrentTime.start(); 
	}
	
	/**
	 * �㲥������
	 */
	class BatteryReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//�ж����Ƿ���Ϊ�����仯��Broadcast Action
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
				//��ȡ��ǰ����
				int level = intent.getIntExtra("level", 0);
				System.out.println("Level   " + level);
//				//�������̶ܿ�
				int scale = intent.getIntExtra("scale", 100);
				System.out.println("scale   " + scale);
				//����ת�ɰٷֱ�
				String battery = "��ص���:" + String.valueOf(level*100/scale) + "%";
				Battery.setText(battery);
			}
		}
		
	}
	
	/** 
     * �����Ƿ�����home�����ͻ����Ƶ���̨ 
     */  
	private BroadcastReceiver receiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
        	System.out.println("1234");
            String action = intent.getAction();//����
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    	//press home
                    	System.out.println("123");
                    	mShared = getSharedPreferences(SHARED_keyword, Context.MODE_PRIVATE);
                		String text = mShared.getString(KEY_keyword,"");
                		if(text.equals("")){
                			System.out.println("0000");
                			Editor editor = mShared.edit();
                			editor.putString(KEY_keyword, "1");
                			editor.commit();
                			Toast.makeText(getApplicationContext(), "�뽫gesture builder���ó�Ĭ��ѡ��", Toast.LENGTH_LONG).show();
                		}else System.out.println(text);
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //long press home
                    }
                }
            }
        }
    };
	
	public void onDestroy() {  
        super.onDestroy();  
    	handler.removeCallbacks(updateCurrentTime);
    	System.out.println("I get out, myBroadCastReceiverע����!");
    	unregisterReceiver(batteryReceiver);
    	unregisterReceiver(receiver);
    	System.out.println("23232");
    }
}
