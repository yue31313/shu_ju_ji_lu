package ptool.datacase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ptool.datacase.data.DataCommon;
import ptool.datacase.data.service.SysPassService;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.SystemConfig;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class LoadingActivity extends Activity {

	private String passWord;
	private SystemConfig app;
	private boolean onlyInt;
	private boolean onlyPicture;
	private static Button etTemp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_loading);

		app = (SystemConfig) this.getApplication();

		// 获取屏幕特性
		Common.getAssetData(LoadingActivity.this, app);
	 
		// 判断SD卡是否存在
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			Common.showMessage(LoadingActivity.this, "未发现可用SD卡，软件将无法启动！\n请检查后，再尝试运行！",
					Toast.LENGTH_LONG, R.drawable.error, Gravity.BOTTOM);

			this.finish();
		} else {
			// 新建线程
			new Thread() {
				@Override
				public void run() {
					// 需要花时间计算的方法
					LoadData();
					// 向handler发消息
					handler.sendEmptyMessage(0);
				}
			}.start();
		}
	}

	/**
	 * 用Handler来更新UI
	 */
 	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (onlyPicture) {
				if (passWord.equals("")) {
					Common.showMessage(LoadingActivity.this,
							"为了您的私密，建议您设定登陆密码！",
							Toast.LENGTH_LONG, R.drawable.warning,
							Gravity.BOTTOM);
					ShowMainActivity();
				} else {
					LoadingActivity.this.finish();
					Intent intent = new Intent();
					intent.setClass(LoadingActivity.this, LocusLoginActivity.class);
					startActivity(intent);
				}
			} else {
				if (passWord.equals("") || passWord.equals(Common.MD5(""))) {
					Common.showMessage(LoadingActivity.this,
							"为了您的私密，建议您设定登陆密码！",
							Toast.LENGTH_LONG, R.drawable.warning,
							Gravity.BOTTOM);
					ShowMainActivity();
				} else {
					if (onlyInt) {
						ShowPasswordKey();
					} else {
						ShowPassword();
					}
				}
			}
		}
	};

	private void LoadData() {
		// 初始化数据库
		DataCommon.checkDatabase(this, app);
		initIcons();
	  	//
		SysPassService sysService = new SysPassService(this);
		// Log.i("Loading", "获取密码原文：" + passWord);
		if (sysService.getValue("only_picture").equals("Y")) {
			passWord = sysService.getValue("locus").toUpperCase();
			onlyPicture = true;
		} else {
			passWord = sysService.getValue("main").toUpperCase();
			onlyPicture = false;
			if (sysService.getValue("only_int").equals("Y")) {
				onlyInt = true;
			} else {
				onlyInt = false;
			}
		}
	}

	protected void ShowMainActivity() {
		this.finish();
		Intent intent = new Intent();
		intent.setClass(LoadingActivity.this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * 显示密码验证窗体
	 */
	protected void ShowPassword() {
		CustomDialog.Builder builder = new CustomDialog.Builder(LoadingActivity.this);
		builder.setTitle(R.string.title_pass);
		builder.setIcon(R.drawable.key);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.password_check, null);
		builder.setView(layout);

		final EditText myEditText = (EditText) layout
				.findViewById(R.id.editText_pass);

		Common.showInputMethod(this);

		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 转译输入的密码明文
				String passInput = myEditText.getText().toString();
				Log.i("Loading", "获取密码译文：" + passInput);
				if (passInput.equals(passWord)
						|| Common.MD5(passInput).equals(passWord)) {
//					Common.showMessage(LoadingActivity.this,
//							getResources().getString(R.string.message_loading),
//							Toast.LENGTH_SHORT, R.drawable.success);

					dialog.dismiss();
					ShowMainActivity();
				} else {
					Common.showMessage(LoadingActivity.this,
							getResources().getString(R.string.error_pass),
							Toast.LENGTH_SHORT, R.drawable.error);
					ShowPassword();
				}
			}
		});

		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				System.exit(0);
			}
		});
		CustomDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}

	private void ShowPasswordKey() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.password_int, null);
		Common.setGray(this);
		final PopupWindow passWindow = new PopupWindow(layout,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		passWindow.showAtLocation(findViewById(R.id.layLoadingMain),
				Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL, 0, 0);
		etTemp = (Button) layout.findViewById(R.id.et_value);

		Button btn_0 = (Button) layout.findViewById(R.id.btn_0);
		btn_0.setOnClickListener(digitListener);

		Button btn_1 = (Button) layout.findViewById(R.id.btn_1);
		btn_1.setOnClickListener(digitListener);

		Button btn_2 = (Button) layout.findViewById(R.id.btn_2);
		btn_2.setOnClickListener(digitListener);

		Button btn_3 = (Button) layout.findViewById(R.id.btn_3);
		btn_3.setOnClickListener(digitListener);

		Button btn_4 = (Button) layout.findViewById(R.id.btn_4);
		btn_4.setOnClickListener(digitListener);

		Button btn_5 = (Button) layout.findViewById(R.id.btn_5);
		btn_5.setOnClickListener(digitListener);

		Button btn_6 = (Button) layout.findViewById(R.id.btn_6);
		btn_6.setOnClickListener(digitListener);

		Button btn_7 = (Button) layout.findViewById(R.id.btn_7);
		btn_7.setOnClickListener(digitListener);

		Button btn_8 = (Button) layout.findViewById(R.id.btn_8);
		btn_8.setOnClickListener(digitListener);

		Button btn_9 = (Button) layout.findViewById(R.id.btn_9);
		btn_9.setOnClickListener(digitListener);
		ImageButton btnClear = (ImageButton) layout.findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (etTemp.getText().length() > 0) {
					String val = etTemp.getText().toString();
					etTemp.setText(val.substring(0,
							etTemp.getText().length() - 1));
				}
			}
		});

		Button btnOk = (Button) layout.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 转译输入的密码明文
				String passInput = etTemp.getText().toString();
				Log.i("Loading", "获取密码译文：" + passInput);
				if (passInput.equals(passWord)
						|| Common.MD5(passInput).equals(passWord)) {
//					Common.showMessage(LoadingActivity.this,
//							getResources().getString(R.string.message_loading),
//							Toast.LENGTH_SHORT, R.drawable.success,
//							Gravity.BOTTOM);

					passWindow.dismiss();
					ShowMainActivity();
				} else {
					etTemp.setError(getResources().getString(
							R.string.error_pass));
					Common.showMessage(LoadingActivity.this,
							getResources().getString(R.string.error_pass),
							Toast.LENGTH_SHORT, R.drawable.error,
							Gravity.BOTTOM);
				}
			}
		});
		ImageView imageViewClose = (ImageView) layout
				.findViewById(R.id.imageViewClose);
		imageViewClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passWindow.dismiss();
				System.exit(0);
			}
		});

		ImageView imageViewKey = (ImageView) layout
				.findViewById(R.id.imageViewKey);
		imageViewKey.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passWindow.dismiss();
				ShowPassword();
			}
		});
	}

	private static View.OnClickListener digitListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			try {
				String val = getButtonText(arg0.getId());
				String nowVal = etTemp.getText().toString();
				etTemp.setText(nowVal + val);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	};

	private static String getButtonText(int buttonId) {
		String val = "";
		switch (buttonId) {
		case R.id.btn_0:
			val = "0";
			break;
		case R.id.btn_1:
			val = "1";
			break;
		case R.id.btn_2:
			val = "2";
			break;
		case R.id.btn_3:
			val = "3";
			break;
		case R.id.btn_4:
			val = "4";
			break;
		case R.id.btn_5:
			val = "5";
			break;
		case R.id.btn_6:
			val = "6";
			break;
		case R.id.btn_7:
			val = "7";
			break;
		case R.id.btn_8:
			val = "8";
			break;
		case R.id.btn_9:
			val = "9";
			break;
		}
		return val;
	}

	/**
	 * 初始化图标文件
	 */
	private void initIcons() {
		File dir = new File(Constant.IMAGE_PATH);
		if (!dir.exists()) {
			dir.mkdir();
			String[] files;
			try {
				files = this.getResources().getAssets().list("icons");
			} catch (IOException e) {
				Log.e("initIcons", "图标库获取失败，错误信息：" + e.getMessage());
				return;
			}

			for (int i = 0; i < files.length; i++) {
				try {
					String fileName = files[i];
					File outFile = new File(Constant.IMAGE_PATH, fileName);
					if (outFile.exists())
						continue;

					InputStream in = getAssets().open("icons/" + fileName);
					OutputStream out = new FileOutputStream(outFile);

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					in.close();
					out.close();
				} catch (IOException e) {
					Log.e("initIcons", "图标库复制失败，错误信息：" + e.getMessage());
				}
			}
		}
	}
}
