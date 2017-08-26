package ptool.datacase.data;

import android.os.Environment;

public class DatabaseConstant {
	 public static final String DATABASE_NAME = "an_db.db";
	  public static final String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PToolDataCase/";
	  public static final int DATABASE_VERSION = 1;
	  public static final String DATABASE_BACKUP_NAME = "-datacase.bkf";
		public static final String DATABASE_BACKUP_PATH = Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/PToolDataCase/Backup/";

		public static final String PREFS_NAME = "PToolPrefsFile";
}
