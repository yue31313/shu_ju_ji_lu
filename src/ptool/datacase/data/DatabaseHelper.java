package ptool.datacase.data;

/**
 * 
 */
import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context) {
		super(context, DatabaseConstant.DATABASE_NAME, null,
				DatabaseConstant.DATABASE_VERSION);
	}

	public SQLiteDatabase openDatabase() {
		try {
			String dbPath = DatabaseConstant.DATABASE_PATH
					+ DatabaseConstant.DATABASE_NAME;
			if (!(new File(dbPath)).exists()) {
				return null;
			} else {
				SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath,
						null, 0);
				return database;
			}
		} catch (Exception e) {
			Log.e("DataBase Open", "数据库无法打开，错误信息：" + e.getMessage());
			return null;
		}
	}

	public SQLiteDatabase readDatabase() {
		try {
			String dbPath = DatabaseConstant.DATABASE_PATH
					+ DatabaseConstant.DATABASE_NAME;
			if (!(new File(dbPath)).exists()) {
				return null;
			} else {
				SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
						dbPath, null);
				return database;
			}
		} catch (Exception e) {
			Log.e("DataBase Read", "数据库无法读取，错误信息：" + e.getMessage());
			return null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//Log.e("DataBase", "DataBase 版本较低，");
	}

}
