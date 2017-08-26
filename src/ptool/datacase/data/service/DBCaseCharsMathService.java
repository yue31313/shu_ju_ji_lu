package ptool.datacase.data.service;

import ptool.datacase.data.DatabaseHelper;
import ptool.datacase.obj.DBCaseCharsMath;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBCaseCharsMathService {
	private DatabaseHelper databaseHelper;

	// private Context context;

	public DBCaseCharsMathService(Context context) {
		// this.context = context;
		this.databaseHelper = new DatabaseHelper(context);
	}

	public Boolean add(DBCaseCharsMath obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "insert into dbcase_chars_math(char_id,char_from ,char_to,char_math,last_md_date,has_synced)"
					+ " values(?,?,?,?,?,?)";
			Object[] bindArgs = new Object[] { obj.getCharId(),
					obj.getCharFrom(), obj.getCharTo(), obj.getCharMath(),
					Common.getSysNowTime(), Constant.FLAG_NO };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseCharsMath.add()", "计算要素新增失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public Boolean update(DBCaseCharsMath obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_chars_math set char_from=? ,char_to=?,char_math=?, last_md_date=?,has_synced=?"
					+ " where char_id = ?";
			Object[] bindArgs = new Object[] { obj.getCharFrom(),
					obj.getCharTo(), obj.getCharMath(), Common.getSysNowTime(),
					Constant.FLAG_NO, obj.getCharId() };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseCharsMath.update()", "计算要素更新失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public void delete(String charId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			Object[] bindArgs = new Object[] { charId };
			// 删除计算要素
			String sql = "update dbcase_chars_math set has_synced = 'D' where has_synced='是' and char_id=?";
			db.execSQL(sql, bindArgs);

			sql = "delete from dbcase_chars_math where has_synced='否' and char_id=?";
			db.execSQL(sql, bindArgs);

		} catch (Exception ex) {
			Log.e("DBCaseCharsMath.delete()", "计算要素删除失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}
 
	public DBCaseCharsMath getObj(String charId) {
		DBCaseCharsMath obj = new DBCaseCharsMath();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select char_id,char_from ,char_to,char_math,last_md_date,has_synced  from dbcase_chars_math where char_id = ?",
							new String[] { charId });
			if (cursor.moveToFirst()) {
				obj.setCharId(cursor.getString(0));
				obj.setCharFrom(cursor.getString(1));
				obj.setCharTo(cursor.getString(2));
				obj.setCharMath(cursor.getString(3));
				obj.setLastMdDate(cursor.getString(4));
				obj.setHasSynced(cursor.getString(5));
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseCharsMath.getObj()", "计算要素查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
		return obj;
	}
}
