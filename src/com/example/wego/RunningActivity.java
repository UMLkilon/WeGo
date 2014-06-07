package com.example.wego;


//import android.content.Intent;
import com.database.Name;
import com.example.wego.R;
import com.example.wego.R.string;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class RunningActivity extends Activity {
	private static final String TAG = "WeGo";
    private SharedPreferences mSettings;
    private SharedPreferences mState;
    private PedometerSettings mPedometerSettings;
    private TextView mTimeValueView;
    private TextView mStepValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    private int mStepValue;//mStepValueView的值
    private float mDistanceValue;//mDistanceValueView的值
    private float mSpeedValue;//mSpeedValueView的值
    private int mCaloriesValue;//mCaloriesValueView的值
    private float mDesiredPaceOrSpeed;//
    private int mMaintain;//是否是爬山
    private boolean mIsMetric;////公制和米制切换标志
    private float mMaintainInc;//
    private boolean mQuitting = false; //
    boolean pauseButtonState = false;
    private int recLen = 0; //计算时间
    private String time;
    private SQLiteDatabase mSQLiteDatabase = null;
    // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy

    public Context getContext()
    {
    	return this;
    }
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;//程序是否运行的标志位
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[ACTIVITY] onCreate");
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.running);
    }
    
    //开始函数，重写该函数，加入日志。
    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
        if(mService != null)
        	resetValues(true);
        
        /*
         * ”完成“按钮的点击事件
         */
        Button finishButton = (Button) findViewById(R.id.finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mService.saveData();
            	mState = getSharedPreferences("state", 0);
            	SharedPreferences.Editor editor = mState.edit();
                editor.putString("runing_time",time);
                editor.commit();
            	
            	if(mIsRunning) {
            		unbindStepService();  
            		stopStepService();
            	}
            	
            	/*
            	 * 保存数据到数据库中
            	 */
            	mSQLiteDatabase = openOrCreateDatabase(Name.DATABASE_NAME,MODE_PRIVATE,null); 
            	ContentValues cv = new ContentValues();
            	cv.put(Name.Date,mState.getString("startDate", "0"));
            	cv.put(Name.Time,time);
        		cv.put(Name.Distance, mState.getFloat("distance", 0.0f));	
        		cv.put(Name.Calorie,  mState.getFloat("calories", 0.0f));
        		cv.put(Name.Speed,    mState.getFloat("speed", 0.0f));
        		cv.put(Name.Steps,    mState.getInt("steps", 0));
        		mSQLiteDatabase.insert(Name.TABLE_NAME,null,cv);
        		mSQLiteDatabase.close();
        		Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
            	
        		/*
        		 * 跳转页面
        		 */
                mQuitting = true;       
                Intent intent = new Intent();
        		intent.setClass(RunningActivity.this, RunFinishActivity.class); 	
        		startActivity(intent);
                finish();
            }
        });
        
        /*
         * “暂停”按钮的点击事件
         */
        final Button pauseButton = (Button) findViewById(R.id.pause);
        final Button continueButton = (Button) findViewById(R.id.restart);
        OnClickListener pclick = new OnClickListener(){
      	   public void onClick(View v) {
     		   if(!pauseButtonState) {
     			   unbindStepService();    	
     			   stopStepService();		   
     			   //onPause();
     			   pauseButtonState = true;
     			   continueButton.setEnabled(true);
     			   pauseButton.setEnabled(false);
     			   pauseButton.setVisibility(View.GONE);
     			   continueButton.setVisibility(View.VISIBLE);
     			   Log.i(TAG,"[pause click]");
     		   }
     		   else {
     			   	//onResume();
     			    startStepService();
                    bindStepService();
                    pauseButtonState = false;
                    continueButton.setEnabled(false);
      			    pauseButton.setEnabled(true);
      			    pauseButton.setVisibility(View.VISIBLE);
    			    continueButton.setVisibility(View.GONE);
     		   }
     			   
     	   }
         }; 
        pauseButton.setOnClickListener(pclick);
        continueButton.setOnClickListener(pclick);
    }

    ////重写回复函数
    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();
        
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        
        
        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();
        
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }
        
        mPedometerSettings.clearServiceRunning();
        
        mTimeValueView     = (TextView) findViewById(R.id.timeTextView);
        mStepValueView     = (TextView) findViewById(R.id.runningStepText);
        mDistanceValueView = (TextView) findViewById(R.id.distanceText);
        mSpeedValueView    = (TextView) findViewById(R.id.averageSpeed);
        mCaloriesValueView = (TextView) findViewById(R.id.calorieRunningText);
        mIsMetric = mPedometerSettings.isMetric();     
        mMaintain = mPedometerSettings.getMaintainOption();
        if (mMaintain == PedometerSettings.M_PACE) {
            mMaintainInc = 5f;
            mDesiredPaceOrSpeed = (float)mPedometerSettings.getDesiredPace();
        }
        else 
        if (mMaintain == PedometerSettings.M_SPEED) {
            mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
            mMaintainInc = 0.1f;
        }
    }
    
    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }

        super.onPause();
        savePaceSetting();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }
    
    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onRestart();
    }

    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();  
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(RunningActivity.this,
                    StepService.class));
            handler.post(updateThread);  //调用Handler的post方法，将要执行的线程对象添加到队列当中
        }
        
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(RunningActivity.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        if(mConnection != null)
        	unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(RunningActivity.this,
                  StepService.class));
            handler.removeCallbacks(updateThread); 
        }
        mIsRunning = false;       
    }
    
  
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            mTimeValueView.setText(R.string.zeroTime);
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }
    
    //       
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
   
    /*
     * 创建一个Handler对象 
     */
    Handler handler = new Handler(); 
    /*
     * 将要执行的操作写在线程对象的run方法当中 
     */
    Runnable updateThread = new Runnable() {    
	     public void run() { 
	
		      recLen++;      		      
		      if((int)(recLen/60) <10) {
		    	  if(recLen%60 < 10)
		    		  time = "0" + String.valueOf((int)(recLen/60))+":0"+String.valueOf(recLen%60);
		    	  else
		    		  time = "0" + String.valueOf((int)(recLen/60))+":"+String.valueOf(recLen%60);
		      }
		      else {
		    	  if(recLen%60 < 10)
		    		  time = String.valueOf((int)(recLen/60))+":0"+String.valueOf(recLen%60);
		    	  else
		    		  time = String.valueOf((int)(recLen/60))+":"+String.valueOf(recLen%60);
		      }
		      /*
		       * 在run方法内部post或postDelayed方法 
		       */
		      mTimeValueView.setText((CharSequence)time);
		      handler.postDelayed(updateThread, 1000); 
	     }
    };
}
