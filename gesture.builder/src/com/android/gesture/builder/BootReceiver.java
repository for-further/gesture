/**
 * 1�����ÿ�����Ҫ�򿪵ķ���İ�������������Ctrl+F����"#"�ַ��ɿ��ٶ�λ��
 * 
 * 
 * **/

package com.android.gesture.builder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//����ʱ����
public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		//������������
		Intent myIntent=new Intent(context, LockService.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(myIntent);	
	}
}
