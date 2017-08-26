package ptool.datacase.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.data.DatabaseHelper;
import ptool.datacase.obj.DBCaseData;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBCaseDataService {
	private DatabaseHelper databaseHelper;

	// private Context context;

	public DBCaseDataService(Context context) {
		// this.context = context;
		this.databaseHelper = new DatabaseHelper(context);
	}

	public Boolean add(DBCaseData obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "insert into dbcase_data (data_id,type_id,last_md_date,has_synced"
					+ ",character1,character2,character3,character4,character5,character6,character7,character8,character9,character10"
					+ ",character11,character12,character13,character14,character15,character16,character17,character18,character19,character20"
					+ ",character21,character22,character23,character24,character25,character26,character27,character28,character29,character30"
					+ ",character31,character32,character33,character34,character35,character36,character37,character38,character39,character40)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Object[] bindArgs = new Object[] { obj.getDataId(),
					obj.getTypeId(), Common.getSysNowTime(), Constant.FLAG_NO,
					obj.getCharacter1(), obj.getCharacter2(),
					obj.getCharacter3(), obj.getCharacter4(),
					obj.getCharacter5(), obj.getCharacter6(),
					obj.getCharacter7(), obj.getCharacter8(),
					obj.getCharacter9(), obj.getCharacter10(),
					obj.getCharacter11(), obj.getCharacter12(),
					obj.getCharacter13(), obj.getCharacter14(),
					obj.getCharacter15(), obj.getCharacter16(),
					obj.getCharacter17(), obj.getCharacter18(),
					obj.getCharacter19(), obj.getCharacter20(),
					obj.getCharacter21(), obj.getCharacter22(),
					obj.getCharacter23(), obj.getCharacter24(),
					obj.getCharacter25(), obj.getCharacter26(),
					obj.getCharacter27(), obj.getCharacter28(),
					obj.getCharacter29(), obj.getCharacter30(),
					obj.getCharacter31(), obj.getCharacter32(),
					obj.getCharacter33(), obj.getCharacter34(),
					obj.getCharacter35(), obj.getCharacter36(),
					obj.getCharacter37(), obj.getCharacter38(),
					obj.getCharacter39(), obj.getCharacter40() };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseData.add()", "手袋明细记录新增失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public Boolean update(DBCaseData obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			String sql = "update dbcase_data set last_md_date=?,has_synced=?"
					+ ",character1=?,character2=?,character3=?,character4=?,character5=?,character6=?,character7=?,character8=?,character9=?,character10=?"
					+ ",character11=?,character12=?,character13=?,character14=?,character15=?,character16=?,character17=?,character18=?,character19=?,character20=?"
					+ ",character21=?,character22=?,character23=?,character24=?,character25=?,character26=?,character27=?,character28=?,character29=?,character30=?"
					+ ",character31=?,character32=?,character33=?,character34=?,character35=?,character36=?,character37=?,character38=?,character39=?,character40=?"
					+ " where data_id = ?";
			Object[] bindArgs = new Object[] { 
					Common.getSysNowTime(), Constant.FLAG_NO,
					obj.getCharacter1(), obj.getCharacter2(),
					obj.getCharacter3(), obj.getCharacter4(),
					obj.getCharacter5(), obj.getCharacter6(),
					obj.getCharacter7(), obj.getCharacter8(),
					obj.getCharacter9(), obj.getCharacter10(),
					obj.getCharacter11(), obj.getCharacter12(),
					obj.getCharacter13(), obj.getCharacter14(),
					obj.getCharacter15(), obj.getCharacter16(),
					obj.getCharacter17(), obj.getCharacter18(),
					obj.getCharacter19(), obj.getCharacter20(),
					obj.getCharacter21(), obj.getCharacter22(),
					obj.getCharacter23(), obj.getCharacter24(),
					obj.getCharacter25(), obj.getCharacter26(),
					obj.getCharacter27(), obj.getCharacter28(),
					obj.getCharacter29(), obj.getCharacter30(),
					obj.getCharacter31(), obj.getCharacter32(),
					obj.getCharacter33(), obj.getCharacter34(),
					obj.getCharacter35(), obj.getCharacter36(),
					obj.getCharacter37(), obj.getCharacter38(),
					obj.getCharacter39(), obj.getCharacter40(), 
					obj.getDataId() };
			db.execSQL(sql, bindArgs);
			return true;
		} catch (Exception ex) {
			Log.e("DBCaseData.update()", "手袋明细记录更新失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	public void delete(DBCaseData obj) {
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			if (obj.getHasSynced().equals(Constant.FLAG_YES)) {
				String sql = "update dbcase_data set has_synced = 'D'"
						+ " where data_id = ?";
				Object[] bindArgs = new Object[] { obj.getDataId() };
				db.execSQL(sql, bindArgs);
			} else {
				String sql = "delete from dbcase_data where data_id = ?";
				Object[] bindArgs = new Object[] { obj.getDataId() };
				db.execSQL(sql, bindArgs);
			}
		} catch (Exception ex) {
			Log.e("DBCaseData.delete()", "手袋明细记录删除失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}

	public DBCaseData getObj(String dataId, String typeId) {
		DBCaseData obj = new DBCaseData();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			Cursor cursor = db
					.rawQuery(
							"select data_id,type_id,last_md_date,has_synced"
									+ ",character1,character2,character3,character4,character5,character6,character7,character8,character9,character10"
									+ ",character11,character12,character13,character14,character15,character16,character17,character18,character19,character20"
									+ ",character21,character22,character23,character24,character25,character26,character27,character28,character29,character30"
									+ ",character31,character32,character33,character34,character35,character36,character37,character38,character39,character40"
									+ " from dbcase_data  where data_id = ? and  type_id = ?",
							new String[] { dataId, typeId });
			if (cursor.moveToFirst()) {
				obj.setDataId(cursor.getString(0));
				obj.setTypeId(cursor.getString(1));
				obj.setLastMdDate(cursor.getString(2));
				obj.setHasSynced(cursor.getString(3));
				obj.setCharacter1(cursor.getString(4));
				obj.setCharacter2(cursor.getString(5));
				obj.setCharacter3(cursor.getString(6));
				obj.setCharacter4(cursor.getString(7));
				obj.setCharacter5(cursor.getString(8));
				obj.setCharacter6(cursor.getString(9));
				obj.setCharacter7(cursor.getString(10));
				obj.setCharacter8(cursor.getString(11));
				obj.setCharacter9(cursor.getString(12));
				obj.setCharacter10(cursor.getString(13));
				obj.setCharacter11(cursor.getString(14));
				obj.setCharacter12(cursor.getString(15));
				obj.setCharacter13(cursor.getString(16));
				obj.setCharacter14(cursor.getString(17));
				obj.setCharacter15(cursor.getString(18));
				obj.setCharacter16(cursor.getString(19));
				obj.setCharacter17(cursor.getString(20));
				obj.setCharacter18(cursor.getString(21));
				obj.setCharacter19(cursor.getString(22));
				obj.setCharacter20(cursor.getString(23));
				obj.setCharacter21(cursor.getString(24));
				obj.setCharacter22(cursor.getString(25));
				obj.setCharacter23(cursor.getString(26));
				obj.setCharacter24(cursor.getString(27));
				obj.setCharacter25(cursor.getString(28));
				obj.setCharacter26(cursor.getString(29));
				obj.setCharacter27(cursor.getString(30));
				obj.setCharacter28(cursor.getString(31));
				obj.setCharacter29(cursor.getString(32));
				obj.setCharacter30(cursor.getString(33));
				obj.setCharacter31(cursor.getString(34));
				obj.setCharacter32(cursor.getString(35));
				obj.setCharacter33(cursor.getString(36));
				obj.setCharacter34(cursor.getString(37));
				obj.setCharacter35(cursor.getString(38));
				obj.setCharacter36(cursor.getString(39));
				obj.setCharacter37(cursor.getString(40));
				obj.setCharacter38(cursor.getString(41));
				obj.setCharacter39(cursor.getString(42));
				obj.setCharacter40(cursor.getString(43));
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseData.getObj()", "手袋明细记录查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
		return obj;
	}

	public void queryAll(Context act, List<Map<String, Object>> objList,
			String typeId,String filter) {
		objList.clear();
		SQLiteDatabase db = databaseHelper.readDatabase();
		try {
			String colums = "A";
			Cursor cursor = db
					.rawQuery(
							//"select column_name from dbcase_chars where type_id =? and is_hide='否' order by char_sequence limit 0,4",
							"select column_name from dbcase_chars where type_id =? and is_hide='否' order by char_sequence",
							new String[] { typeId });
			int i = 1;
			while (cursor.moveToNext()) {
				colums = colums + ","+cursor.getString(0) + " as data"+ i;
				i++;
			}
			colums = colums.replace("A,", "");
			if(colums.equals("A"))
			{
				colums = "data_id";
			}else
			{
				colums = "data_id,"+colums;
			}
			cursor.close();
			
			cursor = db
					.rawQuery(
							"select "+colums+" from dbcase_data where has_synced <> 'D' and type_id = ? " + filter,
							new String[] { typeId });
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (cursor.moveToNext()) {
				map = new HashMap<String, Object>();

				map.put("ListDataId", cursor.getString(0));
				if(i > 1)
				{
				if (!cursor.isNull(1)) {
					map.put("ListItem1", cursor.getString(1));
				} else {
					map.put("ListItem1", "");
				}
				}else
				{
					map.put("ListItem1", "");
				}
				if(i > 2)
				{
					if (!cursor.isNull(2)) {
						map.put("ListItem2", cursor.getString(2));
					} else {
						map.put("ListItem2", "");
					}
				}else
				{
					map.put("ListItem2", "");
				}
				if(i > 3)
				{
					if (!cursor.isNull(3)) {
						map.put("ListItem3", cursor.getString(3));
					} else {
						map.put("ListItem3", "");
					}
				}else
				{
					map.put("ListItem3", "");
				}
				if(i > 4)
				{
					if (!cursor.isNull(4)) {
						map.put("ListItem4", cursor.getString(4));
					} else {
						map.put("ListItem4", "");
					}
				}else
				{
					map.put("ListItem4", "");
				}
				objList.add(map);
			}
			cursor.close();
		} catch (Exception ex) {
			Log.e("DBCaseData.queryAll()", "手袋明细记录查询失败！" + ex.getMessage());
		} finally {
			db.close();
		}
	}
}
