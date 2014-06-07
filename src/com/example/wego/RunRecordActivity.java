package com.example.wego;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.adapter.RecordAdapter;
import com.database.DataBase;
import com.database.Name;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class RunRecordActivity extends Activity {
   
	private ListView recordListView = null;
	private List<Map<String, Object>> recordList = new ArrayList<Map<String,Object>>(); ;
	private RecordAdapter recordAdapter = null;
	private Context context;
	private SQLiteDatabase mSQLiteDatabase = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_record_activity);        
        context = this;
        recordListView = (ListView) findViewById(R.id.record_list);
  
        //创建或打开数据库
        mSQLiteDatabase = openOrCreateDatabase(Name.DATABASE_NAME,MODE_PRIVATE,null);
        try {
			if(!DataBase.tableIsExist(mSQLiteDatabase,Name.TABLE_NAME)){
				mSQLiteDatabase.execSQL(Name.CREATE_TABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		   
        setListData(); //初始化列表
        recordAdapter = new RecordAdapter(RecordAdapter.TYPE_SHOWRUNRECORD, this, recordList);
        recordListView.setAdapter(recordAdapter);
        
        recordListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putInt("id",(Integer)recordList.get(arg2).get("_id"));			
				Intent intent = new Intent(RunRecordActivity.this,RecordDetailActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			}
		});
		recordListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
	
			    	final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context); 
			    	dialogBuilder.setTitle("操作");
			    	final CharSequence[] methods = {"删除记录", "查看明细"}; 
			     	
			    	dialogBuilder.setItems(methods, new DialogInterface.OnClickListener() { 
			    	    public void onClick(DialogInterface dialog, int item) {
			    	    	
			    	    	int id = (Integer) recordList.get(arg2).get("_id");
			    	    	// 删除
			    	    	if(item == 0) {		    
			    	    		mSQLiteDatabase.execSQL("DELETE FROM tableRunInfo WHERE _id=" + id);
			    				recordList.remove(arg2);				
			    				recordAdapter = new RecordAdapter(RecordAdapter.TYPE_SHOWRUNRECORD, context, recordList);
			    		        recordListView.setAdapter(recordAdapter);   
			    				dialog.dismiss();
			    	    	}
			    	    	//查看明细
			    	    	else {
			    	    		Bundle bundle = new Bundle();
			    	    		bundle.putInt("id",id);
			    				Intent intent = new Intent(RunRecordActivity.this,RecordDetailActivity.class);
			    				intent.putExtras(bundle);	
			    				startActivityForResult(intent, 0);
			    				dialog.dismiss();
			    	    	}
			    	    } 
			    	}); 
			    	AlertDialog dialog = dialogBuilder.create();
			    	dialog.show();		
				return false;
			}
		});
    }
    
    @Override
    public void onDestroy()
    {
    	mSQLiteDatabase.close();
    	super.onDestroy();
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mSQLiteDatabase.close();
			RunRecordActivity.this.finish();
			return true;
		}
		return false;
	}
    
	private void setListData() {
		/*打开数据库  */
        mSQLiteDatabase = openOrCreateDatabase(Name.DATABASE_NAME,MODE_PRIVATE,null); 
		try {
			if(!DataBase.tableIsExist(mSQLiteDatabase,Name.TABLE_NAME)){
				Toast.makeText(RunRecordActivity.this, "还没有任何记录", Toast.LENGTH_SHORT).show();
			}
			else {
				//遍历数据库
		    	String col[] = {"_id", "date", "time", "distance","calorie","speed","steps" };
		    	Cursor cursor = mSQLiteDatabase.query("tableRunInfo", col, null, null, null, null, null);
				if (cursor.getCount() ==0)
					Toast.makeText(RunRecordActivity.this, "还没有任何记录", Toast.LENGTH_SHORT).show();
		        if(cursor != null){
		        	if(cursor.moveToFirst()){
		        		do{
		        			addListItem(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getFloat(3),
		        					cursor.getFloat(4),cursor.getFloat(5),cursor.getInt(6));
		        		}while(cursor.moveToNext());
		        	}
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}

	private void addListItem(Integer id,String date,String time,Float distance,Float calorie,
			Float speed,Integer steps){	
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("_id", id);
		map.put("date", date);
		map.put("time", time);
		map.put("distance", distance);
		map.put("calorie", calorie);
		map.put("speed", speed);
		map.put("steps", steps);
		recordList.add(map);
	}
}