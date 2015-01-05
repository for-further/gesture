/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gesture.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.MotionEvent;
import android.gesture.GestureLibraries;
import android.gesture.GestureOverlayView;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CreateGestureActivity extends Activity {
    private static final float LENGTH_THRESHOLD = 120.0f;

    private Gesture mGesture;
    private View mDoneButton;
    private String NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.create_gesture);

        mDoneButton = findViewById(R.id.done);

        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
      
        
        /*
         * 设置手势可多笔绘制，默认为单笔
         */
        overlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);  
		
        overlay.setFadeOffset(2000);  //多笔画每两次的间隔时间
        overlay.setGestureColor(Color.CYAN);//画笔颜色
        overlay.setGestureStrokeWidth(6);//画笔粗细值
        
        overlay.addOnGestureListener(new GesturesProcessor());
        
        Intent intent =getIntent();
        NAME = intent.getStringExtra("name");
        System.out.println(NAME);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            final GestureOverlayView overlay =
                    (GestureOverlayView) findViewById(R.id.gestures_overlay);
            overlay.post(new Runnable() {
                public void run() {
                    overlay.setGesture(mGesture);
                }
            });

            mDoneButton.setEnabled(true);
        }
    }

    public void addGesture(View v) {
        if (mGesture != null) {  
            /**
             * 获取手势库
             *   private final File mStoreFile = new File(Environment.getExternalStorageDirectory(), "gestures");
             *   GestureLibrary sStore = GestureLibraries.fromFile(GestureLibrary);
             * 
             */
            final GestureLibrary store = GestureBuilderActivity.getStore();
            
            if(NAME.equals("锁屏手势")){
            	if(store.load()){
            		for (String name : store.getGestureEntries()) //先得到手势的名字
            		if(name.equals("锁屏手势")){
            			 for (Gesture gesture : store.getGestures(name)){
            				 store.removeGesture(name, gesture);
            			 }
            			 store.save();
            			 break;
            		}
            	}
            }
            
            store.addGesture(NAME, mGesture);
            store.save();
            
            setResult(RESULT_OK);

            final String path = new File(Environment.getExternalStorageDirectory(),
                    "gestures").getAbsolutePath();
            Toast.makeText(this, getString(R.string.save_success, path), Toast.LENGTH_LONG).show();
        } else {
            setResult(RESULT_CANCELED);
        }
        if(NAME.equals("锁屏手势")){
        	System.out.println("lock start");
        	Intent in = new Intent(CreateGestureActivity.this, LockScreen.class);
        	startActivity(in);
        	finish();
        	System.out.println("create finish");
        }else{
        	Intent in = new Intent(CreateGestureActivity.this, GestureBuilderActivity.class);
        	startActivity(in);
        	finish();
        }
        
    }
  
    public void cancelGesture(View v) {
        setResult(RESULT_CANCELED);
        Intent in = new Intent(CreateGestureActivity.this, GestureBuilderActivity.class);
        startActivity(in);
    }
    
    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mDoneButton.setEnabled(false);
            mGesture = null;
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }

        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(false);//清除手势
            }
            mDoneButton.setEnabled(true);
        }

        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }
    }
}
