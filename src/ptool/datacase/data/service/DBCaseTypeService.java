package ptool.datacase.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.data.DatabaseHelper;
import ptool.datacase.obj.DBCaseType;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DBCaseTypeService {
	private DatabaseHelper databaseHelper;

	// private Context context;

	public DBCaseTypeService(Context context) {
		// this.context = context;
		this.databaseHelper = new DatabaseHelper(context);
	}

	public Boolean add(DBCaseType obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "insert into dbcase_type(type_id,type_name ,icon_name,mark,is_lock,enhance_sql,last_md_date,has_synced,sequence)"
					+ " values(?,?,?,?,?,?,?,?,?)";
			Object[] bindArgs = new Object[] { obj.getTypeId(),
					obj.getTypeName(), obj.getIconName(),obj.getMark(),obj.getIsLock(),obj.getEnhanceSql()
					,Common.getSysNowTime(),Constant.FLAG_NO,obj.getSequence() };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseType.add()", "手袋类型新增失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public Boolean update(DBCaseType obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_type set type_name = ? ,icon_name = ?,mark = ?,is_lock = ?,enhance_sql =?,last_md_date = ?,has_synced=?,sequence=?"
					+ " where type_id = ?";
			Object[] bindArgs = new Object[] { obj.getTypeName()
					, obj.getIconName(),obj.getMark(),obj.getIsLock(),obj.getEnhanceSql()
					,Common.getSysNowTime(),Constant.FLAG_NO,obj.getSequence(),obj.getTypeId()};
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseType.update()", "手袋类型更新失败！" + ex.getMessage());
		return false;
		} finally {
			db.close();
		}
	}
 
	public  void delete(String typeId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		db.beginTransaction();
		try {
			//删除值集
			String sql = "update dbcase_values set has_synced = 'D' where has_synced='是' and char_id in (select char_id from dbcase_chars where type_id = ?)";
			Object[] bindArgs = new Object[] { typeId };
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_values where has_synced='否' and char_id in (select char_id from dbcase_chars where type_id = ?)";
			db.execSQL(sql, bindArgs);
			//删除计算要素
			sql = "update dbcase_chars_math set has_synced = 'D' where has_synced='是' and char_id in (select char_id from dbcase_chars where type_id = ?)";
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_chars_math where has_synced='否' and char_id in (select char_id from dbcase_chars where type_id = ?)";
			db.execSQL(sql, bindArgs);
			
			//删除要素
			sql = "update dbcase_chars set has_synced = 'D' where has_synced='是' and  type_id = ?";
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_chars where has_synced='否' and  type_id = ?";
			db.execSQL(sql, bindArgs);
			
			//删除值集
			sql = "update dbcase_data set has_synced = 'D' where has_synced='是' and  type_id = ?";
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_data where has_synced='否' and  type_id = ?";
			db.execSQL(sql, bindArgs);
			
			//删除手袋类型
			sql = "update dbcase_type set has_synced = 'D' where has_synced='是' and  type_id = ?";
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_type where has_synced='否' and  type_id = ?";
			db.execSQL(sql, bindArgs);
			
	 
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e("DBCaseType.delete()", "手袋类型删除失败！" + ex.getMessage());
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	public void queryAll(Context act, List<Map<String, Object>> objList) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select type_id,type_name ,icon_name,mark,is_lock,enhance_sql,last_md_date,has_synced from dbcase_type where has_synced <> 'D' order by sequence",
							null);
			HashMap<String, Object> map = new HashMap<String, Object>();
			Drawable d = null;
			d =act.getResources()
					.getDrawable(R.drawable.type_add);
			 
			map.put("ListItemImage", d);
			map.put("ListItemTitle", "添加");
			map.put("ListItemId", "");
			objList.add(map);
			
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				d = null;
				if (!cursor.getString(2).equals(""))
					d = Common.getImage(cursor.getString(2));
				if (d == null) {
					d = Common.getDefaultImage(act);
				}
				map.put("ListItemImage", d);
				map.put("ListItemTitle", cursor.getString(1));
				map.put("ListItemId", cursor.getString(0));
				objList.add(map);
			}
			map = new HashMap<String, Object>();
		
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseType.queryAll()", "手袋类型查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public DBCaseType getObj(String typeId) {
		DBCaseType obj = new DBCaseType();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select type_id,type_name ,icon_name,mark,is_lock,enhance_sql,last_md_date,has_synced,sequence from dbcase_type where type_id = ?",
							new String[] { typeId });
			if (cursor.moveToFirst()) {
				obj.setTypeId(cursor.getString(0));
				obj.setTypeName(cursor.getString(1));
				obj.setIconName(cursor.getString(2));
				obj.setMark(cursor.getString(3));
				obj.setIsLock(cursor.getString(4));
				obj.setEnhanceSql(cursor.getString(5));
				obj.setLastMdDate(cursor.getString(6));
				obj.setHasSynced(cursor.getString(7));
				obj.setSequence(cursor.getInt(8));
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseType.getObj()", "手袋类型查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
		return obj;
	}

	public Boolean updateSequence(String typeId, int sequence) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_type set last_md_date = ?,has_synced = ?,sequence = ?"
					+ " where type_id = ? ";
			Object[] bindArgs = new Object[] { Common.getSysNowTime(), Constant.FLAG_NO,
					sequence, typeId };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseType.updateSequence()",
					"手袋类型排序 更新失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}
	

	public int getUseCount(String typeId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			int count = 0;
			String sql = "select count(char_id) from dbcase_chars  where type_id = ? and has_synced <> 'D'";
			Cursor cursor = db.rawQuery(sql, new String[] { typeId });
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
			sql = "select count(data_id) from dbcase_data  where type_id = ? and has_synced <> 'D'";
			cursor = db.rawQuery(sql, new String[] { typeId });
			if (cursor.moveToFirst()) {
				count = count + cursor.getInt(0);
			}
			cursor.close();
			return count;
		} catch (Exception ex) {
			Log.e("DBCaseType.getUseCount()", "手袋类型使用次数统计失败！" + ex.getMessage());
			return -1;
		} finally {
			db.close();
		}
	}
	
	 
	
}
