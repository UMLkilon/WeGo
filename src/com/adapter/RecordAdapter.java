package com.adapter;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.wego.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordAdapter extends ArrayAdapter<Object>{
	
	public static final int TYPE_SHOWRUNRECORD = 0;	
	
	private LayoutInflater mInflater;
    private ViewHolder holder = null;
    private int type;
 	private List<Map<String, Object>> runRecordList;
 	private Context context;
 	
	public RecordAdapter(int type, Context context,List<Map<String,Object>> runRecordList){
	    super(context, 0);
		this.type = type;
		this.context = context;
		this.runRecordList = runRecordList;
		this.mInflater = LayoutInflater.from(context);
	}
	public List<Map<String, Object>> getList(){
        return runRecordList;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return runRecordList.size();
	}	
	
	@Override
	public void remove(Object o) {
		// TODO Auto-generated method stub
		runRecordList.remove(o);
		
	}	
	
	@Override
	public void insert(Object o, int position) {
		// TODO Auto-generated method stub
		runRecordList.add(position, (Map<String, Object>) o);
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		//return super.getItem(position);
		return runRecordList.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
    	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View result = null;
		if(type == TYPE_SHOWRUNRECORD)
		{
			result = getViewShow(position, convertView, parent);
		}
		
		return result;
	}
	
	public View getViewShow(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	
        if (convertView == null) {
        	holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.run_record_item, null);
            holder.dateTv = (TextView)convertView.findViewById(R.id.date_tv);
            holder.timeTv = (TextView)convertView.findViewById(R.id.time_tv);
            holder.distanceTv = (TextView) convertView.findViewById(R.id.distance_tv);
            holder.calorieTv = (TextView) convertView.findViewById(R.id.calorie_tv);
            holder.speedTv = (TextView) convertView.findViewById(R.id.speed_tv);
            holder.stepsTv = (TextView) convertView.findViewById(R.id.steps_tv);
            convertView.setTag(holder);
        }
        else {
        	holder = (ViewHolder)convertView.getTag();    
        }     
        holder.dateTv.setText((String)runRecordList.get(position).get("date"));        
        holder.timeTv.setText("用时\n" + runRecordList.get(position).get("time")); 
        holder.distanceTv.setText("路程\n" + runRecordList.get(position).get("distance")); 
        holder.calorieTv.setText("燃烧\n" + runRecordList.get(position).get("calorie")); 
        holder.speedTv.setText("均速\n" + runRecordList.get(position).get("speed")); 
        holder.stepsTv.setText("步数\n" + runRecordList.get(position).get("steps")); 
     
        return convertView;        
	}     
	
}
