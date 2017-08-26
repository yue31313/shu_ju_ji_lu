package ptool.datacase.net;

import ptool.datacase.R;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Config {
    private static final String TAG = "Config";
    
    public static final String UPDATE_SERVER = "http://www.wmptool.com/Download/";
    public static final String UPDATE_APKNAME = "DataCase.apk";
    public static final String UPDATE_VERJSON = "datacase_version.json";
    public static final String UPDATE_SAVENAME = "DataCase.apk";
    public static final String  UPLOAD_SERVER = "http://www.wmptool.com/fileUpload.php";
    public static final String  UPLOAD_PATH = "http://www.wmptool.com/upload/";
    
    public static int getVerCode(Context context) {
            int verCode = -1;
            try {
                    verCode = context.getPackageManager().getPackageInfo(
                                    "ptool.datacase", 0).versionCode;
            } catch (NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
            }
            return verCode;
    }
    
    public static String getVerName(Context context) {
            String verName = "";
            try {
                    verName = context.getPackageManager().getPackageInfo(
                                    "ptool.datacase", 0).versionName;
            } catch (NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
            }
            return verName; 

    }
    
    public static String getAppName(Context context) {
            String verName = context.getResources()
            .getText(R.string.app_name).toString();
            return verName;
    }
}
