package ptool.datacase.util;

import android.app.Application;

/** */
/**
 * @����������������
 */

public class SystemConfig extends Application {
	
 
	// -begin-��������-------------------------------------------------------------
	// ϵͳ�汾��
	private String appVersion;
	// ��ʾ�ܶ�
	private double density;
	// �Ƿ�ȫ��
	private boolean isFullScreen;

	 
	// -end-��������-------------------------------------------------------------

	@Override
	public void onCreate() {
		setVersion("0");
		setDensity(1);
		setIsFullScreen(true);
 
		super.onCreate();
	}

	/**
	 * ���ݿ�汾��
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
