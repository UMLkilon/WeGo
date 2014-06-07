package com.example.wego;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RunFinishActivity extends Activity {
	private int mStepValue;			//mStepValueView的值
    private float mDistanceValue;	//mDistanceValueView的值
    private float mSpeedValue;		//mSpeedValueView的值
    private float mCaloriesValue;	//mCaloriesValueView的值
    private String mTimeValue;
    private String mdesireCalorie;
    private String mdesireStep;
	private TextView mCalorie;
	private TextView mStep;
	private TextView mDistance;
	private TextView mspeed;
	private TextView mtime;
	private TextView mDesireColarie;
	private TextView mDesireStep;
	private Button done;
	private SharedPreferences mState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mState = getSharedPreferences("state", 0);
		mdesireCalorie = mState.getString("desire_calorie", "0");
		mdesireStep = mState.getString("desire_step", "0");
		if(mdesireCalorie == "0" && mdesireStep == "0") {
			setContentView(R.layout.runfinishwithoutplan);
			getDataAndSetText(); 
		}
	    else {
			setContentView(R.layout.runfinish);
			getDataAndSetText();
			mDesireColarie = (TextView) findViewById(R.id.finishDesireCalorie);
			mDesireStep = (TextView) findViewById(R.id.finishDesireStep);
			mDesireColarie.setText(mdesireCalorie);
			mDesireStep.setText(mdesireStep);
			
	    }
		done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() { 
        	public void onClick(View v) {  
        		
                Intent intent = new Intent();
        		intent.setClass(RunFinishActivity.this, MainActivity.class); 	
        		startActivity(intent);
        		finish(); 
        	} 
        }); 
		
	}
	
	/*
	 * 得到共享内存中的数据后重置数据
	 */
	private void getDataAndSetText()
	{
		mCalorie = (TextView) findViewById(R.id.finishCalorie);
		mStep = (TextView) findViewById(R.id.finishStep);
		mDistance = (TextView) findViewById(R.id.finishDistance);
		mspeed = (TextView) findViewById(R.id.finishSpeed);
		mtime = (TextView) findViewById(R.id.finishTime);
		
		
		mStepValue = mState.getInt("steps", 0);
		mCaloriesValue = mState.getFloat("calories", (float)0.0);
		mDistanceValue = mState.getFloat("distance", (float)0.0);
		mSpeedValue = mState.getFloat("speed", (float)0.0);
		mTimeValue = mState.getString("runing_time", "00:00");
		     
		
		SharedPreferences.Editor mStateEditor = mState.edit();
		mStateEditor.putInt("steps", 0);
		mStateEditor.putFloat("calories", (float)0.0);
		mStateEditor.putFloat("distance", (float)0.0);
		mStateEditor.putFloat("speed", (float)0.0);
		mStateEditor.putString("runing_time", "00:00");
		mStateEditor.commit();
		mCalorie.setText(String.valueOf((int)mCaloriesValue));
		mStep.setText(String.valueOf((int)mStepValue));
		mDistance.setText(String.valueOf((int)mDistanceValue));
		mspeed.setText(String.valueOf((int)mSpeedValue));
		mtime.setText(mTimeValue);
	}
}
