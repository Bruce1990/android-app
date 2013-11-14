package com.example.hello;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

public class RankingActivity extends Activity {

	private View m_view;
	private TextView m_textview_ranking_num;
	private TextView m_textview_ranking_name;
	private TextView m_textview_ranking_score;
	private TextView m_textview_ranking_date;
	private SQLiteHelper m_SqLiteHelper = new SQLiteHelper(this);
	private SQLiteDatabase m_SQLiteDatabase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranking);
		m_view=(View)findViewById(R.id.RankingActivity) ;
		m_textview_ranking_num=(TextView)findViewById(R.id.textview_ranking1);
		m_textview_ranking_name=(TextView)findViewById(R.id.textview_ranking2);
		m_textview_ranking_score=(TextView)findViewById(R.id.textview_ranking3);
		m_textview_ranking_date=(TextView)findViewById(R.id.textview_ranking4);
		m_SQLiteDatabase = m_SqLiteHelper.getWritableDatabase();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		int num=0;
		String str;
		LoadBkGd();
		m_textview_ranking_num.setText("");
		m_textview_ranking_name.setText("");
		m_textview_ranking_score.setText("");
		m_textview_ranking_date.setText("");
		String _SqlText = "Select * From ranking order by Score desc";
		Cursor _CursorB = m_SQLiteDatabase.rawQuery(_SqlText, null);
		while (_CursorB.moveToNext()) {
			num++;
			m_textview_ranking_num.setText(m_textview_ranking_num.getText().toString()+num+"\n");
			str=_CursorB.getString(_CursorB.getColumnIndex("UserName"))+"\n";
			m_textview_ranking_name.setText(m_textview_ranking_name.getText().toString()+str);
			str=_CursorB.getInt(_CursorB.getColumnIndex("Score"))+"\n";
			m_textview_ranking_score.setText(m_textview_ranking_score.getText().toString()+str);
			str=_CursorB.getString(_CursorB.getColumnIndex("Date"))+"\n";
			m_textview_ranking_date.setText(m_textview_ranking_date.getText().toString()+str);
		}
		super.onStart();
	}

	private void LoadBkGd()
	{
		if(DateBaseConfig.GetMode().equals("boy"))
			m_view.setBackgroundResource(R.drawable.boy);
		if(DateBaseConfig.GetMode().equals("girl"))
			m_view.setBackgroundResource(R.drawable.girl);
		if(DateBaseConfig.GetMode().equals("jack"))
			m_view.setBackgroundResource(R.drawable.jack);
		if(DateBaseConfig.GetMode().equals("belle"))
			m_view.setBackgroundResource(R.drawable.belle);
		if (DateBaseConfig.GetMode().equals("wolffy"))
			m_view.setBackgroundResource(R.drawable.wolffy);
		if (DateBaseConfig.GetMode().equals("redwolf"))
			m_view.setBackgroundResource(R.drawable.redwolf);
	}
}
