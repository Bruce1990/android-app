package com.example.hello;
/**
 * 基本数据信息配置
 * @author Administrator
 *
 */
public class DateBaseConfig {
	private static final String m_DataBaseName = "infDateBase";
	private static final int m_Version = 1;
	private static final String m_mode="girl";
	private static final String m_switch="on";
	public static final String SWITCH_ON="on";
	public static final String SWITCH_OFF="off";
	private static String m_curMode="girl";
	private static String m_curSwitch="on";
	private static String m_curSwitch_v="on";
	private static String m_curSwitch_e="on" ;
	public static String GetDataBaseName() {
		return m_DataBaseName;
	}

	public static int GetVersion() {
		return m_Version;
	}
	
	public static String GetMode()
	{
		return m_curMode;
	}
	
	public static String GetSwitch()
	{
		return m_curSwitch;
	}
	
	public static String GetDefMode()
	{
		return m_mode;
	}
	
	public static String GetDefSwitch()
	{
		return m_switch;
	}
	
	public static void SetMode(String mode)
	{
		m_curMode=mode;
	}
	
	public static void SetSwitch(String swi)
	{
		m_curSwitch=swi;
	}
	
	public static void SetSwitch_v(String swi_v)
	{
		m_curSwitch_v=swi_v;
	}
	
	public static void SetSwitch_e(String swi_e)
	{
		m_curSwitch_e=swi_e;
	}
	
	public static String GetDefSwitch_v()
	{
		return m_switch;
	}
	
	public static String GetDefSwitch_e()
	{
		return m_switch;
	}
	
	public static String GetSwitch_v()
	{
		return m_curSwitch_v;
	}
	
	public static String GetSwitch_e()
	{
		return m_curSwitch_e;
	}
}
