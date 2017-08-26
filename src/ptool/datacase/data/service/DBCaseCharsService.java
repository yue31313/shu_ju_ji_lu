package ptool.datacase.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.data.DatabaseHelper;
import ptool.datacase.obj.DBCaseChars;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBCaseCharsService {
	private DatabaseHelper databaseHelper;

	// private Context context;

	public DBCaseCharsService(Context context) {
		// this.context = context;
		this.databaseHelper = new DatabaseHelper(context);
	}

	public Boolean add(DBCaseChars obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "insert into dbcase_chars(char_id,char_name ,char_datatype,char_decimal,edit_mask,type_id,char_sequence,char_multi,char_count,count_type,char_unit,char_null,char_width,char_rows,column_name,is_hide,for_search,fix_value,last_md_date,has_synced)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] bindArgs = new Object[] { obj.getCharId(),
					obj.getCharName(), obj.getCharDataType(),
					obj.getCharDecimal(), obj.getEditMask(), obj.getTypeId(),
					obj.getCharSequence(), obj.getCharMulti(),
					obj.getCharCount(), obj.getCountType(), obj.getCharUnit(),
					obj.getCharNull(), obj.getCharWidth(), obj.getCharRows(),
					obj.getColumnName(), obj.getIsHide(), obj.getForSearch(),obj.getFixValue(),
					Common.getSysNowTime(), Constant.FLAG_NO };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseChars.add()", "手袋要素新增失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public Boolean update(DBCaseChars obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_chars set char_name=? ,char_datatype=?,char_decimal=?,edit_mask=?,type_id=?,char_sequence=?,char_multi=?,char_count=?,count_type=?,char_unit=?,char_null=?,char_width=?,char_rows=?,column_name=?,is_hide=?,for_search=?,fix_value=?,last_md_date=?,has_synced=?"
					+ " where char_id = ?";
			Object[] bindArgs = new Object[] { obj.getCharName(),
					obj.getCharDataType(), obj.getCharDecimal(),
					obj.getEditMask(), obj.getTypeId(), obj.getCharSequence(),
					obj.getCharMulti(), obj.getCharCount(), obj.getCountType(),
					obj.getCharUnit(), obj.getCharNull(), obj.getCharWidth(),
					obj.getCharRows(), obj.getColumnName(), obj.getIsHide(),
					obj.getForSearch(),obj.getFixValue(), Common.getSysNowTime(),
					Constant.FLAG_NO, obj.getCharId() };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseChars.update()", "手袋要素更新失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public void delete(String charId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			DBCaseChars obj = getObj(charId);
			String sql = "update dbcase_data set "+obj.getColumnName() +" = '' where type_id = '" +obj.getTypeId()+"'";
			db.execSQL(sql);
			//删除值集
			sql = "update dbcase_values set has_synced = 'D' where has_synced='是' and char_id =?";
			Object[] bindArgs = new Object[] { charId };
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_values where has_synced='否' and char_id =?";
			db.execSQL(sql, bindArgs);
			//删除计算要素
			sql = "update dbcase_chars_math set has_synced = 'D' where has_synced='是' and char_id=?";
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_chars_math where has_synced='否' and char_id=?";
			db.execSQL(sql, bindArgs);
			
			//删除要素
			sql = "update dbcase_chars set has_synced = 'D' where has_synced='是' and  char_id = ?";
			db.execSQL(sql, bindArgs);
			
			sql = "delete from dbcase_chars where has_synced='否' and  char_id = ?";
			db.execSQL(sql, bindArgs);
		} catch (Exception ex) {
			Log.e("DBCaseChars.delete()", "手袋要素删除失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public void queryByTypeAll(Context act, List<Map<String, Object>> objList,
			String typeId) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select char_id,char_name ,char_datatype,char_decimal,edit_mask,char_multi,char_unit,char_null,char_rows,column_name,char_count,count_type,is_hide,fix_value from dbcase_chars where has_synced <> 'D' and   type_id = ? order by char_sequence",
							new String[] { typeId });
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("char_id", cursor.getString(0));
				map.put("char_name", cursor.getString(1));
				map.put("char_datatype", cursor.getString(2));
				map.put("char_decimal", cursor.getString(3));
				map.put("edit_mask", cursor.getString(4));
				map.put("char_multi", cursor.getString(5));
				map.put("char_unit", cursor.getString(6));
				map.put("char_null", cursor.getString(7));
				map.put("char_rows", cursor.getString(8));
				map.put("column_name", cursor.getString(9));
				map.put("char_count", cursor.getString(10));
				map.put("count_type", cursor.getString(11));
				map.put("is_hide", cursor.getString(12));
				if (cursor.isNull(13)) {
					map.put("fix_value", "");
				}else
				{
					map.put("fix_value", cursor.getString(13));
				}
				objList.add(map);
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseChars.queryByType()", "手袋要素查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}
	
	
	
	public void queryByType(Context act, List<Map<String, Object>> objList,
			String typeId) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select char_id,char_name ,char_datatype,char_decimal,edit_mask,char_multi,char_unit,char_null,char_rows,column_name,char_count,count_type,fix_value from dbcase_chars where has_synced <> 'D' and is_hide='否' and type_id = ? order by char_sequence",
							new String[] { typeId });
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("char_id", cursor.getString(0));
				map.put("char_name", cursor.getString(1));
				map.put("char_datatype", cursor.getString(2));
				map.put("char_decimal", cursor.getString(3));
				map.put("edit_mask", cursor.getString(4));
				map.put("char_multi", cursor.getString(5));
				map.put("char_unit", cursor.getString(6));
				map.put("char_null", cursor.getString(7));
				map.put("char_rows", cursor.getString(8));
				map.put("column_name", cursor.getString(9));
				map.put("char_count", cursor.getString(10));
				map.put("count_type", cursor.getString(11));
				if (cursor.isNull(12)) {
					map.put("fix_value", "");
				}
				else
				{
					map.put("fix_value", cursor.getString(12));
				}
				objList.add(map);
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseChars.queryByType()", "手袋要素查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}
	
	
	public void queryAll(Context act, List<Map<String, Object>> objList,
			String typeId) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select char_id,column_name from dbcase_chars where has_synced <> 'D' and type_id = ? ",
							new String[] { typeId });
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("char_id", cursor.getString(0));
				map.put("column_name", cursor.getString(1));
				objList.add(map);
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseChars.queryByType()", "手袋要素查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}
	
	public void queryMathByType(Context act, List<Map<String, Object>> objList,
			String typeId) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select char_id,char_name ,char_datatype from dbcase_chars where has_synced <> 'D' and (char_datatype ='1' or char_datatype = '9') and type_id = ? order by char_sequence",
							new String[] { typeId });
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();
				map.put("ListItemImage", R.drawable.star);// 图像资源的ID
				map.put("ListItemTitle", cursor.getString(1));
				map.put("ListItemId", cursor.getString(0));
				objList.add(map);
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseChars.queryMathByType()", "手袋要素查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public DBCaseChars getObj(String charId) {
		DBCaseChars obj = new DBCaseChars();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select char_id,char_name ,char_datatype,char_decimal,edit_mask,type_id,char_sequence,char_multi,char_count"
									+ " ,count_type,char_unit,char_null,char_width,char_rows,column_name,is_hide,for_search,fix_value,last_md_date,has_synced  from dbcase_chars where char_id = ?",
							new String[] { charId });
			if (cursor.moveToFirst()) {
				obj.setCharId(cursor.getString(0));
				obj.setCharName(cursor.getString(1));
				obj.setCharDataType(cursor.getString(2));
				obj.setCharDecimal(cursor.getInt(3));
				obj.setEditMask(cursor.getString(4));
				obj.setTypeId(cursor.getString(5));
				obj.setCharSequence(cursor.getInt(6));
				obj.setCharMulti(cursor.getString(7));
				obj.setCharCount(cursor.getString(8));
				obj.setCountType(cursor.getString(9));
				obj.setCharUnit(cursor.getString(10));
				obj.setCharNull(cursor.getString(11));
				obj.setCharWidth(cursor.getInt(12));
				obj.setCharRows(cursor.getInt(13));
				obj.setColumnName(cursor.getString(14));

				obj.setIsHide(cursor.getString(15));
				obj.setForSearch(cursor.getString(16));
				
				if (cursor.isNull(17)) {
					obj.setFixValue("");
				}else
				{
					obj.setFixValue(cursor.getString(17));
				}
				obj.setLastMdDate(cursor.getString(18));
				obj.setHasSynced(cursor.getString(19));
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseChars.getObj()", "手袋要素查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
		return obj;
	}
 
	public int getUseCount(String charId) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			int count = 0;
//			String sql = "select count(char_id) from dbcase_chars  where char_id = ? and has_synced <> 'D'";
//			Cursor cursor = db.rawQuery(sql, new String[] { charId });
//			if (cursor.moveToFirst()) {
//				count = cursor.getInt(0);
//			}
//			sql = "select count(data_id) from dbcase_values  where char_id = ? and has_synced <> 'D'";
//			cursor = db.rawQuery(sql, new String[] { charId });
//			if (cursor.moveToFirst()) {
//				count = count + cursor.getInt(0);
//			}
//			sql = "select count(data_id) from dbcase_chars_math  where char_id = ? or char_from = ? or char_to=? and has_synced <> 'D'";
//			cursor = db.rawQuery(sql, new String[] { charId, charId, charId });
//			if (cursor.moveToFirst()) {
//				count = count + cursor.getInt(0);
//			}
//			cursor.close();
			return count;
		} catch (Exception ex) {
			Log.e("DBCaseChars.getUseCount()",
					"手袋要素使用次数统计失败！" + ex.getMessage());
			return -1;
		} finally {
			db.close();
		}
	}

	public Boolean updateSequence(String charId, int sequence) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_chars set last_md_date = ?,has_synced = ?,char_sequence = ?"
					+ " where char_id = ? ";
			Object[] bindArgs = new Object[] { Common.getSysNowTime(),
					Constant.FLAG_NO, sequence, charId };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseChars.updateSequence()",
					"手袋要素排序 更新失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}
}
