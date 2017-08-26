package ptool.datacase.util;

import android.os.Environment;

public class Constant {
	public static final String IMAGE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/PToolDataCase/Icons/";
 
	 
	//-------------------------
 
	public static final String FLAG_YES = "是";
	public static final String FLAG_NO = "否";
	public static final String FLAG_EMPTY= "空";
	 
	
	public static final String UNIT_YEAR = "年";
	public static final String UNIT_MONTH = "月";
	public static final String UNIT_WEEK = "周";
	public static final String UNIT_DAY = "日";
	public static final String UNIT_HOUR = "时";
	public static final String UNIT_MINUTE = "分";
 
	//-------------------------
	public static final int STATE_NEW = 0;// 新增状态
	public static final int STATE_MOIFY = 1;// 编辑状态
	public static final int STATE_OTHER = 2;// 其他状态

	public static final int REQ_ABOUT_US = 0;
	public static final int REQ_HELP_WEB = 1;
	public static final int REQ_NEW_VERSION_WEB = 2;
	public static final int REQ_DATA_NOTE = 3;
	public static final int REQ_DATA_LIST = 4;
	public static final int REQ_CASE_TYPE = 5;
	public static final int REQ_SYSCONFIG = 6;
	public static final int REQ_CHAR_EDIT = 7;
	public static final int REQ_VALUE_LIST= 8;
	public static final int REQ_VALUE_EDIT = 9;
}
