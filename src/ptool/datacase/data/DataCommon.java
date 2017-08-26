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
	 * ������ݿ��Ƿ��Ѿ���ʼ����ȷ�����ݿ���ڣ�
	 * 
	 * @param context
	 * @param app
	 */
	public static void checkDatabase(Context context, SystemConfig app) {
		try {
			//�ж�SDK
			// ���an_sysdb.db�ļ��ľ���·��
			String databaseFilename = DatabaseConstant.DATABASE_PATH
					+ DatabaseConstant.DATABASE_NAME;
			File dir = new File(DatabaseConstant.DATABASE_PATH);
			// ���/sdcard/PToolAccountĿ¼�д��ڣ��������Ŀ¼
			if (!dir.exists()) {
				dir.mkdir();
				Log.i("DataBase Init", "���ݿ�Ŀ¼�������" + dir.getAbsolutePath());
			}
			if (!(new File(databaseFilename)).exists()) {
				InputStream is = context.getResources().openRawResource(
						R.raw.an_db);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				// ��ʼ����an_sysdb.db�ļ�
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				Log.i("DataBase Init", "���ݿ��ʼ����ϣ�");
			} else {
				// �汾���
				SysPassService sysPassService = new SysPassService(context);
				int version = Common.parseInt(sysPassService
						.getValue("version"));
				if (version < app.getDatabaseVersion()) {
					// �������ݿ�
					 
				}
			}
		} catch (Exception e) {
			Log.e("DataBase Init", "���ݿ��޷���ʼ����������Ϣ��" + e.getMessage());
		}
	}
  
	/**
	 * �ļ�����
	 * 
	 * @param oldPath
	 *            Դ�ļ�·��
	 * @param newPath
	 *            Ŀ���ļ�·��
	 * @throws IOException
	 */
	public static void copyFile(String oldPath, String newPath)
			throws IOException {
		// int bytesum = 0;
		int byteread = 0;
		File oldfile = new File(oldPath);
		if (oldfile.exists()) {
			InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[4096];
			// int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				// bytesum += byteread; // �ֽ��� �ļ���С
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
		}
		Log.i("DataCommon", oldPath + "�ļ��ɹ����Ƶ�" + newPath);
	}

	/**
	 * �������ݿ��ļ�
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
				// ȷ������Ŀ¼����
				File backupDir = new File(DatabaseConstant.DATABASE_BACKUP_PATH);
				if (!(backupDir.exists()))
					backupDir.mkdir();

				String backupPath = DatabaseConstant.DATABASE_BACKUP_PATH
						+ Common.getNowDateString()
						+ DatabaseConstant.DATABASE_BACKUP_NAME;
				// �統��ı����Ѿ����ڣ���ɾ�����ļ�
				File backupFile = new File(backupPath);
				if (backupFile.exists())
					backupFile.delete();
				// ���ɵ���ı���
				copyFile(filePath, backupPath);

				int nums = 10;
				String backupNumStr = localSysPassService
						.getValue("auto_backup_nums");
				if (!(backupNumStr.equals("")))
					nums = Integer.parseInt(backupNumStr);

				if (backupDir.listFiles().length > nums) {
					File[] items = backupDir.listFiles();
					items = fileMaopao(items); // ð������
					for (int i = nums; i < items.length; i++) {
						items[i].delete();
					}
				}
				Log.i("DataCommon", "���ݿ��ļ����ݳɹ���");
			}
			//ǿ��ɾ���ϴ��ı����ļ�------------------------------------
			String key = localSysPassService.getValue("cloud_mail") +"~"+ localSysPassService.getValue("cloud_password").toUpperCase();
			String newFileName = DatabaseConstant.DATABASE_PATH + key
					+ ".apk";
			// ɾ�����ļ�
			File backupFile = new File(newFileName);
			if (backupFile.exists())
				backupFile.delete();
			//-----------------------------------------------------------------
		} catch (Exception e) {
			Log.e("DataCommon", "���ݿ��ļ�����ʧ�ܣ�" + e.getMessage());
		}
	}

	/**
	 * �ָ����ݿ��ļ�
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
			Log.e("DataCommon", "���ݿ�ָ�ʧ�ܣ������ļ�����" + fileName);
		}
	}

	/**
	 * ��ñ����ļ��б�
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
	 * �ļ���ð������
	 * 
	 * @param items
	 * @return
	 */
	public static File[] fileMaopao(File[] items) {
		boolean flag; // ��һ����־
		for (int i = 0; i < items.length; i++) {
			flag = false;
			for (int j = 0; j < items.length - 1 - i; j++) {
				if (items[j].lastModified() < items[j + 1].lastModified()) {
					File temp = items[j];
					items[j] = items[j + 1];
					items[j + 1] = temp;
					flag = true; // ��־��Ϊtrue
				}
			}
			if (flag == false)
				i = items.length;// û����������������ǰ�Ѿ�����ֱ�ӷ���
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
				if (value.equals("��"))
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
			Log.e("VACUUMDB()", "ѹ�����ݿ�ʧ�ܣ�" + ex.getMessage());
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
			Log.e("getNewDB()", "������ݿ�ʧ�ܣ�" + ex.getMessage());
			return false;
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	 
	/**
	 * ���ݼ�ֵ����ȡ���ò���
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
	 * �������ò���
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
		return  columnName.replace("character", "�洢λ��");
	}
	
	public static String getCoutTypeName(String typeId)
	{
		String val = "";
		if(typeId.equals("0"))
		{
			val = "�ܺ�";
		}else if(typeId.equals("1"))
		{
			val = "ƽ��";
		}else if(typeId.equals("3"))
		{
			val = "���";
		}else if(typeId.equals("4"))
		{
			val = "��С";
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
			val = "��";
		}else if(typeId.equals("3"))
		{
			val = "��";
		}
		return val;
	}
	public static String getTypeName(int typeId)
	{
		String val = "";
		switch (typeId) {
	 	case 1:
			val = "����";
			break;
		case 2:
			val = "������ͨ�ı�";
			break;
		case 3:
			val = "�����ı�";
			break;
		case 4:
			val = "�������ı�";
			break;
		case 5:
			val = "��ֵ���ı�";
			break;
		case 6:
			val = "����";
			break;
		case 7:
			val = "ʱ��";
			break;
		case 8:
			val = "����ʱ��";
			break;
		case 9:
			val = "��������";
			break;
		}

		return val;
	}
}
