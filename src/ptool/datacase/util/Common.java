package ptool.datacase.util;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ptool.datacase.R;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Common {

	/**
	 * �����뷨
	 * 
	 * @param act
	 */
	public static void showInputMethod(final Activity act) {
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) act
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500);
	}

	/**
	 * �ر����뷨
	 * 
	 * @param act
	 */
	public static void closeInputMethod(final Activity act) {
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) act
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				// if (imm.isActive()) { //��Ч
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				// }
			}
		}, 500);
	}


	/**
	 * ��ȡ��Ļ����
	 */
	public static void getAssetData(Activity act, SystemConfig app) {
		try {
			PackageManager manager = act.getPackageManager();
			PackageInfo info = manager.getPackageInfo(act.getPackageName(), 0);

			app.setVersion(info.versionName); // �汾��

			DisplayMetrics dm = new DisplayMetrics();
			dm = act.getResources().getDisplayMetrics();
			app.setDensity(dm.density); //
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
 
	/**
	 * �����ɫ��ʾ
	 * 
	 * @param act
	 */
	public static void setGray(Activity act) {
		WindowManager.LayoutParams lp = act.getWindow().getAttributes();
		lp.alpha = 0.3f;
		lp.dimAmount = 0.3f;
		act.getWindow().setAttributes(lp);
	}

	/**
	 * ����������ʾ
	 * 
	 * @param act
	 */
	public static void setNormal(Activity act) {
		WindowManager.LayoutParams lp = act.getWindow().getAttributes();
		lp.alpha = 1f;
		lp.dimAmount = 1f;
		act.getWindow().setAttributes(lp);
	}

	/**
	 * ��ʾ��ʾ��Ϣ
	 * 
	 * @param act
	 * @param text
	 * @param duration
	 * @param image
	 */
	public static void showMessage(Activity act, String text, int duration,
			int image) {
		showMessage(act, text, duration, image, Gravity.CENTER);
	}

	/**
	 * ��ʾ��ʾ��Ϣ
	 * 
	 * @param act
	 * @param text
	 * @param duration
	 * @param image
	 * @param gravity
	 */
	public static void showMessage(Activity act, String text, int duration,
			int image, int gravity) {
		Toast toast = Toast.makeText(act, "   " + text, duration);
		toast.setGravity(gravity, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageCodeProject = new ImageView(act);
		imageCodeProject.setImageResource(image);

		toastView.setOrientation(LinearLayout.HORIZONTAL);
		toastView.setGravity(Gravity.CENTER_VERTICAL);
		toastView.addView(imageCodeProject, 0);
		toast.show();
	}

	/**
	 * ��ʾ��������Ϣ
	 * 
	 * @param act
	 * @param titleInt
	 * @param message
	 * @param iconInt
	 */
	public static void alert(Activity act, int titleInt, String message,
			int iconInt) {
		try {
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleInt);
			builder.setMessage(message);
			builder.setIcon(iconInt);

			builder.setNegativeButton(R.string.close, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʾ��������Ϣ
	 * 
	 * @param act
	 * @param titleInt
	 * @param message
	 * @param iconInt
	 */
	public static void alert(Activity act, int titleInt, int messageInt,
			int iconInt) {
		try {
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleInt);
			builder.setMessage(messageInt);
			builder.setIcon(iconInt);

			builder.setNegativeButton(R.string.close, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �������ڸ�ʽ������2012��3��22��[����]
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateSpeaking(Calendar cd) {
		Calendar cdNow = Calendar.getInstance();

		String result = "";
		if (getInterval(cd, cdNow, 3) == 0) {
			result = "[����]";
		} else if (getInterval(cd, cdNow, 3) == 1) {
			result = "[����]";
		} else if (getInterval(cd, cdNow, 3) == 2) {
			result = "[ǰ��]";
		} else if (getInterval(cd, cdNow, 3) == -1) {
			result = "[����]";
		} else if (getInterval(cd, cdNow, 3) == -2) {
			result = "[����]";
		}
		return getDateStringShort(cd) + result;
	}

	/**
	 * �������ڸ�ʽ������2012��3��22��[����]
	 * 
	 * @param val
	 * @return
	 */
	public static String getDateSpeaking(String val) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			Date dt = new Date();
			val = val.replace(Constant.UNIT_MONTH, "-");
			val = val.replace(Constant.UNIT_DAY, "");
			val = val.replace(Constant.UNIT_YEAR, "-").trim();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(val.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (val.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(val);
			cd.setTime(dt);
		} catch (Exception e) {
			Log.e("getDateSpeaking(),String to Date", val + " Convert failed");
		}
		return getDateSpeaking(cd);
	}

	/**
	 * �������ڸ�ʽ������2012��3��22��[����],2012��3��22��[����]
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateDistance(Calendar cd) {
		Calendar cdNow = Calendar.getInstance();

		String result = "";
		if (getDateInt(cdNow) == getDateInt(cd)) {
			result = "[����]";
		} else if (getInterval(cd, cdNow, 3) == 1) {
			result = "[����]";
		} else if (getInterval(cd, cdNow, 3) == 2) {
			result = "[ǰ��]";
		} else if (getInterval(cd, cdNow, 3) == -1) {
			result = "[����]";
		} else if (getInterval(cd, cdNow, 3) == -2) {
			result = "[����]";
		} else {
			if (getInterval(cdNow, cd, 3) > 0)
				result = "[" + (getInterval(cdNow, cd, 3)) + "��]";
		}
		return getDateStringShort(cd) + result;
	}

	/**
	 * �������ڸ�ʽ������2012��3��22��[����],2012��3��22��[����]
	 * 
	 * @param val
	 * @return
	 */
	public static String getDateDistance(String val) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			Date dt = new Date();
			val = val.replace(Constant.UNIT_MONTH, "-");
			val = val.replace(Constant.UNIT_DAY, "");
			val = val.replace(Constant.UNIT_YEAR, "-").trim();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(val.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (val.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(val);
			cd.setTime(dt);
		} catch (Exception e) {
			Log.e("getDateDistance(),String to Date", val + " Convert failed");
		}
		return getDateSpeaking(cd);
	}

	/**
	 * ��õ�ǰ�����ַ���, 2011��2��2��
	 * 
	 * @return
	 */
	public static String getNowDateString() {
		Calendar cd = Calendar.getInstance();
		int y = cd.get(Calendar.YEAR);
		int m = cd.get(Calendar.MONTH) + 1;
		int d = cd.get(Calendar.DAY_OF_MONTH);
		return y + Constant.UNIT_YEAR + m + Constant.UNIT_MONTH + d + "��";
	}

	/**
	 * ��ȡ�������ڵ��ַ�����2011��9��6�� 20:37
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateString(Calendar cd) {
		int y = cd.get(Calendar.YEAR);
		int m = cd.get(Calendar.MONTH) + 1;
		int d = cd.get(Calendar.DAY_OF_MONTH);
		int h = cd.get(Calendar.HOUR_OF_DAY);
		int mi = cd.get(Calendar.MINUTE);
		return y + Constant.UNIT_YEAR + m + Constant.UNIT_MONTH + d
				+ Constant.UNIT_DAY + h + ":" + mi;
	}

	/**
	 * ��ȡ�������ڵ��ַ�����2011��9��6��
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateStringShort(Calendar cd) {
		int y = cd.get(Calendar.YEAR);
		int m = cd.get(Calendar.MONTH) + 1;
		int d = cd.get(Calendar.DAY_OF_MONTH);
		return y + Constant.UNIT_YEAR + m + Constant.UNIT_MONTH + d
				+ Constant.UNIT_DAY;
	}

	/**
	 * ��ȡ���� 2��2��
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateStringVeryShort(Calendar cd) {
		int m = cd.get(Calendar.MONTH) + 1;
		int d = cd.get(Calendar.DAY_OF_MONTH);
		return m + Constant.UNIT_MONTH + d + Constant.UNIT_DAY;
	}

	/**
	 * ��ȡ�������ڵ��ַ�����yyyy-MM-dd
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateSqlString(Calendar cd) {
		java.text.DateFormat ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
		String formatTime = ft.format(cd.getTime());
		return formatTime;
	}

	/**
	 * ��ȡ�������ڵ��ַ�����yyyy-MM-dd 00:00:00
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateNoteString(Calendar cd) {
		java.text.DateFormat ft = new java.text.SimpleDateFormat(
				"yyyy-MM-dd 00:00:00");
		String formatTime = ft.format(cd.getTime());
		return formatTime;
	}

	/**
	 * ����ת��Ϊint ����
	 * 
	 * @param cd
	 * @return
	 */
	public static int getDateInt(Calendar cd) {
		java.text.DateFormat ft = new java.text.SimpleDateFormat("yyyyMMdd");
		String formatTime = ft.format(cd.getTime());

		return parseInt(formatTime);
	}

	/**
	 * ʱ��ת��Ϊ����
	 * 
	 * @param cd
	 * @return
	 */
	public static int getTimeInt(Calendar cd) {
		java.text.DateFormat ft = new java.text.SimpleDateFormat("HHmm");
		String formatTime = ft.format(cd.getTime());

		return parseInt(formatTime);
	}

	/**
	 * ��ȡ�������ڵ��ַ�����2��2��
	 * 
	 * @param val
	 * @return
	 */
	public static String getDateStringVeryShort(String val) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			Date dt = new Date();
			val = val.replace(Constant.UNIT_MONTH, "-");
			val = val.replace(Constant.UNIT_DAY, "");
			val = val.replace(Constant.UNIT_YEAR, "-").trim();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(val.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (val.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(val);
			cd.setTime(dt);
		} catch (Exception e) {
			Log.e("getDateStringVeryShort(),String to Date", val
					+ " Convert failed");
		}
		return getDateStringVeryShort(cd);
	}

	/**
	 * ��ȡ�������ڵ��ַ��� 2011��9��6��
	 * 
	 * @param val
	 * @return
	 */
	public static String getDateStringShort(String val) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			val = val.replace(Constant.UNIT_MONTH, "-");
			val = val.replace(Constant.UNIT_DAY, "");
			val = val.replace(Constant.UNIT_YEAR, "-").trim();
			Date dt = new Date();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(val.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (val.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(val);
			cd.setTime(dt);
			return getDateStringShort(cd);
		} catch (Exception e) {
			Log.e("getDateStringShort(),String to Date", val
					+ " Convert failed");
			return val;
		}
		
	}

	/**
	 * ��ȡ�������ڵ��ַ�����yyyy-MM-dd
	 * 
	 * @param dt
	 * @return
	 */
	public static String getDateSqlString(Date dt) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(dt);

		return getDateSqlString(cd);
	}

	/**
	 * ��ȡ�������ڵ��ַ�����yyyy-MM-dd
	 * 
	 * @param val
	 * @return
	 */
	public static String getDateSqlString(String val) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			Date dt = new Date();
			val = val.replace(Constant.UNIT_MONTH, "-");
			val = val.replace(Constant.UNIT_DAY, "");
			val = val.replace(Constant.UNIT_YEAR, "-").trim();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(val.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (val.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(val);
			cd.setTime(dt);
		} catch (Exception e) {
			Log.e("getDateSqlString(),String to Date", val + " Convert failed");
		}
		return getDateSqlString(cd);
	}

	/**
	 * �������ַ�����ת��Ϊ��׼�����ַ��� yyyy-MM-dd HH:mm:ss
	 * 
	 * @param str
	 * @return
	 */
	public static String getDateString(String str) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			Date dt = new Date();
			str = str.replace(Constant.UNIT_DAY, "");
			str = str.replace(Constant.UNIT_MONTH, "-");
			 str = str.replace(Constant.UNIT_YEAR, "-").trim();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(str.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (str.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(str);
			cd.setTime(dt);
			return getDateString(cd); 
		} catch (Exception e) {
			Log.e("getDateString(),String to Date", str + " Convert failed");
			return str;
		}
		
	}

	/**
	 * ���ַ���ת��Ϊ Calendar
	 * 
	 * @param str
	 * @return
	 */
	public static Calendar getCalendarFromString(String str) {
		Calendar cd = Calendar.getInstance();
		cd.clear();
		try {
			Date dt = new Date();
			str = str.replace(Constant.UNIT_DAY, "");
			str = str.replace(Constant.UNIT_MONTH, "-");
			str = str.replace(Constant.UNIT_YEAR, "-").trim();
			java.text.DateFormat ft = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			if(str.contains("T"))
			{
				 ft = new java.text.SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss");
			}
			if (str.length() <= 10)
				ft = new java.text.SimpleDateFormat("yyyy-MM-dd");
			dt = ft.parse(str);
			cd.setTime(dt);
		} catch (Exception e) {
			Log.e("getCalendarFromString(),String to Date", str
					+ " Convert failed");
		}
		return cd;
	}

	/**
	 * ���ϵͳʱ�� yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getSysNowTime() {
		Date now = new Date();
		java.text.DateFormat ft = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String formatTime = ft.format(now);
		return formatTime;
	}

	/**
	 * ��������ַ��� yyyy-MM-dd HH:mm:ss
	 * 
	 * @param cd
	 * @return
	 */
	public static String getDateTime(Calendar cd) {
		java.text.DateFormat ft = new java.text.SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		String formatTime = ft.format(cd.getTime());
		return formatTime;
	}
 
	/**
	 * ��������ַ��� yyyy-MM-dd HH:mm:ss
	 * 
	 * @param val
	 * @return
	 */
	public static String getDateTime(String val) {
		java.text.DateFormat ft = new java.text.SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		 
		String formatTime = ft.format(val);
		return formatTime;
	}

	/**
	 * �ж��Ƿ�Ϊ���� INT
	 * 
	 * @param val
	 * @return
	 */
	public static Boolean isInt(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * �ж��Ƿ�ΪDouble
	 * 
	 * @param val
	 * @return
	 */
	public static Boolean isDouble(String val) {
		try {
			Double.parseDouble(val);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * �ַ���ת��Ϊ double
	 * 
	 * @param val
	 * @return
	 */
	public static double parseDouble(String val) {
		val = val.replace(",", "");
		if (val.equals(""))
			val = "0";
		return Double.parseDouble(val);
	}

	/**
	 * �ַ���ת��Ϊ int
	 * 
	 * @param val
	 * @return
	 */
	public static int parseInt(String val) {
		double d = parseDouble(val);
		return (int) Math.round(d);
	}

	public static String covertToDouble(String val,int decimal) {
		try {
			val = val.replace(",", "");
			return covertToDouble(Double.parseDouble(val),decimal);
		} catch (Exception e) {
			return val;
		}
	}
	
	public static String covertToDouble(Double val,int decimal) {
		String fmt = "#,##0";
		if(decimal > 0)
		{
			fmt = fmt + ".";
			for(int i = 0; i < decimal; i ++)
			{
				fmt = fmt + "0";
			}
		}
		DecimalFormat df = new DecimalFormat(fmt);
		return df.format(val);
	}
	
	public static double covertToDoubleValue(Double val,int decimal) {
		String fmt = "#,##0";
		if(decimal > 0)
		{
			fmt = fmt + ".";
			for(int i = 0; i < decimal; i ++)
			{
				fmt = fmt + "0";
			}
		}
		DecimalFormat df = new DecimalFormat(fmt);
		return parseDouble(df.format(val));
	}
	
	/**
	 * ת��Ϊ�����ʾ
	 * 
	 * @param val
	 * @return
	 */
	public static String covertToCash(String val) {
		try {
			val = val.replace(",", "");
			return covertToCash(Double.parseDouble(val));
		} catch (Exception e) {
			return val;
		}
	}

	/**
	 * ת��Ϊ�����ʾ
	 * 
	 * @param val
	 * @return
	 */
	public static String covertToCash(Double val) {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(val);
	}

	/***
	 * ����2ΪС��
	 * 
	 * @param d
	 * @return
	 */
	public static double getRound2(double d) {
		return (double) (Math.round(d * 100)) / 100;
	}

	/***
	 * �ٷֱ���ʾ
	 * 
	 * @param d
	 * @return
	 */
	public static String getPercent(double d) {
		return (double) (Math.round(d * 100 * 100)) / 100 + "%";
	}

	/**
	 * ������һ����һ
	 * 
	 * @param dateValue
	 * @return
	 */
	public static Calendar getRecentMonday(Calendar dateValue) {
		if (dateValue.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
			return dateValue;
		} else {
			Calendar temp = (Calendar) dateValue.clone();
			temp.add(Calendar.DATE, -1);
			return getRecentMonday(temp);
		}
	}

	/**
	 * ���֮��һ������
	 * 
	 * @param dateValue
	 * @return
	 */
	public static Calendar getNextSunday(Calendar dateValue) {
		if (dateValue.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return dateValue;
		} else {
			Calendar temp = (Calendar) dateValue.clone();
			temp.add(Calendar.DATE, 1);
			return getNextSunday(temp);
		}
	}

	/**
	 * ����������ڵ��µĵ�һ��
	 * 
	 * @param dateValue
	 * @return
	 */
	public static Calendar getFirstDayOfMonth(Calendar dateValue) {
		Calendar temp = (Calendar) dateValue.clone();
		temp.add(Calendar.DATE, 1 - dateValue.get(Calendar.DAY_OF_MONTH));
		return temp;
	}

	/**
	 * ����������ڵ��µ����һ��
	 * 
	 * @param dateValue
	 * @return
	 */
	public static Calendar getLastDayOfMonth(Calendar dateValue) {
		Calendar calendar = (Calendar) dateValue.clone();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar;
	}
 

	public static Bundle getIntentExtras(Intent data) {
		// data is null, when activity cancelled by BACK BUTTON
		if (data != null) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				return extras;
			}
		}
		return null;
	}

	/**
	 * �������ݷ����仯�ı��
	 * 
	 * @param act
	 */
	public static void setDataChangedFlag(Activity act) {
		Bundle bundle = new Bundle();
		bundle.putString("DataChanged", "Y");
		Intent intent = new Intent();
		intent.putExtras(bundle);
		act.setResult(-1, intent);
	}

	/**
	 * MD5 �����㷨
	 * 
	 * @param val
	 * @return
	 */
	public static String MD5(String val) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());
			byte[] m = md5.digest();
			return toHexString(m);
		} catch (Exception ex) {
			Log.e("MD5", "MD5���ܼ���ʧ�ܣ�" + ex.getMessage());
			return "";
		}
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[(b[i]) & 0x0f]);
		}
		return sb.toString();
	}

	 
	public static Calendar GetDateByValue(int year, int month, int date) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.set(Calendar.YEAR, year);
		localCalendar.set(Calendar.MONTH, month);
		localCalendar.set(Calendar.DATE, date);
		return localCalendar;
	}

	public static String covertToCashInt(Integer paramInteger) {
		return new DecimalFormat("#,##0").format(paramInteger);
	}

	public static String covertToCashInt(String paramString) {
		return covertToCashInt(Integer.valueOf((int) Double
				.parseDouble(paramString.replace(",", ""))));
	}

	public static String getWeek(int weekInt) {
		String val = "Error";
		switch (weekInt) {
		case Calendar.SUNDAY:
			val = "����";
			break;
		case Calendar.SATURDAY:
			val = "����";
			break;
		case Calendar.MONDAY:
			val = "��һ";
			break;
		case Calendar.TUESDAY:
			val = "�ܶ�";
			break;
		case Calendar.WEDNESDAY:
			val = "����";
			break;
		case Calendar.THURSDAY:
			val = "����";
			break;
		case Calendar.FRIDAY:
			val = "����";
			break;
		}

		return val;
	}
 
	public static int getInterval(Calendar cdBegin, Calendar deEnd, int f) {
		int hours = 0;
		try {
			Date beginDate = cdBegin.getTime();
			Date endDate = deEnd.getTime();
			double millisecond = endDate.getTime() - beginDate.getTime(); // ��������õ����ڲ�X(��λ:����)
			/**
			 * 1�� = 1000���� millisecond/1000 --> �� millisecond/1000*60 - > ����
			 * millisecond/(1000*60*60) -- > Сʱ millisecond/(1000*60*60*24) -->
			 * ��
			 * */
			switch (f) {
			case 0: // second
				return Math.abs((int) (millisecond / 1000));
			case 1: // minute
				return Math.abs((int) (millisecond / (1000 * 60)));
			case 2: // hour
				return Math.abs((int) (millisecond / (1000 * 60 * 60)));
			case 3: // day
				double intval = (millisecond / (1000 * 60 * 60 * 24));
				return (int) Math.round(intval);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hours;
	}
 
	public static String listToString(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) != null && list.get(i) != "") {
				sb.append(list.get(i)).append(separator);
			}
		}
		String val = sb.toString();
		if (val.length() > 1) {
			val = val.substring(0, sb.toString().length() - 1);
		}
		return val;
	}
	
	/**
	 * ��ȡͼ��
	 * 
	 * @param iconName
	 * @return
	 */
	public static Drawable getImage(String iconName) {
		try {
			Drawable d = Drawable.createFromPath(Constant.IMAGE_PATH
					+ iconName);
			return d;
		} catch (Exception ex) {
			Log.e("getImage", iconName + "ͼ���ȡʧ�ܣ�" + ex.getMessage());
			return null;
		}
	}
	
	public static Drawable getDefaultImage(Context act) {
		try {
				return act.getResources()
						.getDrawable(R.drawable.db_case);
 
		} catch (Exception ex) {
			Log.e("getDefaultImage",  "ͼ���ȡʧ�ܣ�" + ex.getMessage());
			return null;
		}
	}
}
