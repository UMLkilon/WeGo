package com.database;

import java.text.SimpleDateFormat;

public class Name {
	public Name(){}
	public static final int CASE_CAMERA = 12345;
	public final static String TIME_FORMAT = "yyyy��MM��dd��   HH:mm:ss     ";
	public final static String	TABLE_NAME = "tableRunInfo";
	public final static String	DATABASE_NAME = "jasb.db";
	public final static SimpleDateFormat  formatter   =   new   SimpleDateFormat(TIME_FORMAT);
	
	/* ���е��ֶ� */
	public final static String	TABLE_ID		= "_id";
	public final static String	Date		    = "date";
	public final static String	Time		    = "time";
	public final static String	Distance		= "distance";
	public final static String	Calorie		   	= "calorie";
	public final static String	Speed			= "speed";
	public final static String	Steps			= "steps";
	
	/* �������sql��� */
	public final static  String	CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" (_id INTEGER PRIMARY KEY, " +
			" date TEXT,time TEXT,distance REAL,calorie REAL,speed REAL,steps INTEGER)";
	

}
