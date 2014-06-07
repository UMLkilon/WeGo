package com.example.wego;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

public class MainActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		final TabHost thost = getTabHost();
		final TabWidget tabWidget = thost.getTabWidget();
		thost.addTab(thost.newTabSpec("sportTab").
				setIndicator("跑步").
				setContent(new Intent(this,FirstActivity.class)
				));
		thost.addTab(thost.newTabSpec("historyTab").
				setIndicator("历史").
				setContent(new Intent(this,RunRecordActivity.class)
				));
		
		
		for (int i =0; i < tabWidget.getChildCount(); i++) {
		      View vvv = tabWidget.getChildAt(i);
		      if(thost.getCurrentTab()==i){
		              vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_front_bg));//点中
		      }
		      else{
		    	      vvv.setBackgroundColor(Color.rgb(255, 255, 255));		            
		      }
	    }
		
		thost.setOnTabChangedListener(new OnTabChangeListener(){
				    @Override
				    public void onTabChanged(String tabId) {
				     // TODO Auto-generated method stub
				    for (int i =0; i < tabWidget.getChildCount(); i++) {
				    	View vvv = tabWidget.getChildAt(i);
				    	if(thost.getCurrentTab()==i){
				              vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_front_bg));//点中
				    	}
				    	else {
				    	      vvv.setBackgroundColor(Color.rgb(255, 255, 255));				             
				    	}
				    }
		}});   
		
		
					
	}

}