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

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GestureBuilderActivity extends ListActivity {
    private static final int STATUS_SUCCESS = 0;//加载手势成功
    private static final int STATUS_CANCELLED = 1;//取消加载
    private static final int STATUS_NO_STORAGE = 2;//不能读取SD卡
    private static final int STATUS_NOT_LOADED = 3;

    private static final int MENU_ID_RENAME = 1;
    private static final int MENU_ID_REMOVE = 2;

    private static final int DIALOG_RENAME_GESTURE = 1;

    private static final int REQUEST_NEW_GESTURE = 1;
    
    // Type: long (id)
    private static final String GESTURES_INFO_ID = "gestures.info_id";

    //SD卡的gesture文件
    private final File mStoreFile = new File(Environment.getExternalStorageDirectory(), "gestures");

    /*
     * 手势排序
     * NameGesture是自定义的class
     *  Comparator是个类模板。Comparator<T>是从其模板里生成一个针对T类型的Comparator类的实例。
     */
    private final Comparator<NamedGesture> mSorter = new Comparator<NamedGesture>() {
        public int compare(NamedGesture object1, NamedGesture object2) {
            return object1.name.compareTo(object2.name);
        }
    };

    private static GestureLibrary sStore;

    private GesturesAdapter mAdapter;//GesturesAdapter是自定义类型
    private GesturesLoadTask mTask;//GestureLoadTask是自定义类型
    private TextView mEmpty;

    private Dialog mRenameDialog;//对话框
    private EditText mInput;
    private NamedGesture mCurrentRenameGesture;
    
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gestures_list);

        mAdapter = new GesturesAdapter(this);
        setListAdapter(mAdapter);

        if (sStore == null) {
             sStore = GestureLibraries.fromFile(mStoreFile);
        }
        mEmpty = (TextView) findViewById(android.R.id.empty);
        loadGestures();

        registerForContextMenu(getListView());//注册ContextMenu（长按2秒弹出浮动菜单）,在onCreateContextMenu中实现
        
        button = (Button)findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(GestureBuilderActivity.this, ChooseActivity.class);
				startActivity(intent);
			}
		});
        
        //悬浮窗
        Button startFloatWindow = (Button) findViewById(R.id.start_float_window); 
        Button stopFloatWindow = (Button) findViewById(R.id.stop_float_window);
        startFloatWindow.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View arg0) {
            	Toast.makeText(GestureBuilderActivity.this, "start", Toast.LENGTH_SHORT).show();
            		Intent intent = new Intent(GestureBuilderActivity.this, FloatWindowService.class);  
            		startService(intent);  
            		finish();

            }           
        });  
        stopFloatWindow.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View arg0) {
            		Toast.makeText(GestureBuilderActivity.this, "stop", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GestureBuilderActivity.this, FloatWindowService.class);  
                    //终止FxService  
                    stopService(intent); 
                    finish();
            }           
        }); 
        
        
    }

    static GestureLibrary getStore() {
        return sStore;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void reloadGestures(View v) {
        loadGestures();
    }
    
    	

    
//    @SuppressWarnings({"UnusedDeclaration"})
//    public void addGesture(View v) {
//        Intent intent = new Intent(GestureBuilderActivity.this, ChooseActivity.class);
//        startActivity(intent);
////        startActivityForResult(intent, REQUEST_NEW_GESTURE);
//    }
    
    @SuppressWarnings({"UnusedDeclaration"})
    public void gesturePerform(View v) {
    	Intent intent=new Intent(this,GesturePerformedActivity.class);
    	startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NEW_GESTURE:
                    loadGestures();
                    break;
            }
        }
    }

    private void loadGestures() {
        if (mTask != null && mTask.getStatus() != GesturesLoadTask.Status.FINISHED) {//将上次未完成的任务取消
            mTask.cancel(true);
        }        
        mTask = (GesturesLoadTask) new GesturesLoadTask().execute();//开始加载任务
    }

    @Override
    protected void onDestroy() {//系统销毁了这个Activity的实例在内存中占据的空间。
        super.onDestroy();

        if (mTask != null && mTask.getStatus() != GesturesLoadTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
        }

        cleanupRenameDialog();
    }

    private void checkForEmpty() {//检查是否没有手势
        if (mAdapter.getCount() == 0) {
            mEmpty.setText(R.string.gestures_empty);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {//遇到意外情况，系统可能销毁Activity时调用
        super.onSaveInstanceState(outState);

        if (mCurrentRenameGesture != null) {
            outState.putLong(GESTURES_INFO_ID, mCurrentRenameGesture.gesture.getID());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {//Activity确实被系统销毁时调用
        super.onRestoreInstanceState(state);

        long id = state.getLong(GESTURES_INFO_ID, -1);
        if (id != -1) {
            final Set<String> entries = sStore.getGestureEntries();
out:        for (String name : entries) {
                for (Gesture gesture : sStore.getGestures(name)) {
                    if (gesture.getID() == id) {
                        mCurrentRenameGesture = new NamedGesture();
                        mCurrentRenameGesture.name = name;
                        mCurrentRenameGesture.gesture = gesture;
                        break out;
                    }
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        
        //当显示 AdapterView 的上下文菜单时，为 onCreateContextMenu(ContextMenu, View, ContextMenuInfo) 回调函数提供的额外的菜单信息.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //添加菜单标题
        menu.setHeaderTitle(((TextView) info.targetView).getText());//targetview:用于显示上下文菜单的子视图.也是 AdapterView 的子视图之一.getText：得到其中的text，这里是手势的名字
        //添加菜单项
        menu.add(0, MENU_ID_RENAME, 0, R.string.gestures_rename);
        menu.add(0, MENU_ID_REMOVE, 0, R.string.gestures_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        final NamedGesture gesture = (NamedGesture) menuInfo.targetView.getTag();

        switch (item.getItemId()) {
            case MENU_ID_RENAME:
                renameGesture(gesture);
                return true;
            case MENU_ID_REMOVE:
                deleteGesture(gesture);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void renameGesture(NamedGesture gesture) {
        mCurrentRenameGesture = gesture;
        showDialog(DIALOG_RENAME_GESTURE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_RENAME_GESTURE) {
            return createRenameDialog();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG_RENAME_GESTURE) {
            mInput.setText(mCurrentRenameGesture.name);
        }
    }

    private Dialog createRenameDialog() {
        final View layout = View.inflate(this, R.layout.dialog_rename, null);
        mInput = (EditText) layout.findViewById(R.id.name);
        ((TextView) layout.findViewById(R.id.label)).setText(R.string.gestures_rename_label);
        
        //设置对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(0);
        builder.setTitle(getString(R.string.gestures_rename_title));
        builder.setCancelable(true);//设置对话可取消
        builder.setOnCancelListener(new Dialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                cleanupRenameDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_action),
            new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    cleanupRenameDialog();
                }
            }
        );
        builder.setPositiveButton(getString(R.string.rename_action),
            new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    changeGestureName();
                }
            }
        );
        builder.setView(layout);
        return builder.create();
    }

    private void changeGestureName() {
        final String name = mInput.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            final NamedGesture renameGesture = mCurrentRenameGesture;
            final GesturesAdapter adapter = mAdapter;
            final int count = adapter.getCount();

            // Simple linear search, there should not be enough items to warrant
            // a more sophisticated search
            for (int i = 0; i < count; i++) {
                final NamedGesture gesture = adapter.getItem(i);
                if (gesture.gesture.getID() == renameGesture.gesture.getID()) {
                    sStore.removeGesture(gesture.name, gesture.gesture);
                    gesture.name = mInput.getText().toString();
                    sStore.addGesture(gesture.name, gesture.gesture);
                    /****/
                    sStore.save();
                    break;
                }
            }

            adapter.notifyDataSetChanged();
        }
        mCurrentRenameGesture = null;
    }

    private void cleanupRenameDialog() {
        if (mRenameDialog != null) {
            mRenameDialog.dismiss();//对话框消失
            mRenameDialog = null;
        }
        mCurrentRenameGesture = null;
    }

    private void deleteGesture(NamedGesture gesture) {
        sStore.removeGesture(gesture.name, gesture.gesture);
        sStore.save();

        final GesturesAdapter adapter = mAdapter;
        adapter.setNotifyOnChange(false);
        adapter.remove(gesture);
        adapter.sort(mSorter);
        checkForEmpty();
        adapter.notifyDataSetChanged();

        Toast.makeText(this, R.string.gestures_delete_success, Toast.LENGTH_SHORT).show();
    }

    private class GesturesLoadTask extends AsyncTask<Void, NamedGesture, Integer> {
        private int mThumbnailSize;//手势图片大小
        private int mThumbnailInset;//手势图片边距
        private int mPathColor;//手势颜色

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final Resources resources = getResources();
            mPathColor = resources.getColor(R.color.gesture_color);
            mThumbnailInset = (int) resources.getDimension(R.dimen.gesture_thumbnail_inset);
            mThumbnailSize = (int) resources.getDimension(R.dimen.gesture_thumbnail_size);

            findViewById(R.id.addButton).setEnabled(false);
//            findViewById(R.id.reloadButton).setEnabled(false);
            
            mAdapter.setNotifyOnChange(false);            
            mAdapter.clear();
        }

        @Override
        protected Integer doInBackground(Void... params) {//params是输入参数
            if (isCancelled()) return STATUS_CANCELLED;
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return STATUS_NO_STORAGE;//不能读取SD卡
            }

            final GestureLibrary store = sStore;

            if (store.load()) {//手势文件加载成功
                for (String name : store.getGestureEntries()) {//先得到手势的名字
                    if (isCancelled()) break;

                    for (Gesture gesture : store.getGestures(name)) {//再根据名字得到手势
                        final Bitmap bitmap = gesture.toBitmap(mThumbnailSize, mThumbnailSize,
                                mThumbnailInset, mPathColor);
                        final NamedGesture namedGesture = new NamedGesture();
                        namedGesture.gesture = gesture;
                        namedGesture.name = name;

                        mAdapter.addBitmap(namedGesture.gesture.getID(), bitmap);
                        publishProgress(namedGesture);//操作UI，调用onProgressUpdate
                    }
                }

                return STATUS_SUCCESS;
            }

            return STATUS_NOT_LOADED;
        }

        @Override
        protected void onProgressUpdate(NamedGesture... values) {//在线程中更新UI
            super.onProgressUpdate(values);

            final GesturesAdapter adapter = mAdapter;
            adapter.setNotifyOnChange(false);

            for (NamedGesture gesture : values) {
                adapter.add(gesture);
            }

            adapter.sort(mSorter);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Integer result) {//在doInBackGround之后运行，在UI线程中。result是doInbackGround的返回值
            super.onPostExecute(result);

            if (result == STATUS_NO_STORAGE) {//不能读取SD卡
                getListView().setVisibility(View.GONE);//将listview设成不可见
                mEmpty.setVisibility(View.VISIBLE);//mEmpty设置成可见
                mEmpty.setText(getString(R.string.gestures_error_loading,
                        mStoreFile.getAbsolutePath()));
            } else {
                findViewById(R.id.addButton).setEnabled(true);
//                findViewById(R.id.reloadButton).setEnabled(true);
                checkForEmpty();
            }
        }
    }

    static class NamedGesture {
        String name;
        Gesture gesture;
    }

    private class GesturesAdapter extends ArrayAdapter<NamedGesture> {
        private final LayoutInflater mInflater;//用来载入界面
        private final Map<Long, Drawable> mThumbnails = Collections.synchronizedMap(
                new HashMap<Long, Drawable>());//线程安全的HashMap,让HashMap支持多线程同步

        public GesturesAdapter(Context context) {
            super(context, 0);
            //通过getSystemService(Context.LAYOUT_INFLATER_SERVICE)得到一个LayoutInflater
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void addBitmap(Long id, Bitmap bitmap) {
            mThumbnails.put(id, new BitmapDrawable(bitmap));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.gestures_item, parent, false);
            }

            final NamedGesture gesture = getItem(position);
            final TextView label = (TextView) convertView;//回收滑出屏幕的view

            label.setTag(gesture);//把gesture设成tag缓存起来方便多次重用
            label.setText(gesture.name);
            label.setCompoundDrawablesWithIntrinsicBounds(mThumbnails.get(gesture.gesture.getID()),
                    null, null, null);//setCompoundDrawablesWithIntrinsicBounds是画的drawable的宽高是按drawable固定的宽高

            return convertView;
        }
    }
}
