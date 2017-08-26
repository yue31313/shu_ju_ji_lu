package ptool.datacase.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.data.service.SysPassService;
import ptool.datacase.util.Common;
import ptool.datacase.util.SystemConfig;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataCommon {

	/**
	 * 监测数据库是否已经初始化。确保数据库存在！
	 * 
	 * @param context
	 * @param app
	 */
	public static void checkDatabase(Context context, SystemConfig app) {
		try {
			//判断SDK
			// 获得an_sysdb.db文件的绝对路径
			String databaseFilename = DatabaseConstant.DATABASE_PATH
					+ DatabaseConstant.DATABASE_NAME;
			File dir = new File(DatabaseConstant.DATABASE_PATH);
			// 如果/sdcard/PToolAccount目录中存在，创建这个目录
			if (!dir.exists()) {
				dir.mkdir();
				Log.i("DataBase Init", "数据库目录创建完毕" + dir.getAbsolutePath());
			}
			if (!(new File(databaseFilename)).exists()) {
				InputStream is = context.getResources().openRawResource(
						R.raw.an_db);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制an_sysdb.db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				Log.i("DataBase Init", "数据库初始化完毕！");
			} else {
				// 版本检测
				SysPassService sysPassService = new SysPassService(context);
				int version = Common.parseInt(sysPassService
						.getValue("version"));
				if (version < app.getDatabaseVersion()) {
					// 升级数据库
					 
				}
			}
		} catch (Exception e) {
			Log.e("DataBase Init", "数据库无法初始化，错误信息：" + e.getMessage());
		}
	}
  
	/**
	 * 文件复制
	 * 
	 * @param oldPath
	 *            源文件路径
	 * @param newPath
	 *            目标文件路径
	 * @throws IOException
	 */
	public static void copyFile(String oldPath, String newPath)
			throws IOException {
		// int bytesum = 0;
		int byteread = 0;
		File oldfile = new File(oldPath);
		if (oldfile.exists()) {
			InputStream inStream = new FileInputStream(oldPath); // 读入原文件
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[4096];
			// int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				// bytesum += byteread; // 字节数 文件大小
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
		}
		Log.i("DataCommon", oldPath + "文件成功复制到" + newPath);
	}

	/**
	 * 备份数据库文件
	 * 
	 * @param paramContext
	 */
	public static void backUpDatabase(Context paramContext) {
		SysPassService localSysPassService;
		try {
			localSysPassService = new SysPassService(paramContext);
			if (!(localSysPassService.getValue("auto_backup").equals("N"))) {
				String filePath = DatabaseConstant.DATABASE_PATH
						+ DatabaseConstant.DATABASE_NAME;
				// 确保备份目录存在
				File backupDir = new File(DatabaseConstant.DATABASE_BACKUP_PATH);
				if (!(backupDir.exists()))
					backupDir.mkdir();

				String backupPath = DatabaseConstant.DATABASE_BACKUP_PATH
						+ Common.getNowDateString()
						+ DatabaseConstant.DATABASE_BACKUP_NAME;
				// 如当天的备份已经存在，则删除该文件
				File backupFile = new File(backupPath);
				if (backupFile.exists())
					backupFile.delete();
				// 生成当天的备份
				copyFile(filePath, backupPath);

				int nums = 10;
				String backupNumStr = localSysPassService
						.getValue("auto_backup_nums");
				if (!(backupNumStr.equals("")))
					nums = Integer.parseInt(backupNumStr);

				if (backupDir.listFiles().length > nums) {
					File[] items = backupDir.listFiles();
					items = fileMaopao(items); // 冒泡排序
					for (int i = nums; i < items.length; i++) {
						items[i].delete();
					}
				}
				Log.i("DataCommon", "数据库文件备份成功！");
			}
			//强制删除上传的备份文件------------------------------------
			String key = localSysPassService.getValue("cloud_mail") +"~"+ localSysPassService.getValue("cloud_password").toUpperCase();
			String newFileName = DatabaseConstant.DATABASE_PATH + key
					+ ".apk";
			// 删除该文件
			File backupFile = new File(newFileName);
			if (backupFile.exists())
				backupFile.delete();
			//-----------------------------------------------------------------
		} catch (Exception e) {
			Log.e("DataCommon", "数据库文件备份失败，" + e.getMessage());
		}
	}

	/**
	 * 恢复数据库文件
	 * 
	 * @param fileName
	 */
	public static void resumeDatabase(String fileName) {
		try {
			String fileTo = DatabaseConstant.DATABASE_PATH
					+ DatabaseConstant.DATABASE_NAME;
			String fileFrom = DatabaseConstant.DATABASE_BACKUP_PATH + fileName;
			File localFile = new File(fileTo);
			if (localFile.exists())
				localFile.delete();
			copyFile(fileFrom, fileTo);
		} catch (Exception localException) {
			Log.e("DataCommon", "数据库恢复失败，备份文件名：" + fileName);
		}
	}

	/**
	 * 获得备份文件列表
	 * 
	 * @param fileList
	 */
	public static void getBackupFilesData(List<Map<String, Object>> fileList) {
		fileList.clear();
		File localFile = new File(DatabaseConstant.DATABASE_BACKUP_PATH);
		if ((localFile.exists()) && (localFile.listFiles().length > 0)) {
			File[] arrayOfFile = fileMaopao(localFile.listFiles());
			for (int i = 0; i < arrayOfFile.length; i++) {
				if (arrayOfFile[i].getName().contains(DatabaseConstant.DATABASE_BACKUP_NAME)) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ListItemImage", R.drawable.file);
					map.put("ListItemTitle", arrayOfFile[i].getName());
					// map.put("ListItemId", arrayOfFile[i].getName());
					fileList.add(map);
				}
			}
		}
	}

	/**
	 * 文件按冒泡排序
	 * 
	 * @param items
	 * @return
	 */
	public static File[] fileMaopao(File[] items) {
		boolean flag; // 设一个標志
		for (int i = 0; i < items.length; i++) {
			flag = false;
			for (int j = 0; j < items.length - 1 - i; j++) {
				if (items[j].lastModified() < items[j + 1].lastModified()) {
					File temp = items[j];
					items[j] = items[j + 1];
					items[j + 1] = temp;
					flag = true; // 標志设为true
				}
			}
			if (flag == false)
				i = items.length;// 没发生交换，表明当前已经有序，直接返回
		}
		return items;
	}

	public static String setSqlPart(String[] sels, String parm) {
		String sql = "";
		String value = "";
		if (sels.length > 1 || (sels.length == 1 && sels[0] != "")) {
			sql = sql + " and ( ";
			for (int j = 0; j < sels.length; j++) {
				value = sels[j].trim();
				if (value.equals("空"))
					value = "";
				if (j == 0) {
					sql = sql + parm + " = '" + value + "'";
				} else {
					sql = sql + " or " + parm + " = '" + value + "'";
				}
			}
			sql = sql + ")";
		}
		return sql;
	}

	public static String setSqlPartLike(String[] sels, String parm) {
		String sql = "";
		if (sels.length > 1 || (sels.length == 1 && sels[0] != "")) {
			sql = sql + " and ( ";
			for (int j = 0; j < sels.length; j++) {
				if (sels[j].trim() != "") {
					if (j == 0) {
						sql = sql + parm + " like '%" + sels[j].trim() + "%'";
					} else {
						sql = sql + " or " + parm + " like '%" + sels[j].trim()
								+ "%'";
					}
				}
			}
			sql = sql + ")";
		}
		return sql;
	}

	public static void VACUUMDB(Context context) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase db = databaseHelper.openDatabase();
		try
		{
			db.execSQL("VACUUM");
		} catch (Exception ex) {
			Log.e("VACUUMDB()", "压缩数据库失败！" + ex.getMessage());
		}  finally {
			db.close();
		}
	}
	
	public static Boolean getNewDB(Context context) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase db = databaseHelper.openDatabase();
		db.beginTransaction();
		try {
			String sql = "delete from dbcase_chars";
			db.execSQL(sql);

			sql = "delete from dbcase_chars_math";
			db.execSQL(sql);

			sql = "delete from dbcase_data";
			db.execSQL(sql);

			sql = "delete from dbcase_datatype";
			db.execSQL(sql);

			sql = "delete from dbcase_type";
			db.execSQL(sql);

			sql = "delete from dbcase_values";
			db.execSQL(sql);
 
			db.setTransactionSuccessful();
			return true;
		} catch (Exception ex) {
			Log.e("getNewDB()", "清除数据库失败！" + ex.getMessage());
			return false;
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	 
	/**
	 * 根据键值，获取配置参数
	 * 
	 * @param act
	 * @param type
	 * @return
	 */
	public static String getConfigValue(Activity act, String type) {
		SharedPreferences settings = act.getSharedPreferences(
				DatabaseConstant.PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getString(type, "");
	}

	/**
	 * 保存配置参数
	 * 
	 * @param act
	 * @param type
	 * @param value
	 */
	public static void setConfigValue(Activity act, String type, String value) {
		SharedPreferences settings = act.getSharedPreferences(
				DatabaseConstant.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(type, value);
		editor.commit();
	}
	
	public static String getColumnName(String columnName)
	{
		return  columnName.replace("character", "存储位置");
	}
	
	public static String getCoutTypeName(String typeId)
	{
		String val = "";
		if(typeId.equals("0"))
		{
			val = "总和";
		}else if(typeId.equals("1"))
		{
			val = "平均";
		}else if(typeId.equals("3"))
		{
			val = "最大";
		}else if(typeId.equals("4"))
		{
			val = "最小";
		}
		return val;
	}
	
	public static String getMathTypeName(String typeId)
	{
		String val = "";
		if(typeId.equals("0"))
		{
			val = "+";
		}else if(typeId.equals("1"))
		{
			val = "-";
		}else if(typeId.equals("2"))
		{
			val = "×";
		}else if(typeId.equals("3"))
		{
			val = "÷";
		}
		return val;
	}
	public static String getTypeName(int typeId)
	{
		String val = "";
		switch (typeId) {
	 	case 1:
			val = "数字";
			break;
		case 2:
			val = "单行普通文本";
			break;
		case 3:
			val = "多行文本";
			break;
		case 4:
			val = "带掩码文本";
			break;
		case 5:
			val = "有值集文本";
			break;
		case 6:
			val = "日期";
			break;
		case 7:
			val = "时间";
			break;
		case 8:
			val = "日期时间";
			break;
		case 9:
			val = "计算类型";
			break;
		}

		return val;
	}
}
