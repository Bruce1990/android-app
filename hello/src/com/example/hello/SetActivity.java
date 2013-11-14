package com.example.hello;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ToggleButton;

public class SetActivity extends Activity {
	private SQLiteHelper m_SqLiteHelper;
	private SQLiteDatabase m_SQLiteDatabase;
	private View m_view;
	private Spinner spinner1;
	private ToggleButton togBtn;
	private ToggleButton togBtn_v;
	private ToggleButton togBtn_e;
	private TextView TextView_e;
	private TextView TextView_url;
	private String[] Backgrounds = {"girl","boy","jack","belle","wolffy","redwolf"} ;
	private ArrayAdapter<String> adapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		m_view=(View)findViewById(R.id.SetActivity);
		m_SqLiteHelper = new SQLiteHelper(this);
		m_SQLiteDatabase = m_SqLiteHelper.getWritableDatabase();
		//m_view=LayoutInflater.from(this).inflate(R.layout.activity_set, null);
		spinner1 = (Spinner)findViewById(R.id.spinner1) ;
		togBtn=(ToggleButton)findViewById(R.id.toggleButton1);
		togBtn_v=(ToggleButton)findViewById(R.id.toggleButton_v);
		togBtn_e=(ToggleButton)findViewById(R.id.toggleButton_e);
		togBtn_e.setVisibility(View.INVISIBLE);
		TextView_e=(TextView)findViewById(R.id.textView2);
		TextView_e.setVisibility(View.INVISIBLE);
		TextView_url=(TextView)findViewById(R.id.textView_url);
		//创建一个 SpannableString对象  
        SpannableString sp = new SpannableString("获取源码");   
        //设置超链接  
        sp.setSpan(new URLSpan("http://blog.csdn.net/yycaogg/article/details/12177265"), 0, 4,   
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
        //设置高亮样式一  
        /*sp.setSpan(new BackgroundColorSpan(Color.RED), 17 ,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);   
        //设置高亮样式二  
        sp.setSpan(new ForegroundColorSpan(Color.YELLOW),20,24,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     
        //设置斜体  
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 27, 29, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);   
        //SpannableString对象设置给TextView  */
        TextView_url.setText(sp);   
        //设置TextView可点击  
        TextView_url.setMovementMethod(LinkMovementMethod.getInstance());   
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Backgrounds)  ;
		spinner1.setAdapter(adapter) ;		
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(SetActivity.this, "你选择的背景是："+Backgrounds[arg2], Toast.LENGTH_SHORT).show() ;
				ContentValues _ContentValues = new ContentValues();
				_ContentValues.put("mode",Backgrounds[arg2]);
				m_SQLiteDatabase.update("inf", _ContentValues, null, null);
				DateBaseConfig.SetMode(Backgrounds[arg2]);
				LoadBkGd();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}) ;
		if(DateBaseConfig.GetSwitch().equals("on"))
		{
			togBtn.setChecked(true);
		}
		else
		{
			togBtn.setChecked(false);
		}
		if(DateBaseConfig.GetSwitch_v().equals("on"))
		{
			togBtn_v.setChecked(true);
		}
		else
		{
			togBtn_v.setChecked(false);
		}
		if(DateBaseConfig.GetSwitch_e().equals("on"))
		{
			togBtn_e.setChecked(true);
		}
		else
		{
			togBtn_e.setChecked(false);
		}
		togBtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				ContentValues _ContentValues = new ContentValues();				
				if(arg1)
				{
					_ContentValues.put("switch", "on");
					DateBaseConfig.SetSwitch("on");
				}
				else
				{
					_ContentValues.put("switch", "off");
					//Log.i("你好","ToggleButton false") ;
					DateBaseConfig.SetSwitch("off");
				}
				m_SQLiteDatabase.update("inf", _ContentValues, null, null);
			}
			
		});
		
		togBtn_v.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				ContentValues _ContentValues = new ContentValues();				
				if(arg1)
				{
					_ContentValues.put("switch_v", "on");
					DateBaseConfig.SetSwitch_v("on");
				}
				else
				{
					_ContentValues.put("switch_v", "off");
					//Log.i("你好","ToggleButton false") ;
					DateBaseConfig.SetSwitch_v("off");
				}
				m_SQLiteDatabase.update("inf", _ContentValues, null, null);
			}
			
		});
		
		togBtn_e.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				ContentValues _ContentValues = new ContentValues();				
				if(arg1)
				{
					_ContentValues.put("switch_e", "on");
					DateBaseConfig.SetSwitch_e("on");
				}
				else
				{
					_ContentValues.put("switch_e", "off");
					//Log.i("你好","ToggleButton false") ;
					DateBaseConfig.SetSwitch_e("off");
				}
				m_SQLiteDatabase.update("inf", _ContentValues, null, null);
			}
			
		});
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		LoadBkGd();
		
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
