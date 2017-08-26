package ptool.datacase.data.service;

import ptool.datacase.data.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SysPassService {
	private DatabaseHelper databaseHelper;

	// private Context context;

	public SysPassService(Context context) {
		// this.context = context;
		this.databaseHelper = new DatabaseHelper(context);
	}
	
	public void make(String passId, String passValue) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			Object[] bindArgs = new Object[] { passId };
			String sql = "delete from sys_pass  where pass_id = ?";
			db.execSQL(sql, bindArgs);
			
			 sql = "insert into sys_pass(pass_id,pass_value)"
					+ " values(?,?)";
			bindArgs = new Object[] { passId, passValue };
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("SysPass.make()", "ϵͳ���ñ��¼����ʧ�ܣ�" + ex.getMessage());
		} finally {
			db.close();
		}
	}
	
	public void add(String passId, String passValue) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "insert into sys_pass(pass_id,pass_value)"
					+ " values(?,?)";
			Object[] bindArgs = new Object[] { passId, passValue };
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("SysPass.add()", "ϵͳ���ñ��¼����ʧ�ܣ�" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public void update(String passId, String passValue) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update sys_pass set pass_value = ?"
					+ " where pass_id = ?";
			Object[] bindArgs = new Object[] { passValue, passId };
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("SysPass.update()", "ϵͳ���ñ��¼����ʧ�ܣ�" + ex.getMessage());
		} finally {
			db.close();
		}
	}
	

	public void delete(String passId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "delete from sys_pass  where pass_id = ?";
			Object[] bindArgs = new Object[] {passId };
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("SysPass.delete()", "ϵͳ���ñ��¼ɾ��ʧ�ܣ�" + ex.getMessage());
		} finally {
			db.close();
		}
	}
	
	public void add(SQLiteDatabase db, String passId, String passValue) {
		try {
			String sql = "insert into sys_pass(pass_id,pass_value)"
					+ " values(?,?)";
			Object[] bindArgs = new Object[] { passId, passValue };
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("SysPass.add()", "ϵͳ���ñ��¼����ʧ�ܣ�" + ex.getMessage());
		} 
	}
	public void update(SQLiteDatabase db, String passId, String passValue) {
		try {
			Object[] bindArgs = new Object[] { passId };
			String sql = "delete from sys_pass  where pass_id = ?";
			db.execSQL(sql, bindArgs);
			
			bindArgs = new Object[] { passId,passValue };
			 sql = "insert into sys_pass(pass_id,pass_value)"
						+ " values(?,?)";			
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("SysPass.update()", "ϵͳ���ñ��¼����ʧ�ܣ�" + ex.getMessage());
		}
	}

	public String getValue(String passId) {
		String val = "";
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			String sql = "SELECT pass_value"
					+ " FROM sys_pass  where pass_id = ?";
			Cursor cursor = db.rawQuery(sql, new String[] { passId });
			if (cursor.moveToFirst()) {
				val = cursor.getString(0);
			}
			cursor.close();
			if(val == null)
			{
				val = "";
			}
		} catch (Exception ex) {
			Log.e("SysPass.getValue()", "��ȡϵͳ������Ϣʧ�ܣ�" + ex.getMessage());
		} finally {
			db.close();
		}
		return val;
	}

	/**
	 * ɾ�� ��¼
	 * 
	 * @param obj
	 * @return
	 */
	public Boolean delete(SQLiteDatabase db,String passId) {
		try {
			String sql = "delete from sys_pass  where pass_id = ?";
			Object[] bindArgs = new Object[] { passId };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("SysPass.delete()", "ϵͳ����ɾ��ʧ�ܣ�" + ex.getMessage());
			return false;
		} 
	}
}
