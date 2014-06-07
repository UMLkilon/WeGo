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

/*
 * 运动计划
 */
public class PlanActivity extends Activity {
	private EditText setCalorie;
	private EditText setStep;
	private Button finish;
	private Button cancel;
	private String desireCalorie;
	private String desireStep;
	private SharedPreferences mState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan);
		setCalorie = (EditText) findViewById(R.id.setCalorie);
		setStep = (EditText) findViewById(R.id.setStep);
		finish = (Button) findViewById(R.id.planfinishButton);
		cancel = (Button) findViewById(R.id.planconcelButton);
		mState = getSharedPreferences("state", 0);
		
		desireCalorie = mState.getString("desire_calorie", "0");
		desireStep = mState.getString("desire_step", "0");
		
		setCalorie.setText(desireCalorie);
		setStep.setText(desireStep);
		
		finish.setOnClickListener(new OnClickListener() { 
        	public void onClick(View v) {  
        		SharedPreferences.Editor editor = mState.edit();
                editor.putString("desire_calorie",setCalorie.getText().toString() );
                editor.putString("desire_step",setStep.getText().toString() );
                editor.commit();
                Intent intent = new Intent();
        		intent.setClass(PlanActivity.this, MainActivity.class); 	
        		startActivity(intent);
        		finish(); 
        	} 
        }); 
		
		cancel.setOnClickListener(new OnClickListener() { 
        	public void onClick(View v) {  
        		Intent intent = new Intent();
        		intent.setClass(PlanActivity.this, MainActivity.class); 	
        		startActivity(intent);
        		finish(); 
        	} 
        }); 
	}
}
