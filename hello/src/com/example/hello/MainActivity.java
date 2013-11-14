package com.example.hello;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;

import com.example.hello.TetrisView.RefreshHandler;
import com.example.hello.TetrisView.keys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
//import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity {

	/*Source Insight 3内修改成功
	 * 此小程序乃移植于本人写的c# winform程序,有改进，为了方便快速移植变量名基本同名，可参照原版 注释少而精 用eclipse自动排版
	 */
	// private TetrisView mTetrisView;
	// private RefreshHandler m_mRedrawHandler = new RefreshHandler();
	private Button BtnStart;
	private Button BtnRanking;
	private Button BtnSet;
	private Button BtnOK;
	private SQLiteHelper m_SqLiteHelper = new SQLiteHelper(this);
	private SQLiteDatabase m_SQLiteDatabase;
	private TextView TextView_Tips;
	private TextView TextView_Name;
	private TextView TextView_Score;
	private EditText EditText_Name;
	private int m_cxScreen;// 屏幕宽度
	private int m_cyScreen;// 屏幕高度
	private TetrisView m_game;// 定义游戏运行类
	long timeStart, midTime;
	int m_cxStart, m_cyStart;
	private boolean m_flag = false;// replay flag
	private long dealy = 100;// 滑屏时延时发送指定消息 随滑动距离改变
	/**
	 * 退出标记
	 */
	private boolean m_flagExit = false;

	/**
	 * 仅处理滑屏延时
	 * 
	 * @author Administrator
	 * 
	 */
	// class RefreshHandler extends Handler {
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// switch (msg.what) {
	// case 0:
	// m_game.KeyDown(keys.UP);
	// break;
	// case 1:
	// m_game.KeyDown(keys.DOWN);
	// break;
	// case 2:
	// m_game.KeyDown(keys.LEFT);
	// break;
	// case 3:
	// m_game.KeyDown(keys.RIGHT);
	// break;
	// }
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * int[][] uu=new int[10][10]; int[][] yy=new int[10][10]; for(int
		 * i=0;i<10;++i) { for(int j=0;j<10;++j) uu[i][j]=5; } yy=uu.clone();
		 * yy[3][3]=2; Log.i("你好","uu[3][3] = "+uu[3][3]);
		 */
		// Log.i("你好", "MainActivity onCreate");
		Init();
	}

	/**
	 * Init
	 */
	private void Init() {
		setContentView(R.layout.activity_main);
		m_flag = false;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		m_cxScreen = dm.widthPixels;
		m_cyScreen = dm.heightPixels;

		BtnStart = (Button) findViewById(R.id.BtnStart);
		BtnRanking = (Button) findViewById(R.id.BtnRanking);
		BtnSet = (Button) findViewById(R.id.buttonSet);
		BtnOK = (Button) findViewById(R.id.BtnOk);
		BtnOK.setVisibility(View.INVISIBLE);
		TextView_Tips = (TextView) findViewById(R.id.textView_tips_main);
		TextView_Name = (TextView) findViewById(R.id.textView_name__main);
		TextView_Score = (TextView) findViewById(R.id.textView_score);
		EditText_Name = (EditText) findViewById(R.id.editText_main);
		TextView_Name.setVisibility(View.INVISIBLE);
		TextView_Tips.setVisibility(View.INVISIBLE);
		EditText_Name.setVisibility(View.INVISIBLE);
		InitView();
		// BtnRanking.setY(m_cyScreen/10) ;
		// BtnStart.setY(m_cyScreen/5) ;
		m_SQLiteDatabase = m_SqLiteHelper.getWritableDatabase();
		m_SqLiteHelper.Init(m_SQLiteDatabase);
		BtnRanking.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// MainActivity.this.m_game.InitGame(m_cxScreen,m_cyScreen);
				Intent _intent = new Intent(MainActivity.this,
						RankingActivity.class);
				MainActivity.this.startActivity(_intent);
			}
		});
		BtnStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BtnStart.setVisibility(View.INVISIBLE);
				MainActivity.this.BtnRanking.setVisibility(View.INVISIBLE);
				MainActivity.this.BtnSet.setVisibility(View.INVISIBLE);
				m_game.Begin();
			}
		});
		BtnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent _intent = new Intent(MainActivity.this,
						SetActivity.class);
				MainActivity.this.startActivity(_intent);				
			}
		});
		BtnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (BtnOK.getText().equals("确定")) {
					int num = 0;
					long score = 0;
					String strName;
					Date _date = new Date();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String _SqlText = "Select count(*) From ranking";
					Cursor _CursorB = m_SQLiteDatabase.rawQuery(_SqlText, null);
					while (_CursorB.moveToNext()) {
						num = _CursorB.getInt(_CursorB
								.getColumnIndex("count(*)"));
					}
					if (num >= 15) {// 最多保存15条记录
						String _SqlTextA = "Select min(Score) From ranking";
						Cursor _Cursor = m_SQLiteDatabase.rawQuery(_SqlTextA,
								null);
						while (_Cursor.moveToNext()) {
							score = _Cursor.getInt(_Cursor
									.getColumnIndex("min(Score)"));
						}
						m_SQLiteDatabase.delete("ranking", "Score=" + score,
								null);
					}
					ContentValues _ContentValues = new ContentValues();
					if (EditText_Name.getText().toString().equals("")) {
						strName = "anonymity";
					} else {
						strName = EditText_Name.getText().toString();
					}
					_ContentValues.put("UserName", strName);
					_ContentValues.put("Score", m_game.m_currentData.m_score);
					_ContentValues.put("Date", format.format(_date));
					m_SQLiteDatabase.insert("ranking", null, _ContentValues);
					Intent _intent = new Intent(MainActivity.this,
							RankingActivity.class);
					m_flag = true;
					;
					MainActivity.this.startActivity(_intent);
				} else// replay
				{
					Init();
					LoadBkGd();
				}
			}
		});
		m_game.SetMode(TetrisView.state.HOMEPAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// if(m_game.m_old_state == TetrisView.state.GAMEOVER)
		// {
		// Init();
		// LoadBkGd();

		// }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// BtnStart.setText(""+m_game.getWidth());
		// BtnStart.setY(480);
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		if (m_game.m_old_state == TetrisView.state.HOMEPAGE)
			m_game.SetMode(m_game.m_old_state);
		if (m_flag) {
			Init();
		}
		// Log.i("你好","onRestart()");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		LoadBkGd();
		super.onStart();
	}

	private void LoadBkGd() {
		if (DateBaseConfig.GetMode().equals("boy"))
			m_game.setBackgroundResource(R.drawable.boy);
		if (DateBaseConfig.GetMode().equals("girl"))
			m_game.setBackgroundResource(R.drawable.girl);
		if (DateBaseConfig.GetMode().equals("jack"))
			m_game.setBackgroundResource(R.drawable.jack);
		if (DateBaseConfig.GetMode().equals("belle"))
			m_game.setBackgroundResource(R.drawable.belle);
		if (DateBaseConfig.GetMode().equals("wolffy"))
			m_game.setBackgroundResource(R.drawable.wolffy);
		if (DateBaseConfig.GetMode().equals("redwolf"))
			m_game.setBackgroundResource(R.drawable.redwolf);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (m_game.m_state != TetrisView.state.PAUSE) {
			m_game.m_old_state = m_game.m_state;
			if (m_game.m_state == TetrisView.state.RUN)
				m_game.m_currentData.m_pause = !m_game.m_currentData.m_pause;
			m_game.SetMode(TetrisView.state.PAUSE);
		}
		super.onPause();
	}

	private void InitView() {
		m_game = (TetrisView) findViewById(R.id.tetris);
		m_game.SetBtnOK(BtnOK);
		m_game.SetContext(this);
		m_game.SetTextView_Tips(TextView_Tips);
		m_game.SetTextView_Name(TextView_Name);
		m_game.SetEditText_Name(EditText_Name);
		m_game.SetTextView_Score(TextView_Score);
		m_game.InitGame(m_cxScreen, m_cyScreen);
		m_game.m_MainActivity = this;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (Math.abs((int) event.getX() - m_cxStart) >= (TetrisView.BLOCK_WIDE / 2)
					|| Math.abs((int) event.getY() - m_cyStart) >= (TetrisView.BLOCK_HIGHT / 2)) {
				if (System.currentTimeMillis() - timeStart > dealy)// 延时 可触发
				{					
					timeStart = System.currentTimeMillis();
					if (Math.abs((int) event.getX() - m_cxStart) > Math// 左右移动触发
							.abs((int) event.getY() - m_cyStart)) {
						dealy = 200 - Math.abs((int) event.getX() - m_cxStart)
								/ TetrisView.BLOCK_WIDE * 10;
						if ((int) event.getX() > m_cxStart)// 右滑动
						{
							m_game.KeyDown(keys.RIGHT);
						} else {
							m_game.KeyDown(keys.LEFT);
						}
					} else {
						if ((int) event.getY() > m_cyStart)// 下滑
						{
							m_game.KeyDown(keys.DOWN);
						} else {
							m_game.KeyDown(keys.UP);
							dealy = 500;
						}
					}
				}
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			timeStart = System.currentTimeMillis();
			m_cxStart = (int) event.getX();
			m_cyStart = (int) event.getY();
			dealy = 100;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (Math.abs((int) event.getX() - m_cxStart) < (TetrisView.BLOCK_WIDE / 2)
					&& Math.abs((int) event.getY() - m_cyStart) < (TetrisView.BLOCK_HIGHT / 2)) {
				m_game.KeyDown(TraslateToKey((int) event.getX(),
						(int) event.getY()));
			}
		}
		return super.onTouchEvent(event);
	}

	private keys TraslateToKey(int cx, int cy) {
		/* 上方1/3为up，下方1/3为down中间平分left,right.预览区暂停 */
		// Log.i("你好", "当前TetrisView 按下点" + cx + "  " + cy);
		if (cy < TetrisView.BLOCK_HIGHT * 3 && cx > TetrisView.BLOCK_WIDE * 7) {

			return keys.PAUSE;
		}
		if (cy < m_cyScreen / 4) {
			return keys.UP;
		}
		if (cy > m_cyScreen / 4 * 3) {
			return keys.DOWN;
		}
		if (cx < m_cxScreen / 2) {
			return keys.LEFT;
		} else
			return keys.RIGHT;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!m_flagExit) {
				m_flagExit = true;
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2500);
							m_flagExit = false;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// Log.i("你好", "Runnable() Exception");
							e.printStackTrace();
						}
					}
				}).start();
				Toast.makeText(this, "再次按返回键退出程序", 500).show();
				return true;
			} else
				android.os.Process.killProcess(android.os.Process.myPid());
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			m_game.KeyDown(keys.UP);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			m_game.KeyDown(keys.PAUSE);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			m_game.KeyDown(keys.DOWN);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			m_game.KeyDown(keys.LEFT);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			m_game.KeyDown(keys.RIGHT);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// Log.i("你好",String.format("x=%f  y=%f", event.getX(),event.getY()));
		return super.onTrackballEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_settings) {
			Intent _intent = new Intent(MainActivity.this, SetActivity.class);
			MainActivity.this.startActivity(_intent);
		}
		return super.onOptionsItemSelected(item);
	}

}
