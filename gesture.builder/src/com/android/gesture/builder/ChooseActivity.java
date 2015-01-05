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
				intent.putExtra("name", "锁屏手势");
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.lockimage){
				System.out.println("abc");
				Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
  
                /* 取得相片后返回本画面 */  
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
	        	Uri selectedImage = data.getData();//得到uri，uri用于区分提供的内容
	        	//获取图片的路径
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	            
	            /*
	             * 得到的结果
	             * 第一个参数：uri； 第二个参数：告诉uri要返回的内容；
	             * 第三个参数：筛选条件，null表示不筛选；
	             * 第四个参数：配合第三个参数使用，如果第三个参数中有？，则替换成第四个参数
	             * 第五个参数：按照什么进行排序
	             */
	            Cursor cursor = getContentResolver().query(selectedImage,
	                    filePathColumn, null, null, null);
	            cursor.moveToFirst();//定位第一行
	            
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);//第一列的名字
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close(); 
	                
	            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
	            System.out.println("stu");
	            try {
					SaveBitmap.saveImage(bitmap, "background");
					System.out.println("save");
					Toast.makeText(this, "设置成功", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        }  
	        super.onActivityResult(requestCode, resultCode, data);  
	    }  

}
