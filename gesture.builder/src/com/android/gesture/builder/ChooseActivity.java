package com.android.gesture.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChooseActivity extends Activity {
	
	private TextView site, app, call, message;
	private Button button;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gestures_name);
		site = (TextView)findViewById(R.id.opensite);
		app = (TextView)findViewById(R.id.openapp);
		call = (TextView)findViewById(R.id.call);
		message = (TextView)findViewById(R.id.message);
		button = (Button)findViewById(R.id.back);
		
		ChooseListener listener = new ChooseListener();
		site.setOnClickListener(listener);
		app.setOnClickListener(listener);
		call.setOnClickListener(listener);
		message.setOnClickListener(listener);
		button.setOnClickListener(listener);
		
	}
	class ChooseListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.opensite){
				Intent intent = new Intent(ChooseActivity.this, WebSite.class);
				startActivity(intent);
			}else if(v.getId() == R.id.openapp){
				Intent intent = new Intent(ChooseActivity.this, AppList.class);
				startActivity(intent);
			}else if(v.getId() == R.id.call){
				System.out.println("abc");
				Intent intent = new Intent(ChooseActivity.this, Contacts.class);
				intent.putExtra("type", "call");
				startActivity(intent);
			}else if(v.getId() == R.id.message){
				Intent intent = new Intent(ChooseActivity.this, Contacts.class);
				intent.putExtra("type", "message");
				startActivity(intent);
				
			}else if(v.getId() == R.id.back){
				Intent intent = new Intent(ChooseActivity.this, GestureBuilderActivity.class);
				startActivity(intent);
			}
		}
		
	}
	

}
