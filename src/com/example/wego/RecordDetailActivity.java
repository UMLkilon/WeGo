package com.example.wego;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.database.DataBase;
import com.database.Name;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RecordDetailActivity extends Activity{
	private Button returnBtn;
	private TextView timeTv;
	private TextView distanceTv;
	private TextView calorieTv;
	private TextView stepsTv;
	private TextView speedTv;
	private TextView dateTv;
	private int id;
	private SQLiteDatabase mSQLiteDatabase = null;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.record_detail_activity);
	        returnBtn = (Button) findViewById(R.id.returnBtn);
	        Bundle bundle = new Bundle();
	        bundle = this.getIntent().getExtras();
	        id = bundle.getInt("id");
	        Toast.makeText(RecordDetailActivity.this, Integer.toString(id), Toast.LENGTH_SHORT).show();
	        
	        dateTv = (TextView)findViewById(R.id.dateTv);
            timeTv = (TextView)findViewById(R.id.timeTv);
            distanceTv = (TextView)findViewById(R.id.distanceTv);
            calorieTv = (TextView)findViewById(R.id.calorieTv);
            speedTv = (TextView)findViewById(R.id.speedTv);
            stepsTv = (TextView)findViewById(R.id.stepsTv);
	     	        
	        returnBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
					mSQLiteDatabase.close();
				}
			});
	        
	        showData();
	 }
	private void showData() {
		/*�����ݿ�  */
        mSQLiteDatabase = openOrCreateDatabase(Name.DATABASE_NAME,MODE_PRIVATE,null); 
		try {
			if(!DataBase.tableIsExist(mSQLiteDatabase,Name.TABLE_NAME)){
				Toast.makeText(RecordDetailActivity.this, "��û���κμ�¼", Toast.LENGTH_SHORT).show();
			}
			else {
				//�������ݿ�
		    	Cursor cursor = mSQLiteDatabase.rawQuery("select * from tableRunInfo where _id=" + Integer.toString(id), null);
				if (cursor.getCount() ==0)
					Toast.makeText(RecordDetailActivity.this, "��û���κμ�¼", Toast.LENGTH_SHORT).show();
		        if(cursor != null){
		        	if(cursor.moveToFirst()){
		        		do{
		        			setData(cursor.getString(1),cursor.getString(2),cursor.getFloat(3),
		        					cursor.getFloat(4),cursor.getFloat(5),cursor.getInt(6));
		        		}while(cursor.moveToNext());
		        	}
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}
	
	private void setData(String date,String time,Float distance,Float calorie,
			Float speed,Integer steps)
	{
		DecimalFormat df=new DecimalFormat("#.00");
		dateTv.setText(    "�Ҵ�"   + date                + "��ʼ�ܲ��˶�");        
        timeTv.setText(    "��ʱ\n" + time);        
        distanceTv.setText("·��\n" + df.format(distance) + "\n����");
        calorieTv.setText( "ȼ��\n" + df.format(calorie)  + "\n��"); 
        speedTv.setText(   "����\n" + df.format(speed)    +"\n����/Сʱ"); 
        stepsTv.setText(   "����\n" + steps               + "\n��"); 
     
	}
}
