/**
 * 1，设置开机后要打开的服务的包名和类名（用Ctrl+F搜索"#"字符可快速定位）
 * 
 * 
 * **/

package com.android.gesture.builder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//开机时启动
public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		//启动监听服务
		Intent myIntent=new Intent(context, LockService.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(myIntent);	
	}
}
