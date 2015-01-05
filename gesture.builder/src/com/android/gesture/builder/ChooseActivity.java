package com.android.gesture.builder;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ChooseActivity extends Activity {
	
	private TextView site, app, call, message, lock, lockimage;
	private Button button;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("choose");
		setContentView(R.layout.gestures_name);
		site = (TextView)findViewById(R.id.opensite);
		app = (TextView)findViewById(R.id.openapp);
		call = (TextView)findViewById(R.id.call);
		message = (TextView)findViewById(R.id.message);
		lock = (TextView)findViewById(R.id.lock);
		lockimage = (TextView)findViewById(R.id.lockimage);
		button = (Button)findViewById(R.id.back);
		
		ChooseListener listener = new ChooseListener();
		site.setOnClickListener(listener);
		app.setOnClickListener(listener);
		call.setOnClickListener(listener);
		message.setOnClickListener(listener);
		lock.setOnClickListener(listener);
		lockimage.setOnClickListener(listener);
		button.setOnClickListener(listener);
		
	}
	class ChooseListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == R.id.opensite){
				Intent intent = new Intent(ChooseActivity.this, WebSite.class);
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.openapp){
				Intent intent = new Intent(ChooseActivity.this, AppList.class);
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.call){
				System.out.println("abc");
				Intent intent = new Intent(ChooseActivity.this, Contacts.class);
				intent.putExtra("type", "call");
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.message){
				Intent intent = new Intent(ChooseActivity.this, Contacts.class);
				intent.putExtra("type", "message");
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.lock){
				Intent intent = new Intent(ChooseActivity.this, CreateGestureActivity.class);
				intent.putExtra("name", "��������");
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.lockimage){
				System.out.println("abc");
				Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
  
                /* ȡ����Ƭ�󷵻ر����� */  
                System.out.println("ghi");
                startActivityForResult(intent, 1);
                System.out.println("jkl");
			}else if(v.getId() == R.id.back){
				Intent intent = new Intent(ChooseActivity.this, GestureBuilderActivity.class);
				startActivity(intent);
				finish();
			}
		}
		
	}
	 @Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        if (resultCode == RESULT_OK && null != data && !data.equals("")) {  
	        	Uri selectedImage = data.getData();//�õ�uri��uri���������ṩ������
	        	//��ȡͼƬ��·��
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	            
	            /*
	             * �õ��Ľ��
	             * ��һ��������uri�� �ڶ�������������uriҪ���ص����ݣ�
	             * ������������ɸѡ������null��ʾ��ɸѡ��
	             * ���ĸ���������ϵ���������ʹ�ã�����������������У������滻�ɵ��ĸ�����
	             * ���������������ʲô��������
	             */
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();//��λ��һ��
	            
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);//��һ�е�����
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close(); 
	                
	            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
	            System.out.println("stu");
	            try {
					SaveBitmap.saveImage(bitmap, "background");
					System.out.println("save");
					Toast.makeText(this, "���óɹ�", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        }  
	        super.onActivityResult(requestCode, resultCode, data);  
	    }  

}
