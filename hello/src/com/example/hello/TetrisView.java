package com.example.hello;

import java.util.Random;

//import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
//import android.util.Log;
//import android.util.Log;

//import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author Administrator
 * 
 */
public class TetrisView extends SurfaceView {
	/**
	 * 确定按钮
	 */
	private Button m_BtnOK;
	/**
	 * TextView_Tips
	 */
	private TextView m_TextView_Tips;
	/**
	 * TextView_Name
	 */
	private TextView m_TextView_Name;
	/**
	 * EditText_Name
	 */
	private EditText m_EditText_Name;
	/**
	 * TextView_Score;
	 */
	private TextView m_TextView_Score;
	public MainActivity m_MainActivity;
	/**
	 * effect
	 */
	private int[][] m_effect;
	/**
	 * handler
	 */
	private RefreshHandler m_mRedrawHandler = new RefreshHandler();
	/**
	 * MainActivity Context
	 */
	private Context m_context;

	/**
	 * 当前模式
	 * 
	 * @author Administrator
	 * 
	 */
	public enum state {
		NULL, HOMEPAGE, RUN, PAUSE, GAMEOVER
	}

	public enum keys {
		LEFT, RIGHT, UP, DOWN, PAUSE// 在右上角 单击预览块既暂停
	}

	/**
	 * 声音种类
	 * 
	 * @author Administrator
	 * 
	 */
	public enum sound {
		BANG, GUNFIRE, PAUSE, PEOW
	}

	public class DataTetris {
		/**
		 * 种类标志
		 */
		public int m_flag;
		/**
		 * 中心点or旋转点如果点中数小余0则不可旋转
		 */
		public int[] m_gyrate;
		/**
		 * 数据
		 */
		public int[][] m_data;
		/**
		 * 出现频率和范围起始点结合使用--以0~99为区间，占比；如50，前块40，则出现频率（50-40）/100
		 */
		public int m_frequency;
	}

	/**
	 * 当前运行数据
	 * 
	 * @author Administrator
	 * 
	 */
	class CurrentData {
		/**
		 * 种类标志
		 */
		public int m_flag;
		/**
		 * 中心点or旋转点如果点中数小余0则不可旋转
		 */
		public int[] m_gyrate;
		/**
		 * 当前块下落最低点
		 */
		// public int m_curLine = 20;
		/**
		 * 数据
		 */
		public int[][] m_data;
		/**
		 * 当前分数
		 */
		public long m_score = 0;
		/**
		 * 基础分数，单次所得分是此数2^n。基础分数随难度增加而增加
		 */
		public int m_baseScore = 10;
		/**
		 * 是否本次块掉底，准备下一个块
		 */
		public boolean m_accept = false;
		/**
		 * 时钟延迟
		 */
		public int m_interval = 500;
		/**
		 * 暂停=false
		 */
		public boolean m_pause = false;
		/**
		 * 发射块cx =-1表示可发射
		 */
		public int m_cx = -1;
		/**
		 * 发射块 cy
		 */
		public int m_cy;
		/**
		 * 爆炸效果过程 =-1未有爆炸
		 */
		public int m_expold = -1;
		/**
		 * 计算分数过程 =-1未消减块
		 */
		public int m_calcScore = -1;
		/**
		 * 计算分数过程 暂存当前要消减行 =-1未有消减行
		 */
		public int m_calcScoreA = -1;
		/**
		 * 消减的总行数
		 */
		public int m_num = 0;
		/**
		 * 暂存爆炸扩展点
		 */
		public int[] m_point_Bomb = new int[6];
		/**
		 * 暂存爆炸扩展点A
		 */
		public int[] m_point_Bomb_A = new int[6];
		/**
		 * 暂存SHAPE1及消隐状态 闪动
		 */
		public boolean m_SHAPE1_Flag = true;
		/**
		 * 暂存SHAPE1cx
		 */
		public int m_SHAPE1_cx = 0;
		/**
		 * 保存要消隐块
		 */
		public int m_X[][];
		/**
		 * 预览区闪动标志
		 */
		public boolean m_previewFlag = false;
		/**
		 * 炸弹爆炸标志
		 */
		public boolean m_bombFlag = false;
		/**
		 * m_shape1Flag
		 */
		public boolean m_shape1Flag = false;
		/**
		 * m_riseBaseScore
		 */
		public long m_riseBaseScore = 0;
		/**
		 * m_riseFlag
		 */
		public boolean m_riseFlag = false;
	}

	/**
	 * 块代号
	 * 
	 * @author Administrator
	 * 
	 */
	public enum block {
		/**
		 * 长条
		 */
		STRIP,
		/**
		 * T状
		 */
		TSHAPE,
		/**
		 * L状
		 */
		LSHAPE,
		/**
		 * L状2
		 */
		LSHAPE2,
		/**
		 * Z
		 */
		ZSHAPE,
		/**
		 * Z2
		 */
		ZSHAPE2,
		/**
		 * 口
		 */
		OSHAPE,
		/**
		 * 一点 可以穿透的特殊
		 */
		SHAPE1,
		/**
		 * 2点 可以发射消减子弹
		 */
		SHAPE2,
		/**
		 * 3点 可以发射增加子弹
		 */
		SHAPE3,
		/**
		 * 炸弹
		 */
		BOMB,
		/**
		 * 消隐长条
		 */
		STRIPX,
		/**
		 * 消隐T状
		 */
		TSHAPEX,
		/**
		 * 消隐L状
		 */
		LSHAPEX,
		/**
		 * 消隐L状2
		 */
		LSHAPE2X,
		/**
		 * 消隐Z
		 */
		ZSHAPEX,
		/**
		 * 消隐Z2
		 */
		ZSHAPE2X,
		/**
		 * 消隐口
		 */
		OSHAPEX,
		/**
		 * 缓冲 用于显示下一个
		 */
		BUFFER
	}

	/**
	 * 首页数据
	 * 
	 * @author Administrator
	 * 
	 */
	public class HomePageData {
		/**
		 * 单帧延迟 50fps
		 */
		public static final int DELAY_NUM = 20;
		/**
		 * 单帧speed
		 */
		public int m_speed = 0;
		/**
		 * 最左标志块cx号
		 */
		public int m_cx_flag = 0;
		/**
		 * 最左标志块cy号
		 */
		public int m_cy_flag = 0;
		/**
		 * 缩放后的块宽
		 */
		public int m_wide = 0;
		/**
		 * 缩放后的块高
		 */
		public int m_hight = 0;
		/**
		 * 最左标志块cx pixel
		 */
		public int m_cx = 0;
		/**
		 * 最左标志块cy pixel
		 */
		public int m_cy = 0;
		/**
		 * 首次标志用于初始化
		 */
		public long m_flag = 0;
		/**
		 * 暂存爆炸点
		 */
		public int[] m_point_Bomb = new int[6];
		/**
		 * 块数据
		 */
		public DataTetris m_dataTetris;
	}

	/**
	 * HomePageData集合
	 */
	private HomePageData[] m_homePageData;
	/**
	 * block集合
	 */
	private DataTetris[] m_block;
	/**
	 * 声音处理类
	 */
	public MediaPlayer[] m_mediaPlayer;
	// ///一些常量定义start
	/**
	 * 单个块宽
	 */
	static int BLOCK_WIDE = 0;
	/**
	 * 单个块高
	 */
	static int BLOCK_HIGHT = 0;
	/**
	 * cx块个数=10
	 */
	private final static int NUM_CX = 10;
	/**
	 * cy块个数=20
	 */
	private final static int NUM_CY = 20;
	/**
	 * cy块个数含隐藏顶部4个=24
	 */
	private final static int NUM_CY_EX = 24;
	/**
	 * 睡眠时间A=100
	 */
	private final static int NUM_SLEEPA = 100;
	/**
	 * 睡眠时间B=15
	 */
	private final static int NUM_SLEEPB = 15;
	/**
	 * 总块数
	 */
	private final static int NUM_BLOCK = 18;
	// ///一些常量定义end
	/**
	 * 加载块共12个
	 */
	private Bitmap[] m_bitMapBlock;
	/**
	 * 客户区宽度
	 */
	private int m_cxClient;
	/**
	 * 客户区高度
	 */
	private int m_cyClient;
	/**
	 * 锁住按键
	 */
	private boolean m_lockKey = false;
	/**
	 * 当前模式
	 */
	public state m_state = state.NULL;
	/**
	 * 暂存
	 */
	public state m_old_state = state.NULL;
	/**
	 * 是否有背景
	 */
	// private boolean m_boolBk = false;
	/**
	 * 方格24*10
	 */
	private int[][] m_unit;
	/**
	 * 窗体右边的预览方格4*4
	 */
	private int[][] m_unitPreview;
	/**
	 * 当前状态数据
	 */
	CurrentData m_currentData;
	/**
	 * 随机数类
	 */
	private Random m_random = new Random();

	/**
	 * 屏幕缓冲
	 */
	// private Bitmap m_SCBitmap;
	/**
	 * 方向按键，由触摸翻译
	 */
	// private keys m_key;

	// ///////////////////////////////////////////////////////////////////
	public TetrisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// BtnStart=(Button)findViewById(R.id.BtnStart);
	}

	public TetrisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// BtnStart=(Button)findViewById(R.id.BtnStart);
		// BtnStart.setY(100);
	}

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (m_state == state.PAUSE)
				return;
			switch (msg.what) {
			case 0:
				TimeTick();
				break;
			case 1:
				TimeTick1();
				break;
			case 2:
				TimeTick2();
				break;
			case 3:
				TimeTick3();
				break;
			case 4:
				TimeTick4();
				break;
			case 5:
				Rise();
				break;
			}
		}
	}

	/**
	 * 初始化
	 * 
	 * @param scrX
	 *            屏幕宽
	 * @param scrY
	 *            屏幕高
	 */
	public void InitGame(int scrX, int scrY) {
		m_cxClient = scrX;
		m_cyClient = scrY;
		BLOCK_WIDE = scrX / 10;
		BLOCK_HIGHT = scrY / 20;
		// m_mRedrawHandler.sendEmptyMessage(0);
		m_mediaPlayer = new MediaPlayer[4];
		m_bitMapBlock = new Bitmap[12];
		m_unitPreview = new int[4][4];
		m_random.setSeed(System.currentTimeMillis());
		m_unit = new int[NUM_CY_EX][NUM_CX];
		m_block = new DataTetris[NUM_BLOCK + 1];// 块18个+1缓冲
		m_effect = new int[25][2];// 最多保存25个特效块坐标 cx高位携带块号 cy<0未保存块 cy高位携带透明度
		for (int i = 0; i < 25; ++i) {
			m_effect[i][1] = -1;
		}
		// Log.i("你好","m_effect.Length() = "+m_effect.length) ;
		m_currentData = new CurrentData();
		m_currentData.m_gyrate = new int[2];
		m_currentData.m_data = new int[4][2];
		m_currentData.m_X = new int[4][2];
		for (int i = 0; i < 4; ++i) {
			m_currentData.m_X[i][0] = -1;
		}
		m_currentData.m_point_Bomb[4] = -1;
		m_homePageData = new HomePageData[10];
		// m_SCBitmap = Bitmap.createBitmap(m_cxClient, m_cyClient,
		// Config.ARGB_8888);
		Resources r = this.getContext().getResources();
		// int[][] ii = new int[][] {};
		loadTile(0, r.getDrawable(R.drawable.a));
		loadTile(1, r.getDrawable(R.drawable.b));
		loadTile(2, r.getDrawable(R.drawable.c));
		loadTile(3, r.getDrawable(R.drawable.d));
		loadTile(4, r.getDrawable(R.drawable.e));
		loadTile(5, r.getDrawable(R.drawable.f));
		loadTile(6, r.getDrawable(R.drawable.g));
		loadTile(7, r.getDrawable(R.drawable.h));
		loadTile(8, r.getDrawable(R.drawable.i));
		loadTile(9, r.getDrawable(R.drawable.j));
		loadTile(10, r.getDrawable(R.drawable.k));
		loadTile(11, r.getDrawable(R.drawable.l));
		m_mediaPlayer[sound.PAUSE.ordinal()] = MediaPlayer.create(m_context,
				R.raw.pause);
		m_mediaPlayer[sound.BANG.ordinal()] = MediaPlayer.create(m_context,
				R.raw.bang);
		m_mediaPlayer[sound.GUNFIRE.ordinal()] = MediaPlayer.create(m_context,
				R.raw.gunfire);
		m_mediaPlayer[sound.PEOW.ordinal()] = MediaPlayer.create(m_context,
				R.raw.peow);
		for (int i = 0; i < NUM_BLOCK + 1; i++) {
			if (i < 10) {
				m_homePageData[i] = new HomePageData();
				m_homePageData[i].m_dataTetris = new DataTetris();
				m_homePageData[i].m_dataTetris.m_data = new int[4][2];
				m_homePageData[i].m_dataTetris.m_gyrate = new int[2];
				m_homePageData[i].m_dataTetris.m_flag = i;
			}
			m_block[i] = new DataTetris();
			m_block[i].m_data = new int[4][2];
			m_block[i].m_gyrate = new int[2];
			m_block[i].m_flag = i;
		}
		m_block[block.STRIP.ordinal()].m_frequency = 12;// 出现频率13%
		m_block[block.TSHAPE.ordinal()].m_frequency = 22;// 出现频率(22-12)%
		m_block[block.LSHAPE.ordinal()].m_frequency = 31;
		m_block[block.LSHAPE2.ordinal()].m_frequency = 40;
		m_block[block.ZSHAPE.ordinal()].m_frequency = 49;
		m_block[block.ZSHAPE2.ordinal()].m_frequency = 58;
		m_block[block.OSHAPE.ordinal()].m_frequency = 67;
		m_block[block.SHAPE1.ordinal()].m_frequency = 70;
		m_block[block.SHAPE2.ordinal()].m_frequency = 73;
		m_block[block.SHAPE3.ordinal()].m_frequency = 76;
		m_block[block.BOMB.ordinal()].m_frequency = 79;
		m_block[block.STRIPX.ordinal()].m_frequency = 82;
		m_block[block.TSHAPEX.ordinal()].m_frequency = 85;
		m_block[block.LSHAPEX.ordinal()].m_frequency = 88;
		m_block[block.LSHAPE2X.ordinal()].m_frequency = 91;
		m_block[block.ZSHAPEX.ordinal()].m_frequency = 94;
		m_block[block.ZSHAPE2X.ordinal()].m_frequency = 97;
		m_block[block.OSHAPEX.ordinal()].m_frequency = 100;
		m_block[block.STRIP.ordinal()].m_gyrate[0] = 4;// x坐标
		m_block[block.STRIP.ordinal()].m_gyrate[1] = 22;// y坐标
		// 数据区，8步，最多4个点（炸弹特殊处理),[0,0]=4,[0,1]=20构成第一个点【20，4】
		m_block[block.STRIP.ordinal()].m_data[0][0] = 4;
		m_block[block.STRIP.ordinal()].m_data[0][1] = 20;
		m_block[block.STRIP.ordinal()].m_data[1][0] = 4;
		m_block[block.STRIP.ordinal()].m_data[1][1] = 21;
		m_block[block.STRIP.ordinal()].m_data[2][0] = 4;
		m_block[block.STRIP.ordinal()].m_data[2][1] = 22;
		m_block[block.STRIP.ordinal()].m_data[3][0] = 4;
		m_block[block.STRIP.ordinal()].m_data[3][1] = 23;
		// hello!
		m_block[block.TSHAPE.ordinal()].m_gyrate[0] = 4;
		m_block[block.TSHAPE.ordinal()].m_gyrate[1] = 21;
		m_block[block.TSHAPE.ordinal()].m_data[0][0] = 3;
		m_block[block.TSHAPE.ordinal()].m_data[0][1] = 21;
		m_block[block.TSHAPE.ordinal()].m_data[1][0] = 4;
		m_block[block.TSHAPE.ordinal()].m_data[1][1] = 21;
		m_block[block.TSHAPE.ordinal()].m_data[2][0] = 5;
		m_block[block.TSHAPE.ordinal()].m_data[2][1] = 21;
		m_block[block.TSHAPE.ordinal()].m_data[3][0] = 4;
		m_block[block.TSHAPE.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.LSHAPE.ordinal()].m_gyrate[0] = 4;
		m_block[block.LSHAPE.ordinal()].m_gyrate[1] = 21;
		m_block[block.LSHAPE.ordinal()].m_data[0][0] = 4;
		m_block[block.LSHAPE.ordinal()].m_data[0][1] = 20;
		m_block[block.LSHAPE.ordinal()].m_data[1][0] = 5;
		m_block[block.LSHAPE.ordinal()].m_data[1][1] = 20;
		m_block[block.LSHAPE.ordinal()].m_data[2][0] = 4;
		m_block[block.LSHAPE.ordinal()].m_data[2][1] = 21;
		m_block[block.LSHAPE.ordinal()].m_data[3][0] = 4;
		m_block[block.LSHAPE.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.LSHAPE2.ordinal()].m_gyrate[0] = 4;
		m_block[block.LSHAPE2.ordinal()].m_gyrate[1] = 21;
		m_block[block.LSHAPE2.ordinal()].m_data[0][0] = 4;
		m_block[block.LSHAPE2.ordinal()].m_data[0][1] = 20;
		m_block[block.LSHAPE2.ordinal()].m_data[1][0] = 5;
		m_block[block.LSHAPE2.ordinal()].m_data[1][1] = 20;
		m_block[block.LSHAPE2.ordinal()].m_data[2][0] = 5;
		m_block[block.LSHAPE2.ordinal()].m_data[2][1] = 21;
		m_block[block.LSHAPE2.ordinal()].m_data[3][0] = 5;
		m_block[block.LSHAPE2.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.ZSHAPE.ordinal()].m_gyrate[0] = 4;
		m_block[block.ZSHAPE.ordinal()].m_gyrate[1] = 21;
		m_block[block.ZSHAPE.ordinal()].m_data[0][0] = 3;
		m_block[block.ZSHAPE.ordinal()].m_data[0][1] = 21;
		m_block[block.ZSHAPE.ordinal()].m_data[1][0] = 4;
		m_block[block.ZSHAPE.ordinal()].m_data[1][1] = 21;
		m_block[block.ZSHAPE.ordinal()].m_data[2][0] = 4;
		m_block[block.ZSHAPE.ordinal()].m_data[2][1] = 22;
		m_block[block.ZSHAPE.ordinal()].m_data[3][0] = 5;
		m_block[block.ZSHAPE.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.ZSHAPE2.ordinal()].m_gyrate[0] = 4;
		m_block[block.ZSHAPE2.ordinal()].m_gyrate[1] = 21;
		m_block[block.ZSHAPE2.ordinal()].m_data[0][0] = 3;
		m_block[block.ZSHAPE2.ordinal()].m_data[0][1] = 22;
		m_block[block.ZSHAPE2.ordinal()].m_data[1][0] = 4;
		m_block[block.ZSHAPE2.ordinal()].m_data[1][1] = 22;
		m_block[block.ZSHAPE2.ordinal()].m_data[2][0] = 4;
		m_block[block.ZSHAPE2.ordinal()].m_data[2][1] = 21;
		m_block[block.ZSHAPE2.ordinal()].m_data[3][0] = 5;
		m_block[block.ZSHAPE2.ordinal()].m_data[3][1] = 21;
		// hello!
		m_block[block.OSHAPE.ordinal()].m_gyrate[0] = -1;// 负数既不可旋转
		m_block[block.OSHAPE.ordinal()].m_data[0][0] = 4;
		m_block[block.OSHAPE.ordinal()].m_data[0][1] = 20;
		m_block[block.OSHAPE.ordinal()].m_data[1][0] = 5;
		m_block[block.OSHAPE.ordinal()].m_data[1][1] = 20;
		m_block[block.OSHAPE.ordinal()].m_data[2][0] = 4;
		m_block[block.OSHAPE.ordinal()].m_data[2][1] = 21;
		m_block[block.OSHAPE.ordinal()].m_data[3][0] = 5;
		m_block[block.OSHAPE.ordinal()].m_data[3][1] = 21;
		// hello!
		m_block[block.SHAPE1.ordinal()].m_gyrate[0] = -1;
		m_block[block.SHAPE1.ordinal()].m_data[0][0] = 4;
		m_block[block.SHAPE1.ordinal()].m_data[0][1] = 20;
		// hello!
		m_block[block.SHAPE2.ordinal()].m_gyrate[0] = -1;
		m_block[block.SHAPE2.ordinal()].m_data[0][0] = 4;
		m_block[block.SHAPE2.ordinal()].m_data[0][1] = 20;
		m_block[block.SHAPE2.ordinal()].m_data[1][0] = 4;
		m_block[block.SHAPE2.ordinal()].m_data[1][1] = 21;
		// hello!
		m_block[block.SHAPE3.ordinal()].m_gyrate[0] = -1;
		m_block[block.SHAPE3.ordinal()].m_data[0][0] = 4;
		m_block[block.SHAPE3.ordinal()].m_data[0][1] = 20;
		m_block[block.SHAPE3.ordinal()].m_data[1][0] = 4;
		m_block[block.SHAPE3.ordinal()].m_data[1][1] = 21;
		m_block[block.SHAPE3.ordinal()].m_data[2][0] = 4;
		m_block[block.SHAPE3.ordinal()].m_data[2][1] = 22;
		// hello!
		m_block[block.BOMB.ordinal()].m_gyrate[0] = -1;
		m_block[block.BOMB.ordinal()].m_data[0][0] = 4;
		m_block[block.BOMB.ordinal()].m_data[0][1] = 21;
		m_block[block.BOMB.ordinal()].m_data[1][0] = 5;
		m_block[block.BOMB.ordinal()].m_data[1][1] = 21;
		m_block[block.BOMB.ordinal()].m_data[2][0] = 4;
		m_block[block.BOMB.ordinal()].m_data[2][1] = 22;
		m_block[block.BOMB.ordinal()].m_data[3][0] = 5;
		m_block[block.BOMB.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.STRIPX.ordinal()].m_gyrate[0] = 4;// x坐标
		m_block[block.STRIPX.ordinal()].m_gyrate[1] = 22;// y坐标
		// 数据区，8步，最多4个点（炸弹特殊处理),[0,0]=4,[0,1]=20构成第一个点【20，4】
		m_block[block.STRIPX.ordinal()].m_data[0][0] = 4;
		m_block[block.STRIPX.ordinal()].m_data[0][1] = 20;
		m_block[block.STRIPX.ordinal()].m_data[1][0] = 4;
		m_block[block.STRIPX.ordinal()].m_data[1][1] = 21;
		m_block[block.STRIPX.ordinal()].m_data[2][0] = 4;
		m_block[block.STRIPX.ordinal()].m_data[2][1] = 22;
		m_block[block.STRIPX.ordinal()].m_data[3][0] = 4;
		m_block[block.STRIPX.ordinal()].m_data[3][1] = 23;
		// hello!
		m_block[block.TSHAPEX.ordinal()].m_gyrate[0] = 4;
		m_block[block.TSHAPEX.ordinal()].m_gyrate[1] = 21;
		m_block[block.TSHAPEX.ordinal()].m_data[0][0] = 3;
		m_block[block.TSHAPEX.ordinal()].m_data[0][1] = 21;
		m_block[block.TSHAPEX.ordinal()].m_data[1][0] = 4;
		m_block[block.TSHAPEX.ordinal()].m_data[1][1] = 21;
		m_block[block.TSHAPEX.ordinal()].m_data[2][0] = 5;
		m_block[block.TSHAPEX.ordinal()].m_data[2][1] = 21;
		m_block[block.TSHAPEX.ordinal()].m_data[3][0] = 4;
		m_block[block.TSHAPEX.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.LSHAPEX.ordinal()].m_gyrate[0] = 4;
		m_block[block.LSHAPEX.ordinal()].m_gyrate[1] = 21;
		m_block[block.LSHAPEX.ordinal()].m_data[0][0] = 4;
		m_block[block.LSHAPEX.ordinal()].m_data[0][1] = 20;
		m_block[block.LSHAPEX.ordinal()].m_data[1][0] = 5;
		m_block[block.LSHAPEX.ordinal()].m_data[1][1] = 20;
		m_block[block.LSHAPEX.ordinal()].m_data[2][0] = 4;
		m_block[block.LSHAPEX.ordinal()].m_data[2][1] = 21;
		m_block[block.LSHAPEX.ordinal()].m_data[3][0] = 4;
		m_block[block.LSHAPEX.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.LSHAPE2X.ordinal()].m_gyrate[0] = 4;
		m_block[block.LSHAPE2X.ordinal()].m_gyrate[1] = 21;
		m_block[block.LSHAPE2X.ordinal()].m_data[0][0] = 4;
		m_block[block.LSHAPE2X.ordinal()].m_data[0][1] = 20;
		m_block[block.LSHAPE2X.ordinal()].m_data[1][0] = 5;
		m_block[block.LSHAPE2X.ordinal()].m_data[1][1] = 20;
		m_block[block.LSHAPE2X.ordinal()].m_data[2][0] = 5;
		m_block[block.LSHAPE2X.ordinal()].m_data[2][1] = 21;
		m_block[block.LSHAPE2X.ordinal()].m_data[3][0] = 5;
		m_block[block.LSHAPE2X.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.ZSHAPEX.ordinal()].m_gyrate[0] = 4;
		m_block[block.ZSHAPEX.ordinal()].m_gyrate[1] = 21;
		m_block[block.ZSHAPEX.ordinal()].m_data[0][0] = 3;
		m_block[block.ZSHAPEX.ordinal()].m_data[0][1] = 21;
		m_block[block.ZSHAPEX.ordinal()].m_data[1][0] = 4;
		m_block[block.ZSHAPEX.ordinal()].m_data[1][1] = 21;
		m_block[block.ZSHAPEX.ordinal()].m_data[2][0] = 4;
		m_block[block.ZSHAPEX.ordinal()].m_data[2][1] = 22;
		m_block[block.ZSHAPEX.ordinal()].m_data[3][0] = 5;
		m_block[block.ZSHAPEX.ordinal()].m_data[3][1] = 22;
		// hello!
		m_block[block.ZSHAPE2X.ordinal()].m_gyrate[0] = 4;
		m_block[block.ZSHAPE2X.ordinal()].m_gyrate[1] = 21;
		m_block[block.ZSHAPE2X.ordinal()].m_data[0][0] = 3;
		m_block[block.ZSHAPE2X.ordinal()].m_data[0][1] = 22;
		m_block[block.ZSHAPE2X.ordinal()].m_data[1][0] = 4;
		m_block[block.ZSHAPE2X.ordinal()].m_data[1][1] = 22;
		m_block[block.ZSHAPE2X.ordinal()].m_data[2][0] = 4;
		m_block[block.ZSHAPE2X.ordinal()].m_data[2][1] = 21;
		m_block[block.ZSHAPE2X.ordinal()].m_data[3][0] = 5;
		m_block[block.ZSHAPE2X.ordinal()].m_data[3][1] = 21;
		// hello!
		m_block[block.OSHAPEX.ordinal()].m_gyrate[0] = -1;// 负数既不可旋转
		m_block[block.OSHAPEX.ordinal()].m_data[0][0] = 4;
		m_block[block.OSHAPEX.ordinal()].m_data[0][1] = 20;
		m_block[block.OSHAPEX.ordinal()].m_data[1][0] = 5;
		m_block[block.OSHAPEX.ordinal()].m_data[1][1] = 20;
		m_block[block.OSHAPEX.ordinal()].m_data[2][0] = 4;
		m_block[block.OSHAPEX.ordinal()].m_data[2][1] = 21;
		m_block[block.OSHAPEX.ordinal()].m_data[3][0] = 5;
		m_block[block.OSHAPEX.ordinal()].m_data[3][1] = 21;
		// hello!
		for (int i = 0; i < 10; ++i) {
			// Log.i("你好",""+i);
			InitHomePageData(i);
		}
	}

	/**
	 * 加载bitmap
	 * 
	 * @param key
	 *            index
	 * @param tile
	 *            Drawable
	 */
	private void loadTile(int key, Drawable tile) {
		Bitmap bitmap = Bitmap.createBitmap(BLOCK_WIDE, BLOCK_HIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, BLOCK_WIDE, BLOCK_HIGHT);
		tile.draw(canvas);
		m_bitMapBlock[key] = bitmap;
	}

	public void SetBtnOK(Button btnOK) {
		m_BtnOK = btnOK;
	}

	public void SetTextView_Tips(TextView TextView_Tips) {
		m_TextView_Tips = TextView_Tips;
	}

	public void SetTextView_Name(TextView TextView_Name) {
		m_TextView_Name = TextView_Name;
	}

	public void SetEditText_Name(EditText EditText_Name) {
		m_EditText_Name = EditText_Name;
	}

	public void SetTextView_Score(TextView TextView_Score) {
		m_TextView_Score = TextView_Score;
	}

	/**
	 * 设置activity context
	 * 
	 * @param context
	 *            context
	 */
	public void SetContext(Context context) {
		m_context = context;
	}

	public void SetMode(state sta) {
		m_state = sta;
		if (sta == state.PAUSE) {
			m_TextView_Tips.setVisibility(View.VISIBLE);
			m_TextView_Tips.setText("Pause ... Pause");
			m_lockKey = true;
			return;
		}
		m_TextView_Tips.setVisibility(View.INVISIBLE);
		if (sta == state.HOMEPAGE) {
			// m_mRedrawHandler.sendMessageDelayed(msg, 1000);
			// Log.i("你好","sendEmptyMessageDelayed(0, 5000)前");
			m_mRedrawHandler.sendEmptyMessageDelayed(0, 100);
			// Log.i("你好","sendEmptyMessageDelayed(0, 5000)后");
		} else if (sta == state.RUN) {
			// Begin();
			m_mRedrawHandler.sendEmptyMessage(1);
			m_mRedrawHandler.sendEmptyMessage(2);
			m_mRedrawHandler.sendEmptyMessage(3);
			// m_mRedrawHandler.sendEmptyMessage(4);
		} else if (sta == state.GAMEOVER) {
			long score = 0;
			m_lockKey = true;
			SQLiteHelper m_SqLiteHelper = new SQLiteHelper(m_context);
			SQLiteDatabase m_SQLiteDatabase;
			m_SQLiteDatabase = m_SqLiteHelper.getWritableDatabase();
			String _SqlText = "Select min(Score) From ranking";
			Cursor _CursorB = m_SQLiteDatabase.rawQuery(_SqlText, null);
			while (_CursorB.moveToNext()) {
				score = _CursorB.getInt(_CursorB.getColumnIndex("min(Score)"));
			}
			m_TextView_Tips.setVisibility(View.VISIBLE);
			m_BtnOK.setVisibility(View.VISIBLE);
			if (m_currentData.m_score > score) {
				m_TextView_Tips.setText("你的得分：" + m_currentData.m_score);
				m_TextView_Name.setVisibility(View.VISIBLE);
				m_EditText_Name.setVisibility(View.VISIBLE);
				m_BtnOK.setText("确定");
			} else {
				m_TextView_Tips.setText("抱歉你弱爆了得分仅:" + m_currentData.m_score
						+ " 低于最低分" + score);
				m_BtnOK.setText("Replay");
			}
		}
	}

	/**
	 * 按键处理（也可为触屏处理后）
	 * 
	 * @param key
	 */
	public void KeyDown(keys key) {
		boolean flag = false;
		boolean fl = true;
		int cxOffset, cyOffset;// 旋转偏移
		if (m_state == state.HOMEPAGE)
			return;
		if (key == keys.PAUSE)// 暂停
		{
			m_currentData.m_previewFlag = false;
			invalidate();
			m_currentData.m_pause = !m_currentData.m_pause;
			if (!m_currentData.m_pause) {
				m_lockKey = false;
				SetMode(state.RUN);
				m_TextView_Tips.setVisibility(View.INVISIBLE);
			} else {
				// Font drawFont = new Font("宋体", 50);
				// SolidBrush drawBrush = new SolidBrush(Color.Red);
				m_lockKey = true;
				if (DateBaseConfig.GetSwitch().equals("on"))
					m_mediaPlayer[sound.PAUSE.ordinal()].start();
				m_old_state = state.RUN;
				SetMode(state.PAUSE);
			}
			return;
		}
		if (m_lockKey)
			return;
		if (key == keys.RIGHT)// VK_Right
		{
			if (m_currentData.m_data[0][0] == 0)
				m_currentData.m_data[0][0] = m_currentData.m_SHAPE1_cx;
			for (int i = 0; i < 4; ++i) {
				if (m_currentData.m_data[i][0] == 0)
					break;
				if (m_currentData.m_data[i][0] % 10 == 9)// 最右端
				{
					flag = true;
					break;
				}
				if (m_currentData.m_flag == block.BOMB.ordinal())
					if (m_currentData.m_data[i][0] % 10 == 8)// 最右端
					{
						flag = true;
						break;
					}
				// 有障碍
				if (m_currentData.m_flag != block.SHAPE1.ordinal()
						&& m_unit[m_currentData.m_data[i][1]][m_currentData.m_data[i][0] % 10 + 1] != 0) {
					flag = true;
					break;
				}
			}
			if (!flag)// 可右移
			{
				m_currentData.m_gyrate[0]++;
				for (int i = 0; i < 4; ++i) {
					if (m_currentData.m_data[i][0] > 0)
						m_currentData.m_data[i][0]++;
				}
				m_currentData.m_SHAPE1_cx++;
			}
		} else if (key == keys.LEFT)// 左移
		{
			if (m_currentData.m_data[0][0] == 0)
				m_currentData.m_data[0][0] = m_currentData.m_SHAPE1_cx;
			for (int i = 0; i < 4; ++i) {
				if (m_currentData.m_data[i][0] == 0)
					break;
				if (m_currentData.m_data[i][0] % 10 == 0) {
					flag = true;
					break;
				}
				if (m_currentData.m_flag == block.BOMB.ordinal())
					if (m_currentData.m_data[i][0] % 10 == 1) {
						flag = true;
						break;
					}
				if (m_currentData.m_flag != block.SHAPE1.ordinal()
						&& m_unit[m_currentData.m_data[i][1]][m_currentData.m_data[i][0] % 10 - 1] != 0) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				m_currentData.m_gyrate[0]--;
				for (int i = 0; i < 4; ++i) {
					if (m_currentData.m_data[i][0] > 0)
						m_currentData.m_data[i][0]--;
				}
				m_currentData.m_SHAPE1_cx--;
			}
		} else if (key == keys.DOWN) {
			m_lockKey = true;
			m_currentData.m_interval = -m_currentData.m_interval;// 取负值仅仅是作为标记
			// m_currentData.m_interval = 15;
		} else if (key == keys.UP
				&& (m_currentData.m_flag < block.SHAPE1.ordinal() || m_currentData.m_flag > block.BOMB
						.ordinal())) {
			if (m_currentData.m_data[0][0] == 0)
				m_currentData.m_data[0][0] = m_currentData.m_SHAPE1_cx;
			if (m_currentData.m_gyrate[0] >= 0)// 可旋转
			{
				int bottom = 20;
				int[][] data = new int[4][2];// 暂存
				// data = (int[,])m_currentData.m_data.Clone();//暂存
				for (int i = 0; i < 4; ++i) {
					data[i][0] = m_currentData.m_data[i][0];
					data[i][1] = m_currentData.m_data[i][1];
				}
				int what = 0;
				for (int k = 0; k < 4; ++k)// 对每个块旋转
				{
					what = m_currentData.m_data[k][0] / 10 * 10;
					m_currentData.m_data[k][0] = m_currentData.m_data[k][0] % 10;
					// 旋转
					cxOffset = m_currentData.m_data[k][0]
							- m_currentData.m_gyrate[0];
					cyOffset = m_currentData.m_data[k][1]
							- m_currentData.m_gyrate[1];
					m_currentData.m_data[k][0] = m_currentData.m_gyrate[0]
							+ cyOffset;
					m_currentData.m_data[k][1] = m_currentData.m_gyrate[1]
							- cxOffset;
					// 旋转边界
					if (m_currentData.m_data[k][1] < 0
							|| m_currentData.m_data[k][0] < 0
							|| m_currentData.m_data[k][0] > 9) {
						fl = false;
						break;
					}
					// 旋转障碍判定 共4总判定 旋转点不处理
					if (m_currentData.m_data[k][0] != m_currentData.m_gyrate[0]
							|| m_currentData.m_data[k][1] != m_currentData.m_gyrate[1]) {
						if (m_currentData.m_data[k][0] <= data[k][0] % 10
								&& m_currentData.m_data[k][1] >= data[k][1]) {
							for (int i = 0; i <= m_currentData.m_data[k][1]
									- data[k][1]; ++i) {
								for (int j = 0; j <= data[k][0] % 10
										- m_currentData.m_data[k][0]; ++j) {
									if (m_unit[m_currentData.m_data[k][1] - i][m_currentData.m_data[k][0]
											+ j] != 0) {
										fl = false;
										break;
									}
								}
								if (!fl)
									break;
							}
						}// 2
						else if (m_currentData.m_data[k][0] >= data[k][0] % 10
								&& m_currentData.m_data[k][1] >= data[k][1]) {
							for (int i = 0; i <= m_currentData.m_data[k][1]
									- data[k][1]; ++i) {
								for (int j = 0; j <= m_currentData.m_data[k][0]
										- data[k][0] % 10; ++j) {
									if (m_unit[data[k][1] + i][data[k][0] % 10
											+ j] != 0) {
										fl = false;
										break;
									}
								}
								if (!fl)
									break;
							}
						}// 3
						else if (m_currentData.m_data[k][0] >= data[k][0] % 10
								&& m_currentData.m_data[k][1] <= data[k][1]) {
							for (int i = 0; i <= data[k][1]
									- m_currentData.m_data[k][1]; ++i) {
								for (int j = 0; j <= m_currentData.m_data[k][0]
										- data[k][0] % 10; ++j) {
									if (m_unit[m_currentData.m_data[k][1] + i][data[k][0]
											% 10 + j] != 0) {
										fl = false;
										break;
									}
								}
								if (!fl)
									break;
							}
						}// 4
						else if (m_currentData.m_data[k][0] <= data[k][0] % 10
								&& m_currentData.m_data[k][1] <= data[k][1]) {
							for (int i = 0; i <= data[k][1]
									- m_currentData.m_data[k][1]; ++i) {
								for (int j = 0; j <= data[k][0] % 10
										- m_currentData.m_data[k][0]; ++j) {
									if (m_unit[m_currentData.m_data[k][1] + i][m_currentData.m_data[k][0]
											+ j] != 0) {
										fl = false;
										break;
									}
								}
								if (!fl)
									break;
							}
						}
					}
					m_currentData.m_data[k][0] += what;
					if (m_currentData.m_data[k][1] < bottom)
						bottom = m_currentData.m_data[k][1];
				}
				if (fl)// 旋转成功
				{
					// m_currentData.m_curLine = bottom;
				} else {
					for (int i = 0; i < 4; ++i) {
						m_currentData.m_data[i][0] = data[i][0];
						m_currentData.m_data[i][1] = data[i][1];
					}
				}
				m_currentData.m_SHAPE1_cx = m_currentData.m_data[0][0];
			}
		} else if (key == keys.UP
				&& m_currentData.m_flag > block.SHAPE1.ordinal()) {
			// if(m_currentData.m_data[0][0]==0)
			// m_currentData.m_data[0][0]=m_currentData.m_SHAPE1_cx;
			if (m_currentData.m_flag == block.BOMB.ordinal()) {
				m_currentData.m_bombFlag = true;//
			} else {
				if (m_currentData.m_cx == -1)// 可发射
				{
					if (DateBaseConfig.GetSwitch().equals("on"))
						m_mediaPlayer[sound.GUNFIRE.ordinal()].start();
					m_currentData.m_cx = m_currentData.m_data[0][0];// 带block块号
					m_currentData.m_cy = m_currentData.m_data[0][1];
					m_mRedrawHandler.sendEmptyMessageDelayed(2, NUM_SLEEPB);// 启动时钟
				}
			}
		}
		invalidate();
	}

	public void Begin() {
		m_state = state.RUN;
		m_lockKey = false;
		Preview(67);
		for (int mm = 0; mm < 4; ++mm) {
			m_currentData.m_data[mm] = m_block[NUM_BLOCK].m_data[mm].clone();
		}
		m_currentData.m_flag = m_block[NUM_BLOCK].m_flag;
		// m_currentData.m_curLine = 20;// m_currentData.
		m_currentData.m_gyrate = m_block[NUM_BLOCK].m_gyrate.clone();
		m_currentData.m_point_Bomb_A = m_currentData.m_point_Bomb.clone();
		Preview();
		TimeTick1();
		// TimeTick4();
		// Log.i("你好", "Begin() 结束");
	}

	/**
	 * 负责首页跑酷
	 */
	private void TimeTick() {
		long timeStart, timeEnd;
		if (m_state != state.HOMEPAGE)
			return;
		// Log.i("你好","sendEmptyMessageDelayed(0, 5000)--TimeTick"+System.currentTimeMillis());
		// long now = System.currentTimeMillis();
		timeStart = System.currentTimeMillis();

		for (int i = 0; i < 10; ++i) {
			m_homePageData[i].m_cy -= m_homePageData[i].m_speed;
			if (m_homePageData[i].m_cy < -m_homePageData[i].m_hight * 4)
				InitHomePageData(i);
		}
		invalidate();
		timeEnd = System.currentTimeMillis();
		// Log.i("你好","ping="+(timeEnd-timeStart));
		if (timeEnd - timeStart > HomePageData.DELAY_NUM) {
			m_mRedrawHandler.sendEmptyMessage(0);
			// Log.i("你好", "TimeTick() ping = " + (timeEnd - timeStart));

		} else {
			m_mRedrawHandler.sendEmptyMessageDelayed(0, HomePageData.DELAY_NUM
					- timeEnd + timeStart);
		}
	}

	/**
	 * 主时钟 负责主块更新
	 */
	private void TimeTick1() {
		long timeStart, timeEnd;
		if (m_state == state.RUN) {
			timeStart = System.currentTimeMillis();
			// int num = m_currentData.m_curLine - 1;
			boolean flag = false;
			// if (m_currentData.m_flag == block.BOMB.ordinal())
			// num--;
			// 正常下落时num>=0
			// if (num < 0) {// 到底了
			// flag = true;
			// }
			if (m_currentData.m_bombFlag) {
				flag = true;
			}
			if (m_currentData.m_flag < block.SHAPE1.ordinal()
					|| m_currentData.m_flag > block.BOMB.ordinal()) {
				for (int i = 0; i < 4; ++i) {
					if (m_currentData.m_data[i][1] <= 0) {
						flag = true;
					}
				}
			} else if (m_currentData.m_flag == block.SHAPE1.ordinal()) {
				if (m_currentData.m_shape1Flag) {
					flag = true;
					m_currentData.m_shape1Flag = false;
				}
			} else if (m_currentData.m_flag == block.SHAPE2.ordinal()) {
				if (m_currentData.m_data[0][1] <= 0
						|| m_currentData.m_data[1][1] <= 0)
					flag = true;
			} else if (m_currentData.m_flag == block.SHAPE3.ordinal()) {
				if (m_currentData.m_data[0][1] <= 0
						|| m_currentData.m_data[1][1] <= 0
						|| m_currentData.m_data[2][1] <= 0)
					flag = true;
			}else if(m_currentData.m_flag == block.BOMB.ordinal()){
				for (int i = 0; i < 4; ++i) {
					if (m_currentData.m_data[i][1] <= 1) {
						flag = true;
					}
				}
			}
			if (m_currentData.m_flag == block.BOMB.ordinal() && !flag)// 爆炸块特殊处理
			{				
				for (int f = 0; f < 4; ++f) {
					if (m_unit[m_currentData.m_data[0][1] - 2][m_currentData.m_data[0][0]
							% 10 - 1 + f] != 0) {
						flag = true;
						break;
					}
				}
			} else if (m_currentData.m_flag != block.SHAPE1.ordinal() && !flag) {
				for (int i = 0; i < 4; ++i) {
					if (m_currentData.m_data[i][0] > 0) {
						if (m_unit[m_currentData.m_data[i][1] - 1][m_currentData.m_data[i][0] % 10] != 0) {
							flag = true;
							break;
						}
					} else {
						break;
					}
				}
			}
			if (!flag)// 下落
			{
				// Log.i("你好","TimeTick1() 下落");
				if (m_currentData.m_SHAPE1_Flag) {
					for (int h = 0; h < 4; ++h) {
						if (m_currentData.m_data[h][1] > 0) {
							if (m_currentData.m_data[h][0] > 0) {
								RegisterEffect(m_currentData.m_data[h][0],
										m_currentData.m_data[h][1]);
							}
							m_currentData.m_data[h][1]--;
						}
					}
					if (m_currentData.m_gyrate[0] >= 0)
						m_currentData.m_gyrate[1]--;
					// if (m_currentData.m_flag != block.SHAPE1.ordinal()
					// && m_currentData.m_flag < block.STRIPX.ordinal())
					// m_currentData.m_curLine--;
				}
				if (m_currentData.m_flag == block.SHAPE1.ordinal()
						|| m_currentData.m_flag > block.BOMB.ordinal())// 穿心蛋及消隐
				{
					boolean flag2 = false;
					if (m_currentData.m_SHAPE1_Flag) {
						// invalidate();
						m_currentData.m_SHAPE1_cx = m_currentData.m_data[0][0];
						m_currentData.m_data[0][0] = 0;
						// m_currentData.m_Xflag = true;
						m_currentData.m_SHAPE1_Flag = false;
					} else {
						// m_currentData.m_curLine--;
						// m_currentData.m_Xflag = false;
						m_currentData.m_SHAPE1_Flag = true;
						m_currentData.m_data[0][0] = m_currentData.m_SHAPE1_cx;
						// m_currentData.m_curLine--;
						if (m_currentData.m_flag == block.SHAPE1.ordinal()
								&& m_unit[m_currentData.m_data[0][1]][m_currentData.m_data[0][0] % 10] == 0)// 计算有无整行
						{// Log.i("你好","测试SHAPE1 有无整行");
							m_unit[m_currentData.m_data[0][1]][m_currentData.m_data[0][0] % 10] = 1;
							for (int s = 0; s < NUM_CX; ++s) {
								if (m_unit[m_currentData.m_data[0][1]][s] == 0) {
									flag2 = true;
									break;
								}
							}
							m_unit[m_currentData.m_data[0][1]][m_currentData.m_data[0][0] % 10] = 0;
							if (!flag2)// 有整行开始消减
							{
								m_currentData.m_calcScore = 0;
								m_unit[m_currentData.m_data[0][1]][m_currentData.m_data[0][0] % 10] = 1;
								m_mRedrawHandler.sendEmptyMessage(3);// Log.i("你好","SHAPE1 有整行");
							}
						}
						if (m_currentData.m_data[0][1] == 0) {
							m_currentData.m_shape1Flag = true;
						}
					}
				}
			} else {
				if (m_currentData.m_data[0][1] >= 17
						&& m_currentData.m_flag < block.BOMB.ordinal()) {
					for (int g = 1; g < 3; ++g) {
						if (m_currentData.m_data[g][1] >= 20) {
							// MessageBox.Show("game       over");Font drawFont
							// = new Font("宋体", 50);
							// Font drawFont = new Font("宋体", 50);
							// SolidBrush drawBrush = new SolidBrush(Color.Red);
							SetMode(state.GAMEOVER);
							return;
							// m_g.DrawString("Game Over", drawFont, drawBrush,
							// 20.0F, 150.0F);
						}
					}
				}
				if (m_currentData.m_flag < block.SHAPE1.ordinal()) {
					for (int j = 0; j < 4; ++j) {
						if (m_currentData.m_data[j][0] > 0) {
							m_unit[m_currentData.m_data[j][1]][m_currentData.m_data[j][0] % 10] = m_currentData.m_data[j][0] / 10;
						}
					}
					m_currentData.m_calcScore = 0;
					m_mRedrawHandler.sendEmptyMessageDelayed(3, NUM_SLEEPA);// 启动消减
				} else if (m_currentData.m_flag == block.BOMB.ordinal()) {
					m_currentData.m_expold = 0;
					m_currentData.m_point_Bomb[4] = m_currentData.m_data[0][1] - 1;
					m_currentData.m_point_Bomb[5] = m_currentData.m_data[0][0] % 10 - 1;
					// if (m_currentData.m_calcScore == -1) {
					m_lockKey = true;
					m_mRedrawHandler.sendEmptyMessageDelayed(3, 50);// 要爆炸
					// }
				} else if (m_currentData.m_flag >= block.STRIPX.ordinal()) {// 消隐块
					for (int mm = 0; mm < 4; ++mm) {
						m_currentData.m_X[mm] = m_currentData.m_data[mm]
								.clone();
					}
					if (DateBaseConfig.GetSwitch().equals("on"))
						m_mediaPlayer[sound.GUNFIRE.ordinal()].start();
					m_mRedrawHandler.sendEmptyMessageDelayed(2, NUM_SLEEPB);// 启动时钟
				}
				for (int mm = 0; mm < 4; ++mm) {
					m_currentData.m_data[mm] = m_block[NUM_BLOCK].m_data[mm]
							.clone();
				}
				m_currentData.m_flag = m_block[NUM_BLOCK].m_flag;
				// m_currentData.m_curLine = 20;// m_currentData.
				m_currentData.m_gyrate = m_block[NUM_BLOCK].m_gyrate.clone();
				m_currentData.m_point_Bomb_A = m_currentData.m_point_Bomb
						.clone();
				Preview();// 立即预览下一个
				// MessageBox.Show(m_currentData.m_data[0, 1].ToString());
				m_lockKey = false;
				m_currentData.m_interval = Math.abs(m_currentData.m_interval);
				// m_mRedrawHandler.sendEmptyMessageDelayed(3,20);
				if (m_currentData.m_flag != block.BOMB.ordinal())
					TimeTick();// 下一个
			}
			invalidate();
			timeEnd = System.currentTimeMillis();
			if (timeEnd - timeStart > (m_currentData.m_interval > 0 ? ((m_currentData.m_flag == block.SHAPE1
					.ordinal() || m_currentData.m_flag > block.BOMB.ordinal()) ? m_currentData.m_interval / 2
					: m_currentData.m_interval)
					: ((m_currentData.m_flag == block.SHAPE1.ordinal() || m_currentData.m_flag > block.BOMB
							.ordinal()) ? 30 : 15))) {// 有延迟
				m_mRedrawHandler.sendEmptyMessage(1);
			} else {
				m_mRedrawHandler
						.sendEmptyMessageDelayed(
								1,// 慢慢看
								(m_currentData.m_interval > 0 ? ((m_currentData.m_flag == block.SHAPE1
										.ordinal() || m_currentData.m_flag > block.BOMB
										.ordinal()) ? m_currentData.m_interval / 2
										: m_currentData.m_interval)
										: ((m_currentData.m_flag == block.SHAPE1
												.ordinal() || m_currentData.m_flag > block.BOMB
												.ordinal()) ? 30 : 15))
										- timeEnd + timeStart);
			}
		}

	}

	/**
	 * 负责发射块更新
	 */
	private void TimeTick2() {
		long timeStart, timeEnd;
		boolean flag = false, flag2 = false;
		for (int i = 0; i < 4; ++i) {
			if (m_currentData.m_X[i][0] > 0)
				flag = true;
		}
		if (!flag && m_currentData.m_cx == -1)
			return;
		timeStart = System.currentTimeMillis();
		if (m_currentData.m_cx > 0) {
			if (m_currentData.m_cy < 0// 有障碍
					|| m_unit[m_currentData.m_cy][m_currentData.m_cx % 10] != 0) {
				if (m_currentData.m_flag == block.SHAPE2.ordinal()) {
					if (m_currentData.m_cy >= 0)
						m_unit[m_currentData.m_cy][m_currentData.m_cx % 10] = 0;
				} else// block.SHAPE3
				{
					m_unit[m_currentData.m_cy + 1][m_currentData.m_cx % 10] = m_currentData.m_cx / 10;
					m_currentData.m_calcScore = 0;
					m_mRedrawHandler.sendEmptyMessage(3);
				}
				m_currentData.m_cx = -1;
			}
			// 无障碍
			else if (m_unit[m_currentData.m_cy][m_currentData.m_cx % 10] == 0) {
				m_currentData.m_cy--;
				flag2 = true;
			}
		}
		if (flag) {
			for (int i = 0; i < 4; ++i) {
				if (m_currentData.m_X[i][0] > 0) {
					if (m_currentData.m_X[i][1] < 0// 有障碍
							|| m_unit[m_currentData.m_X[i][1]][m_currentData.m_X[i][0] % 10] != 0) {
						if (m_currentData.m_X[i][1] >= 0)
							m_unit[m_currentData.m_X[i][1]][m_currentData.m_X[i][0] % 10] = 0;
						m_currentData.m_X[i][0] = -1;
					} else if (m_unit[m_currentData.m_X[i][1]][m_currentData.m_X[i][0] % 10] == 0) {
						m_currentData.m_X[i][1]--;
						flag2 = true;
					}
				}
			}
		}
		if (flag2) {
			invalidate();
			timeEnd = System.currentTimeMillis();
			if (timeEnd - timeStart > NUM_SLEEPB) {// 有延迟
				m_mRedrawHandler.sendEmptyMessage(2);
			} else {
				m_mRedrawHandler.sendEmptyMessageDelayed(2, NUM_SLEEPB
						- (timeEnd - timeStart));
			}
		}
	}

	/**
	 * 负责爆炸效果及消减块
	 */
	private void TimeTick3() {
		CalcScore();
		Explode();
	}

	/**
	 * 负责处理特效
	 */
	private void TimeTick4() {
		if (DateBaseConfig.GetSwitch_e().equals("on")) {
			for (int i = 0; i < m_effect.length; ++i) {
				if (m_effect[i][1] > -1) {
					m_effect[i][1] -= 60 * 100;// 每次衰减60透明度
				}
			}
			invalidate();
			// Log.i("你好","TimeTick4() Run");
		}
		m_mRedrawHandler.sendEmptyMessageDelayed(4, 20);
	}

	private void InitHomePageData(int num) {
		int left = 9, right = 0, top = 20, bottom = 23;// 方块最左，最右。。。
		Preview();
		for (int m = 0; m < 4; ++m)// 计算最左最右最高矮
		{
			if (m_block[NUM_BLOCK].m_data[m][0] > 0) {
				if (m_block[NUM_BLOCK].m_data[m][0] % 10 < left)
					left = m_block[NUM_BLOCK].m_data[m][0] % 10;
				if (m_block[NUM_BLOCK].m_data[m][0] % 10 > right)
					right = m_block[NUM_BLOCK].m_data[m][0] % 10;
				if (m_block[NUM_BLOCK].m_data[m][1] < bottom)
					bottom = m_block[NUM_BLOCK].m_data[m][1];
				if (m_block[NUM_BLOCK].m_data[m][1] > top)
					top = m_block[NUM_BLOCK].m_data[m][1];
			} else
				break;
		}
		for (int i = 0; i < 4; ++i) {
			m_homePageData[num].m_dataTetris.m_data[i] = m_block[NUM_BLOCK].m_data[i]
					.clone();
		}
		m_homePageData[num].m_dataTetris.m_flag = m_block[NUM_BLOCK].m_flag;
		m_homePageData[num].m_cx_flag = left;
		m_homePageData[num].m_cy_flag = bottom;
		if (m_homePageData[num].m_dataTetris.m_flag == block.BOMB.ordinal()) {
			m_homePageData[num].m_point_Bomb[0] = m_random.nextInt(12) + 1;
			m_homePageData[num].m_point_Bomb[1] = m_random.nextInt(12) + 1;
			m_homePageData[num].m_point_Bomb[2] = m_random.nextInt(12) + 1;
			m_homePageData[num].m_point_Bomb[3] = m_random.nextInt(12) + 1;
		}

		if (num < 4) {
			m_homePageData[num].m_wide = BLOCK_WIDE / 2;
			m_homePageData[num].m_hight = BLOCK_HIGHT / 2;
		} else if (num < 7) {
			m_homePageData[num].m_wide = BLOCK_WIDE;
			m_homePageData[num].m_hight = BLOCK_HIGHT;
		} else if (num < 9) {
			m_homePageData[num].m_wide = (int) (BLOCK_WIDE * 1.5);
			m_homePageData[num].m_hight = (int) (BLOCK_HIGHT * 1.5);
		} else {
			m_homePageData[num].m_wide = BLOCK_WIDE * 2;
			m_homePageData[num].m_hight = BLOCK_HIGHT * 2;
		}
		m_homePageData[num].m_speed = m_homePageData[num].m_hight / 5;
		m_homePageData[num].m_cx = m_random.nextInt(m_cxClient
				- m_homePageData[num].m_wide * (right - left + 1));
		if (m_homePageData[num].m_flag == 0) {
			m_homePageData[num].m_flag++;
			m_homePageData[num].m_cy = m_cyClient + m_homePageData[num].m_hight
					* (4 + num % 5 * 5);
		} else
			m_homePageData[num].m_cy = m_cyClient + m_homePageData[num].m_hight
					* 6;
	}

	private void Preview() {
		Preview(-1);
	}

	/**
	 * 预览，生成方格并放入缓冲
	 */
	private void Preview(int _seed)// 预览，生成方格并放入缓冲
	{
		/* m_block[NUM_BLOCK]既为缓冲去 */
		int seed;
		if (_seed > 0)
			seed = m_random.nextInt(_seed);
		else {
			seed = m_random.nextInt(101);// 随机产生命中
			// seed = 10;
		}
		int numGyrate;// 旋转次数
		int cxOffset, cyOffset;// 旋转偏移
		int left = 9, right = 0, top = 20, bottom = 23;// 方块最左，最右。。。以便修正旋转后位置
		// int cx, cy;//预览区栅格坐标
		// Log.i("你好","seed = "+seed);
		for (int i = 0; i < NUM_BLOCK; i++) {
			if (seed <= m_block[i].m_frequency)// 命中
			{
				// m_block[NUM_BLOCK] = m_block[i];//找到并放入缓存
				for (int k = 0; k < 4; ++k) {
					m_block[NUM_BLOCK].m_data[k] = m_block[i].m_data[k].clone();
				}
				m_block[NUM_BLOCK].m_gyrate = m_block[i].m_gyrate.clone();
				m_block[NUM_BLOCK].m_flag = m_block[i].m_flag;
				// Log.i("你好","m_block["+i+"].m_data[0][0] = "+m_block[i].m_data[0][0]);
				break;
			}
		}
		m_currentData.m_SHAPE1_Flag = true;
		m_currentData.m_bombFlag = false;
		// Log.i("你好","m_block[block.LSHAPE2.ordinal()].m_flag = "+m_block[block.LSHAPE2.ordinal()].m_flag);
		for (int k = 0; k < 4; k++) {
			for (int a = 0; a < 4; a++) {
				m_unitPreview[k][a] = 0;
			}
		}
		if (m_block[NUM_BLOCK].m_flag == block.BOMB.ordinal()) {
			m_unitPreview[0][0] = m_random.nextInt(12) + 1;
			m_unitPreview[0][3] = m_random.nextInt(12) + 1;
			m_unitPreview[3][0] = m_random.nextInt(12) + 1;
			m_unitPreview[3][3] = m_random.nextInt(12) + 1;
			m_currentData.m_point_Bomb[0] = m_unitPreview[0][0];// 携带块
			m_currentData.m_point_Bomb[1] = m_unitPreview[0][3];
			m_currentData.m_point_Bomb[2] = m_unitPreview[3][0];
			m_currentData.m_point_Bomb[3] = m_unitPreview[3][3];
		}
		if (m_block[NUM_BLOCK].m_gyrate[0] > 0)// 可旋转
		{
			if (m_block[NUM_BLOCK].m_flag == block.STRIP.ordinal())// 长条特殊
				numGyrate = m_random.nextInt(2);
			else
				numGyrate = m_random.nextInt(4);
			for (int j = 0; j < numGyrate; ++j)// 旋转
			{
				for (int k = 0; k < 4; ++k)// 对每个块旋转
				{
					// MessageBox.Show(m_block[NUM_BLOCK].m_data[k,
					// 0].ToString() + ","
					// + m_block[NUM_BLOCK].m_data[k, 1].ToString());
					cxOffset = m_block[NUM_BLOCK].m_data[k][0]
							- m_block[NUM_BLOCK].m_gyrate[0];
					cyOffset = m_block[NUM_BLOCK].m_data[k][1]
							- m_block[NUM_BLOCK].m_gyrate[1];
					m_block[NUM_BLOCK].m_data[k][0] = m_block[NUM_BLOCK].m_gyrate[0]
							+ cyOffset;
					m_block[NUM_BLOCK].m_data[k][1] = m_block[NUM_BLOCK].m_gyrate[1]
							- cxOffset;
					// MessageBox.Show(m_block[NUM_BLOCK].m_data[k,
					// 0].ToString() + ","
					// + m_block[NUM_BLOCK].m_data[k, 1].ToString());
				}
			}
		}
		for (int m = 0; m < 4; ++m)// 计算最左最右最高矮
		{
			if (m_block[NUM_BLOCK].m_data[m][0] > 0) {
				if (m_block[NUM_BLOCK].m_data[m][0] < left)
					left = m_block[NUM_BLOCK].m_data[m][0];
				if (m_block[NUM_BLOCK].m_data[m][0] > right) {
					right = m_block[NUM_BLOCK].m_data[m][0];
					// Log.i("你好","right = "+right+" m = "+m);
				}
				if (m_block[NUM_BLOCK].m_data[m][1] < bottom)
					bottom = m_block[NUM_BLOCK].m_data[m][1];
				if (m_block[NUM_BLOCK].m_data[m][1] > top)
					top = m_block[NUM_BLOCK].m_data[m][1];
			} else
				break;
		}
		// Log.i("你好","left = "+left+" right = "+right+" bottom = "+bottom+" top = "+top);
		// MessageBox.Show(left.ToString() + "," + right.ToString() + "," +
		// top.ToString() + "," + bottom.ToString());
		if (bottom > NUM_CY)// 缓冲区cy轴修正
		{
			for (int n = 0; n < 4; n++) {
				if (m_block[NUM_BLOCK].m_data[n][0] > 0) {
					m_block[NUM_BLOCK].m_data[n][1] -= bottom - NUM_CY;
				} else
					break;
			}
			m_block[NUM_BLOCK].m_gyrate[1] -= bottom - NUM_CY;
		}
		if (left - (NUM_CX - 1 - right) >= 2)// 左移
		{
			for (int p = 0; p < 4; ++p) {
				if (m_block[NUM_BLOCK].m_data[p][0] > 0) {
					m_block[NUM_BLOCK].m_data[p][0] -= (left - (NUM_CX - 1 - right)) / 2;
				} else
					break;
			}
			m_block[NUM_BLOCK].m_gyrate[0] -= (left - (NUM_CX - 1 - right)) / 2;
		}
		if ((NUM_CX - 1 - right) - left >= 2)// 右移
		{
			for (int w = 0; w < 4; ++w) {
				if (m_block[NUM_BLOCK].m_data[w][0] > 0) {
					m_block[NUM_BLOCK].m_data[w][0] += ((NUM_CX - 1 - right) - left) / 2;
				} else
					break;
			}
			m_block[NUM_BLOCK].m_gyrate[0] += ((NUM_CX - 1 - right) - left) / 2;
		}
		for (int t = 0; t < 4; ++t)// 让缓冲区cx坐标携带block类型
		{
			if (m_block[NUM_BLOCK].m_data[t][0] > 0) {
				m_block[NUM_BLOCK].m_data[t][0] += (m_random.nextInt(12) + 1) * 10;
			} else
				break;
		}
		if (top - bottom < 2) {
			for (int r = 0; r < 4; ++r) {
				if (m_block[NUM_BLOCK].m_data[r][0] > 0) {
					// MessageBox.Show((m_block[NUM_BLOCK].m_flag).ToString() +
					// "," +
					// (m_block[NUM_BLOCK].m_data[r, 0] % 10 - 3).ToString() +
					// "," +
					// (m_block[NUM_BLOCK].m_data[r, 1] - NUM_CY +
					// 1).ToString());
					m_unitPreview[m_block[NUM_BLOCK].m_data[r][1] - NUM_CY + 1][m_block[NUM_BLOCK].m_data[r][0] % 10 - 3] = m_block[NUM_BLOCK].m_data[r][0] / 10;
				} else
					break;
			}
		} else {
			for (int r = 0; r < 4; ++r) {
				if (m_block[NUM_BLOCK].m_data[r][0] > 0) {
					// MessageBox.Show((m_block[NUM_BLOCK].m_flag).ToString() +
					// "," +
					// (m_block[NUM_BLOCK].m_data[r, 0] % 10 - 3).ToString() +
					// "," +
					// (m_block[NUM_BLOCK].m_data[r, 1] - NUM_CY).ToString());
					// Log.i("你好","m_block[NUM_BLOCK].m_data["+r+"][1] - NUM_CY = "+(m_block[NUM_BLOCK].m_data[r][1]
					// - NUM_CY));
					// Log.i("你好"," (m_block[NUM_BLOCK].m_data["+r+"][ 0] % 10) - 3 = "+(
					// (m_block[NUM_BLOCK].m_data[r][ 0] % 10) - 3));
					m_unitPreview[m_block[NUM_BLOCK].m_data[r][1] - NUM_CY][(m_block[NUM_BLOCK].m_data[r][0] % 10) - 3] = m_block[NUM_BLOCK].m_data[r][0] / 10;
				}
			}
		}

	}

	private void RegisterEffect(int cx, int cy) {
		for (int i = 0; i < m_effect.length; ++i) {
			if (m_effect[i][1] < 0) {
				m_effect[i][0] = cx;// 高位携带有块号
				m_effect[i][1] = cy + 200 * 100;// 高位携带透明度初始200
			}
		}
	}

	/**
	 * 计算分数即消减块
	 */
	private void CalcScore() {
		/*
		 * 消减分2步，先把整行留空再让高处的降落，重复此过程直到没有整行，得分取决一次消减行数和当前总分所计算的基础分 总分越高难度越大
		 */
		long timeStart, timeEnd, test;
		if (m_currentData.m_calcScore == -1)
			return;
		timeStart = System.currentTimeMillis();
		if (m_currentData.m_calcScore % 2 == 0)// 扫描
		{
			boolean flag = false;
			for (int i = 0; i < NUM_CY; ++i) {
				flag = false;
				for (int j = 0; j < NUM_CX; ++j) {
					if (m_unit[i][j] == 0) {
						flag = true;
						break;
					}
				}
				if (!flag)// 准备消减
				{
					m_currentData.m_calcScoreA = i;
					break;
				}
			}
			if (m_currentData.m_calcScoreA == -1)// 未扫描到整行
			{
				m_currentData.m_calcScore = -1;// 未消减标志=-1 既退出
				// m_unit[m_currentData.m_cy_Sharp, m_currentData.m_cx_Sharp %
				// 10] = 0;
				return;
			} else {// 执行消减
				if (DateBaseConfig.GetSwitch().equals("on"))
					m_mediaPlayer[sound.PEOW.ordinal()].start();
				for (int k = 0; k < NUM_CX; ++k) {
					m_unit[m_currentData.m_calcScoreA][k] = 0;
				}
				m_currentData.m_calcScore++;
			}

		} else {
			for (int i = m_currentData.m_calcScoreA + 1; i < NUM_CY; ++i) {
				for (int j = 0; j < NUM_CX; ++j) {
					m_unit[i - 1][j] = m_unit[i][j];
				}
			}
			m_currentData.m_calcScoreA = -1;
			m_currentData.m_calcScore++;
			m_currentData.m_score += m_currentData.m_baseScore
					* m_currentData.m_calcScore / 2;
			m_currentData.m_num++;
			m_currentData.m_interval = 500 - m_currentData.m_num * 2;
			// m_currentData.m_interval = 500 - 300;
			if (m_currentData.m_interval < 100)
				m_currentData.m_interval = 100;
			m_currentData.m_baseScore = 10 + ((int) m_currentData.m_score) / 100;
			m_TextView_Score.setText("Score:" + m_currentData.m_score);
			Rise();
		}
		invalidate();
		timeEnd = System.currentTimeMillis();
		if (m_currentData.m_flag == block.SHAPE1.ordinal()
				&& m_currentData.m_interval < 0)
			test = 15;
		else
			test = NUM_SLEEPA;
		if (timeEnd - timeStart > test) {// 有延迟
			m_mRedrawHandler.sendEmptyMessage(3);
		} else {
			m_mRedrawHandler.sendEmptyMessageDelayed(3, test - timeEnd
					+ timeStart);
		}
	}

	/**
	 * 从底向上抬起块
	 */
	private void Rise() {
		int seed = 0;
		if (m_currentData.m_score - m_currentData.m_riseBaseScore < 100) {
			return;
		} else {
			if (!m_currentData.m_riseFlag) {
				if (m_currentData.m_interval == 15)
					m_mRedrawHandler.sendEmptyMessageDelayed(5, 6000);
				else
					m_mRedrawHandler.sendEmptyMessageDelayed(5,
							m_currentData.m_interval * 6);
				m_currentData.m_riseFlag = true;
				return;
			}
			m_currentData.m_riseFlag=false;
			m_currentData.m_riseBaseScore = m_currentData.m_score;
			for (int i = NUM_CY - 1; i >= 0; --i) {// 向上抬升
				for (int j = 0; j < NUM_CX; ++j) {
					m_unit[i + 1][j] = m_unit[i][j];
				}
			}
			for (int j = 0; j < NUM_CX; ++j) {// 随机产生块填充
				seed = m_random.nextInt(5);
				if (seed <= 2) {
					m_unit[0][j] = m_random.nextInt(12) + 1;
				} else {
					m_unit[0][j] = 0;
				}
			}
			for (int j = 0; j < NUM_CX; ++j) {// 判断是否到顶
				if (m_unit[NUM_CY - 1][j] > 0) {
					SetMode(state.GAMEOVER);
					return;
				}
			}
		}
	}

	/**
	 * 爆炸效果处理
	 */
	private void Explode() {
		long timeStart, timeEnd;
		if (m_currentData.m_expold == -1)
			return;
		timeStart = System.currentTimeMillis();
		if (m_currentData.m_expold == 0) {
			if (DateBaseConfig.GetSwitch().equals("on"))
				m_mediaPlayer[sound.BANG.ordinal()].start();
			if (DateBaseConfig.GetSwitch_v().equals("on")) {
				// long [] pattern = {20,500};
				Vibrator vibrator = (Vibrator) m_MainActivity
						.getSystemService(Service.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
			}
			for (int i = 0; i < 6; ++i) {
				for (int j = 0; j < 6; ++j) {
					if (m_currentData.m_point_Bomb[4] - 1 + i >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j < NUM_CX) {
						m_unit[m_currentData.m_point_Bomb[4] - 1 + i][m_currentData.m_point_Bomb[5]
								- 1 + j] = 0;
					}
				}
			}
			m_unit[m_currentData.m_point_Bomb[4] + 1][m_currentData.m_point_Bomb[5] + 1] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 1][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 2][m_currentData.m_point_Bomb[5] + 1] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 2][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_currentData.m_expold++;
		} else if (m_currentData.m_expold == 1) {
			for (int i = 0; i < 6; ++i) {
				for (int j = 0; j < 6; ++j) {
					if (m_currentData.m_point_Bomb[4] - 1 + i >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j < NUM_CX) {
						m_unit[m_currentData.m_point_Bomb[4] - 1 + i][m_currentData.m_point_Bomb[5]
								- 1 + j] = 0;
					}
				}
			}
			m_unit[m_currentData.m_point_Bomb[4] + 1][m_currentData.m_point_Bomb[5] + 1] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 1][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 2][m_currentData.m_point_Bomb[5] + 1] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 2][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4]][m_currentData.m_point_Bomb[5]] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4]][m_currentData.m_point_Bomb[5] + 3] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 3][m_currentData.m_point_Bomb[5]] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 3][m_currentData.m_point_Bomb[5] + 3] = m_random
					.nextInt(12) + 1;
			m_currentData.m_expold++;
		} else if (m_currentData.m_expold == 2) {
			for (int i = 0; i < 6; ++i) {
				for (int j = 0; j < 6; ++j) {
					if (m_currentData.m_point_Bomb[4] - 1 + i >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j < NUM_CX) {
						m_unit[m_currentData.m_point_Bomb[4] - 1 + i][m_currentData.m_point_Bomb[5]
								- 1 + j] = 0;
					}
				}
			}
			m_unit[m_currentData.m_point_Bomb[4] + 1][m_currentData.m_point_Bomb[5] + 1] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 1][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 2][m_currentData.m_point_Bomb[5] + 1] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 2][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4]][m_currentData.m_point_Bomb[5]] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4]][m_currentData.m_point_Bomb[5] + 3] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 3][m_currentData.m_point_Bomb[5]] = m_random
					.nextInt(12) + 1;
			m_unit[m_currentData.m_point_Bomb[4] + 3][m_currentData.m_point_Bomb[5] + 3] = m_random
					.nextInt(12) + 1;
			if (m_currentData.m_point_Bomb[4] - 1 >= 0
					&& m_currentData.m_point_Bomb[5] - 1 >= 0) {
				m_unit[m_currentData.m_point_Bomb[4] - 1][m_currentData.m_point_Bomb[5] - 1] = m_random
						.nextInt(12) + 1;
				m_unit[m_currentData.m_point_Bomb[4] - 1][m_currentData.m_point_Bomb[5] + 1] = m_random
						.nextInt(12) + 1;
				m_unit[m_currentData.m_point_Bomb[4] - 1][m_currentData.m_point_Bomb[5] + 2] = m_random
						.nextInt(12) + 1;
				m_unit[m_currentData.m_point_Bomb[4] + 4][m_currentData.m_point_Bomb[5] - 1] = m_random
						.nextInt(12) + 1;
			}
			if (m_currentData.m_point_Bomb[4] - 1 >= 0
					&& m_currentData.m_point_Bomb[5] + 4 < NUM_CX) {
				m_unit[m_currentData.m_point_Bomb[4] - 1][m_currentData.m_point_Bomb[5] + 4] = m_random
						.nextInt(12) + 1;
				m_unit[m_currentData.m_point_Bomb[4] + 4][m_currentData.m_point_Bomb[5] + 4] = m_random
						.nextInt(12) + 1;
			}
			m_unit[m_currentData.m_point_Bomb[4] + 4][m_currentData.m_point_Bomb[5] + 2] = m_random
					.nextInt(12) + 1;
			m_currentData.m_expold++;
		} else if (m_currentData.m_expold == 3) {
			for (int i = 0; i < 6; ++i) {
				for (int j = 0; j < 6; ++j) {
					if (m_currentData.m_point_Bomb[4] - 1 + i >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j >= 0
							&& m_currentData.m_point_Bomb[5] - 1 + j < NUM_CX) {
						m_unit[m_currentData.m_point_Bomb[4] - 1 + i][m_currentData.m_point_Bomb[5]
								- 1 + j] = 0;
					}
				}
			}
			m_currentData.m_expold = -1;
			m_lockKey = false;
		}
		invalidate();
		timeEnd = System.currentTimeMillis();
		if (timeEnd - timeStart > NUM_SLEEPA) {// 有延迟
			m_mRedrawHandler.sendEmptyMessage(3);
		} else {
			m_mRedrawHandler.sendEmptyMessageDelayed(3, NUM_SLEEPA - timeEnd
					+ timeStart);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		// m_SCBitmap=Bitmap.createBitmap(m_cxClient, m_cyClient,
		// Config.ARGB_8888);
		// canvas.setBitmap(m_SCBitmap);
		Rect src, dest;
		src = new Rect(0, 0, BLOCK_WIDE, BLOCK_HIGHT);
		if (m_state == state.HOMEPAGE) {
			// Paint mpaint=new Paint();
			// mpaint.setAlpha(100) ;
			for (int i = 0; i < 10; ++i) {
				for (int j = 0; j < 4; ++j) {
					if (m_homePageData[i].m_dataTetris.m_data[j][0] > 0) {
						dest = new Rect(
								m_homePageData[i].m_cx
										+ (m_homePageData[i].m_dataTetris.m_data[j][0] % 10 - m_homePageData[i].m_cx_flag)
										* m_homePageData[i].m_wide,
								m_homePageData[i].m_cy
										- (m_homePageData[i].m_dataTetris.m_data[j][1] - m_homePageData[i].m_cy_flag)
										* m_homePageData[i].m_hight,
								(m_homePageData[i].m_cx + (m_homePageData[i].m_dataTetris.m_data[j][0] % 10 - m_homePageData[i].m_cx_flag)
										* m_homePageData[i].m_wide)
										+ m_homePageData[i].m_wide,
								(m_homePageData[i].m_cy - (m_homePageData[i].m_dataTetris.m_data[j][1] - m_homePageData[i].m_cy_flag)
										* m_homePageData[i].m_hight)
										+ m_homePageData[i].m_hight);
						canvas.drawBitmap(
								m_bitMapBlock[m_homePageData[i].m_dataTetris.m_data[j][0] / 10 - 1],
								src, dest, null);
					}
				}
				if (m_homePageData[i].m_dataTetris.m_flag == block.BOMB
						.ordinal()) {
					// Log.i("你好","OnDraw()--m_state == state.HOMEPAGE---== block.BOMB");
					dest = new Rect(m_homePageData[i].m_cx
							- m_homePageData[i].m_wide, m_homePageData[i].m_cy
							- 2 * m_homePageData[i].m_hight,
							m_homePageData[i].m_cx, m_homePageData[i].m_cy
									- m_homePageData[i].m_hight);
					canvas.drawBitmap(
							m_bitMapBlock[m_homePageData[i].m_point_Bomb[0] - 1],
							src, dest, null);
					dest = new Rect(m_homePageData[i].m_cx
							- m_homePageData[i].m_wide, m_homePageData[i].m_cy
							+ m_homePageData[i].m_hight,
							m_homePageData[i].m_cx, m_homePageData[i].m_cy + 2
									* m_homePageData[i].m_hight);
					canvas.drawBitmap(
							m_bitMapBlock[m_homePageData[i].m_point_Bomb[1] - 1],
							src, dest, null);
					dest = new Rect(m_homePageData[i].m_cx + 2
							* m_homePageData[i].m_wide, m_homePageData[i].m_cy
							- 2 * m_homePageData[i].m_hight,
							m_homePageData[i].m_cx + 3
									* m_homePageData[i].m_wide,
							m_homePageData[i].m_cy - m_homePageData[i].m_hight);
					canvas.drawBitmap(
							m_bitMapBlock[m_homePageData[i].m_point_Bomb[0] - 1],
							src, dest, null);
					dest = new Rect(m_homePageData[i].m_cx + 2
							* m_homePageData[i].m_wide, m_homePageData[i].m_cy
							+ m_homePageData[i].m_hight, m_homePageData[i].m_cx
							+ 3 * m_homePageData[i].m_wide,
							m_homePageData[i].m_cy + 2
									* m_homePageData[i].m_hight);
					canvas.drawBitmap(
							m_bitMapBlock[m_homePageData[i].m_point_Bomb[1] - 1],
							src, dest, null);

				}

			}
			// canvas.drawBitmap(m_SCBitmap,0,0,null);
		} else if (m_state != state.NULL) {// Log.i("你好","onDraw() m_state == state.RUN")
											// ;
			// Log.i("你好", "Run Draw");
			m_currentData.m_previewFlag = !m_currentData.m_previewFlag;
			for (int i = 0; i < 20; ++i)// 绘制每个方格
			{
				for (int k = 0; k < 10; k++) {// m_random.Next(1, 13);
					if (m_unit[i][k] > 0) {
						canvas.drawBitmap(m_bitMapBlock[m_unit[i][k] - 1], k
								* BLOCK_WIDE, (19 - i) * BLOCK_HIGHT, null);
					}
				}
			}
			for (int i = 0; i < 4; ++i)// 绘制活动块
			{
				if (m_currentData.m_data[i][0] > 0
						&& m_currentData.m_data[i][1] < 20) {
					canvas.drawBitmap(
							m_bitMapBlock[m_currentData.m_data[i][0] / 10 - 1],
							m_currentData.m_data[i][0] % 10 * BLOCK_WIDE,
							(19 - m_currentData.m_data[i][1]) * BLOCK_HIGHT,
							null);
				} else {
					break;
				}
			}
			for (int i = 0; i < 4; ++i)// 绘制消隐块
			{
				if (m_currentData.m_X[i][0] > 0 && m_currentData.m_X[i][1] < 20) {
					canvas.drawBitmap(
							m_bitMapBlock[m_currentData.m_X[i][0] / 10 - 1],
							m_currentData.m_X[i][0] % 10 * BLOCK_WIDE,
							(19 - m_currentData.m_X[i][1]) * BLOCK_HIGHT, null);
				}
			}
			if (m_currentData.m_flag == block.BOMB.ordinal()) {
				canvas.drawBitmap(
						m_bitMapBlock[m_currentData.m_point_Bomb_A[0] - 1],
						m_currentData.m_data[0][0] % 10 * BLOCK_WIDE
								- BLOCK_WIDE,
						(19 - m_currentData.m_data[0][1] + 1) * BLOCK_HIGHT,
						null);
				canvas.drawBitmap(
						m_bitMapBlock[m_currentData.m_point_Bomb_A[1] - 1],
						m_currentData.m_data[0][0] % 10 * BLOCK_WIDE
								+ BLOCK_WIDE * 2,
						(19 - m_currentData.m_data[0][1] + 1) * BLOCK_HIGHT,
						null);
				canvas.drawBitmap(
						m_bitMapBlock[m_currentData.m_point_Bomb_A[2] - 1],
						m_currentData.m_data[0][0] % 10 * BLOCK_WIDE
								- BLOCK_WIDE,
						(19 - m_currentData.m_data[0][1] - 2) * BLOCK_HIGHT,
						null);
				canvas.drawBitmap(
						m_bitMapBlock[m_currentData.m_point_Bomb_A[3] - 1],
						m_currentData.m_data[0][0] % 10 * BLOCK_WIDE
								+ BLOCK_WIDE * 2,
						(19 - m_currentData.m_data[0][1] - 2) * BLOCK_HIGHT,
						null);
			}
			if ((m_block[NUM_BLOCK].m_flag < block.STRIPX.ordinal() && m_block[NUM_BLOCK].m_flag != block.SHAPE1
					.ordinal()) || m_currentData.m_previewFlag) {
				for (int i = 0; i < 4; ++i)// 绘制预览区
				{
					for (int j = 0; j < 4; ++j) {
						if (m_unitPreview[i][j] > 0) {
							dest = new Rect(7 * BLOCK_WIDE + BLOCK_WIDE / 2 + j
									* BLOCK_WIDE / 2, BLOCK_HIGHT * 3
									- BLOCK_HIGHT / 2 - i * BLOCK_HIGHT / 2, 7
									* BLOCK_WIDE + BLOCK_WIDE / 2 + j
									* BLOCK_WIDE / 2 + BLOCK_WIDE / 2,
									BLOCK_HIGHT * 3 - BLOCK_HIGHT / 2 - i
											* BLOCK_HIGHT / 2 + BLOCK_HIGHT / 2);
							canvas.drawBitmap(
									m_bitMapBlock[m_unitPreview[i][j] - 1],
									src, dest, null);
						}
					}
				}
			}
			if (m_currentData.m_cx > 0) {
				if (m_currentData.m_flag == block.SHAPE2.ordinal())
					canvas.drawBitmap(
							m_bitMapBlock[m_currentData.m_cx / 10 - 1],
							m_currentData.m_cx % 10 * BLOCK_WIDE,
							(19 - m_currentData.m_cy) * BLOCK_HIGHT, null);
				else
					canvas.drawBitmap(
							m_bitMapBlock[m_currentData.m_cx / 10 - 1],
							m_currentData.m_cx % 10 * BLOCK_WIDE,
							(18 - m_currentData.m_cy) * BLOCK_HIGHT, null);
			}
			if (DateBaseConfig.GetSwitch_e().equals("on"))// 绘制特效
			{
				Paint mpaint;
				for (int pp = 0; pp < m_effect.length; ++pp) {
					if (m_effect[pp][1] > 0) {
						mpaint = new Paint();
						mpaint.setAlpha(m_effect[pp][1] / 100);
						canvas.drawBitmap(
								m_bitMapBlock[m_effect[pp][0] / 10 - 1],
								m_effect[pp][0] % 10 * BLOCK_WIDE,
								(19 - m_effect[pp][1] % 100) * BLOCK_HIGHT,
								mpaint);
					}
				}
			}
		}
		super.onDraw(canvas);
	}

}
