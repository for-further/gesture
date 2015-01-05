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
	// 手势库
	GestureLibrary mGestureLib;
	
	private TextView HHmm, Date, Battery;
	BatteryReceiver batteryReceiver;
	
	SharedPreferences mShared = null;
	public final static String SHARED_keyword = "first";//文件名
	public final static String KEY_keyword = "first";//关键字名
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("LockScreen");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //设置全屏
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.lock_screen);
		
		Bitmap background = SaveBitmap.getImageFromSDCard("background");//从SD卡中取出锁屏背景
		
		startService(new Intent(LockScreen.this, LockService.class));//启动服务

		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.mlock);
		LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
		HHmm = (TextView)findViewById(R.id.HHmm);
		Date = (TextView)findViewById(R.id.Date);
		Battery = (TextView)findViewById(R.id.Battery);
		
		//注册广播接受者java代码
		//过滤广播
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		//创建广播接受者对象
		batteryReceiver = new BatteryReceiver();
		//注册receiver(电池信息的广播)
		registerReceiver(batteryReceiver, intentFilter);
		//注册Home键的广播
		registerReceiver(receiver, new IntentFilter(  
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		if(background != null){
			System.out.println("get   background");
			int sdk = android.os.Build.VERSION.SDK_INT;//得到SDK版本
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {//android4.1以前的版本
			    layout.setBackgroundDrawable(new BitmapDrawable(background));
			} else {
				layout.setBackground(new BitmapDrawable(background));
			}
		}
		
		setTime();//设置时间

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
				System.out.println("123");
				ArrayList<Prediction> predictions = mGestureLib
						.recognize(gesture); 	
				if (predictions.size() > 0) {
					Prediction prediction = (Prediction) predictions.get(0);
					// 匹配的手势
					System.out.println(prediction.name);
					if (prediction.score > 1.0 && prediction.name.equals("锁屏手势")) {
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
	
	//屏蔽掉Back键
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
                mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份  
                mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份  
                mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码  
                mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
                if("1".equals(mWay)){  
                    mWay ="天";  
                }else if("2".equals(mWay)){  
                    mWay ="一";  
                }else if("3".equals(mWay)){  
                    mWay ="二";  
                }else if("4".equals(mWay)){  
                    mWay ="三";  
                }else if("5".equals(mWay)){  
                    mWay ="四";  
                }else if("6".equals(mWay)){  
                    mWay ="五";  
                }else if("7".equals(mWay)){  
                    mWay ="六";  
                }  
                String currentTime =  mYear + "-" + mMonth + "-" + mDay+"    "+"星期"+mWay; 
                message = handler.obtainMessage(UPDATE_MY_Date, currentTime);  
                handler.sendMessage(message);  
                
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");    
                currentTime=sdf.format(new java.util.Date()); //  new java.util.Date()获取时间 
                message = handler.obtainMessage(UPDATE_MY_Time, currentTime);  
                handler.sendMessage(message);  
                //use Handler to control the time  
                handler.postDelayed(this, 3000);  // 3秒更新一次
            }  
        };  
        updateCurrentTime.start(); 
	}
	
	/**
	 * 广播接受者
	 */
	class BatteryReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//判断它是否是为电量变化的Broadcast Action
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
				//获取当前电量
				int level = intent.getIntExtra("level", 0);
				System.out.println("Level   " + level);
//				//电量的总刻度
				int scale = intent.getIntExtra("scale", 100);
				System.out.println("scale   " + scale);
				//把它转成百分比
				String battery = "电池电量:" + String.valueOf(level*100/scale) + "%";
				Battery.setText(battery);
			}
		}
		
	}
	
	/** 
     * 监听是否点击了home键将客户端推到后台 
     */  
	private BroadcastReceiver receiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
        	System.out.println("1234");
            String action = intent.getAction();//接收
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
                			Toast.makeText(getApplicationContext(), "请将gesture builder设置成默认选项", Toast.LENGTH_LONG).show();
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
    	System.out.println("I get out, myBroadCastReceiver注销了!");
    	unregisterReceiver(batteryReceiver);
    	unregisterReceiver(receiver);
    	System.out.println("23232");
    }
}
