package ptool.datacase.util;

import android.app.Application;

/** */
/**
 * @基本配置数据区域
 */

public class SystemConfig extends Application {
	
 
	// -begin-基础数据-------------------------------------------------------------
	// 系统版本号
	private String appVersion;
	// 显示密度
	private double density;
	// 是否全屏
	private boolean isFullScreen;

	 
	// -end-基础数据-------------------------------------------------------------

	@Override
	public void onCreate() {
		setVersion("0");
		setDensity(1);
		setIsFullScreen(true);
 
		super.onCreate();
	}

	/**
	 * 数据库版本号
	 * 
	 * @return
	 */
	public int getDatabaseVersion() {
		return 20;
	}
 
	public String getVersion() {
		return appVersion;
	}

	public void setVersion(String s) {
		appVersion = s;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double s) {
		density = s;
	}

	public boolean getIsFullScreen() {
		return isFullScreen;
	}

	public void setIsFullScreen(boolean s) {
		isFullScreen = s;
	}
 
}
