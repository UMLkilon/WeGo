
package com.example.wego;

//import android.support.v7.app.ActionBar;
import com.example.wego.R;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
//import android.os.Build;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        final Button sportButton = (Button)findViewById(R.id.sportButton);
        final Button planButton = (Button)findViewById(R.id.planButton); 
             
        sportButton.setOnClickListener(new OnClickListener () {
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(FirstActivity.this, RunningActivity.class); 	
        		startActivity(intent);
        		finish();    
        	}
        });
        
        planButton.setOnClickListener(new OnClickListener() { 
        	public void onClick(View v) {  
        		Intent intent = new Intent();
        		intent.setClass(FirstActivity.this, PlanActivity.class); 	
        		startActivity(intent);
        		finish(); 
        	} 
        }); 
    }

}
