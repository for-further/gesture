package com.android.gesture.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WebSite extends Activity {
	
	private Button Done;
	private EditText Name;
	private String web;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_website);
		Done = (Button)findViewById(R.id.DoneWeb);
		Name = (EditText)findViewById(R.id.webname);
		Name.setEnabled(true);
		
		WebListener listener = new WebListener();
		Done.setOnClickListener(listener);
		
	}
	
	class WebListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.DoneWeb){
				web = Name.getText().toString();
				System.out.println(web);
				Intent intent = new Intent(WebSite.this, CreateGestureActivity.class);
				intent.putExtra("name", web);
				startActivity(intent);
			}
		}
		
	}

}
