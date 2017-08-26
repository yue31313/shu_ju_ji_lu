package ptool.datacase.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.data.DatabaseHelper;
import ptool.datacase.obj.DBCaseValues;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBCaseValuesService {
	private DatabaseHelper databaseHelper;

	// private Context context;

	public DBCaseValuesService(Context context) {
		// this.context = context;
		this.databaseHelper = new DatabaseHelper(context);
	}

	public Boolean add(DBCaseValues obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "insert into dbcase_values(value_id,type_id ,char_id,value_name,last_md_date,has_synced)"
					+ " values(?,?,?,?,?,?)";
			Object[] bindArgs = new Object[] { obj.getValueId(),
					obj.getTypeId(), obj.getCharId(), obj.getValueName(),
					Common.getSysNowTime(), Constant.FLAG_NO };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseValues.add()", "要素值集新增失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public Boolean update(DBCaseValues obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_values set type_id = ? ,char_id = ?,value_name = ?,last_md_date = ?,has_synced=?"
					+ " where value_id = ?";
			Object[] bindArgs = new Object[] { obj.getTypeId(),
					obj.getCharId(), obj.getValueName(),
					Common.getSysNowTime(), Constant.FLAG_NO, obj.getValueId() };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseValues.update()", "要素值集更新失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public void delete(DBCaseValues obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			if (obj.getHasSynced().equals(Constant.FLAG_YES)) {
				String sql = "update dbcase_values set has_synced = 'D'"
						+ " where value_id = ?";
				Object[] bindArgs = new Object[] { obj.getValueId() };
				db.execSQL(sql, bindArgs);
			} else {
				String sql = "delete from dbcase_values where value_id = ?";
				Object[] bindArgs = new Object[] { obj.getValueId() };
				db.execSQL(sql, bindArgs);
			}
		} catch (Exception ex) {
			Log.e("DBCaseValues.delete()", "要素值集删除失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public void delete(String valueId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			Object[] bindArgs = new Object[] { valueId };
			String sql = "update dbcase_values set has_synced = 'D' where has_synced='是' and value_id=?";
			db.execSQL(sql, bindArgs);

			sql = "delete from dbcase_values where has_synced='否' and value_id=?";
			db.execSQL(sql, bindArgs);

		} catch (Exception ex) {
			Log.e("DBCaseValues.delete()", "要素值集删除失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public void queryValues(Context act, List<Map<String, Object>> objList,
			String typeId, String charId) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select value_id,value_name from dbcase_values where type_id = ? and char_id =? and   has_synced <> 'D' ",
							new String[] { typeId, charId });
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("ListItemImage", R.drawable.cross);// 图像资源的ID
				map.put("ListItemTitle", cursor.getString(1));
				map.put("ListItemId", cursor.getString(0));
				objList.add(map);
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseValues.queryValues()", "要素值集查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public DBCaseValues getObj(String valueId) {
		DBCaseValues obj = new DBCaseValues();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select value_id,type_id ,char_id,value_name,last_md_date,has_synced from dbcase_values where value_id = ?",
							new String[] { valueId });
			if (cursor.moveToFirst()) {
				obj.setValueId(cursor.getString(0));
				obj.setTypeId(cursor.getString(1));
				obj.setCharId(cursor.getString(2));
				obj.setValueName(cursor.getString(3));
				obj.setLastMdDate(cursor.getString(4));
				obj.setHasSynced(cursor.getString(5));
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseValues.getObj()", "要素值集查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
		return obj;
	}

	// public Boolean updateSequence(String ValuesName, int sequence) {
	// SQLiteDatabase db = databaseHelper.openDatabase();
	// try {
	// String sql =
	// "update dbcase_Values set last_md_date = ?,has_synced = ?,sequence = ?"
	// + " where Values_name = ? ";
	// Object[] bindArgs = new Object[] { Common.getSysNowTime(),
	// Constant.FLAG_NO, sequence, ValuesName };
	// db.execSQL(sql, bindArgs);
	// return true;
	// } catch (Exception ex) {
	// Log.e("DBCaseValues.updateSequence()",
	// "要素值集排序 更新失败！" + ex.getMessage());
	// return false;
	// } finally {
	// db.close();
	// }
	// }
	//
	// public int getUseCount(String ValuesId) {
	// SQLiteDatabase db = databaseHelper.openDatabase();
	// try {
	// int count = 0;
	// String sql =
	// "select count(char_id) from dbcase_chars  where Values_id = ? and has_synced <> 'D'";
	// Cursor cursor = db.rawQuery(sql, new String[] { ValuesId });
	// if (cursor.moveToFirst()) {
	// count = cursor.getInt(0);
	// }
	// sql =
	// "select count(data_id) from dbcase_data  where Values_id = ? and has_synced <> 'D'";
	// cursor = db.rawQuery(sql, new String[] { ValuesId });
	// if (cursor.moveToFirst()) {
	// count = count + cursor.getInt(0);
	// }
	// cursor.close();
	// return count;
	// } catch (Exception ex) {
	// Log.e("DBCaseValues.getUseCount()",
	// "要素值集使用次数统计失败！" + ex.getMessage());
	// return -1;
	// } finally {
	// db.close();
	// }
	// }

}
