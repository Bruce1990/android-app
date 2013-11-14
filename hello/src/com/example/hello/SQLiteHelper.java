package com.example.hello;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
		public SQLiteHelper(Context context) 
		{
			super(context, DateBaseConfig.GetDataBaseName(), null, DateBaseConfig.GetVersion());
		}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public void Init(SQLiteDatabase arg0)
	{
		String _SqlText ="Select * From sqlite_master Where type = 'table' AND name='ranking'" ;
		Cursor _Cursor =arg0.rawQuery(_SqlText, null) ;
		//Log.i("你好","有表");	
		if(_Cursor.getCount()==0)
		{
			//Log.i("你好","没ranking表");	
			_SqlText="Create  TABLE ranking([UserName] varchar(10) NOT NULL,[Score] integer NOT NULL" +
					",[Date] varchar(50) NOT NULL)" ;
			arg0.execSQL(_SqlText);
			//Date _date=new Date();
			ContentValues _ContentValues = new ContentValues();
			_ContentValues.put("UserName","阿拉丁");
			_ContentValues.put("Score",1);
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			_ContentValues.put("Date","2013年10月1日");
			arg0.insert("ranking", null, _ContentValues);
		}
		_SqlText ="Select * From sqlite_master Where type = 'table' AND name='inf'" ;
		Cursor _CursorA =arg0.rawQuery(_SqlText, null) ;
		if(_CursorA.getCount()==0)
		{
			//Log.i("你好","没inf表");
			//Date _date=new Date();
			//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			 
			//Log.i("你好","没inf表"+format.format(_date));
			_SqlText="Create  TABLE inf([mode] varchar(10) NOT NULL,[switch] varchar(10) NOT NULL" +
					",[switch_v] varchar(10) NOT NULL,[switch_e] varchar(10) NOT NULL)";
			arg0.execSQL(_SqlText);
			ContentValues _ContentValues = new ContentValues();
			_ContentValues.put("mode",DateBaseConfig.GetDefMode());
			_ContentValues.put("switch",DateBaseConfig.GetDefSwitch());
			_ContentValues.put("switch_v",DateBaseConfig.GetDefSwitch_v());
			_ContentValues.put("switch_e",DateBaseConfig.GetDefSwitch_e());
			arg0.insert("inf", null, _ContentValues);			
		}
		else
		{
			_SqlText ="Select * From inf" ;
			Cursor _CursorB =arg0.rawQuery(_SqlText, null) ;
			//Log.i("你好","有inf表");
			while(_CursorB.moveToNext())
			{			
				DateBaseConfig.SetMode(_CursorB.getString(_CursorB.getColumnIndex("mode")));
				DateBaseConfig.SetSwitch(_CursorB.getString(_CursorB.getColumnIndex("switch")));
				DateBaseConfig.SetSwitch_v(_CursorB.getString(_CursorB.getColumnIndex("switch_v")));
				DateBaseConfig.SetSwitch_e(_CursorB.getString(_CursorB.getColumnIndex("switch_e")));
			}
		}
	}

}
