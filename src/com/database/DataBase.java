package com.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBase 
{
	public DataBase(){}
	public static boolean tableIsExist(SQLiteDatabase mSQLiteDatabase ,String tableName)
	{
		boolean result = false;
		String sql = "SELECT COUNT(*) FROM Sqlite_master WHERE TYPE = 'table' AND name = '" + tableName.trim() + "'";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if(cursor.moveToNext()){
			int count = cursor.getInt(0);
			if(count > 0)
				result = true;
		}
		return result;
	}

}
