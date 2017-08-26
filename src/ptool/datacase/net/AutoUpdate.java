package ptool.datacase.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import ptool.datacase.HelpWebActivity;
import ptool.datacase.R;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.CustomDialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

/**
 * 在线升级
 * 
 * @author Zhao
 * 
 */
public class AutoUpdate {
	//private static final String TAG = "AutoUpdate";
	public static ProgressDialog pBar;

	public static boolean getServerVerCode(UpdateEntity updateObj) {
		try {
			String verjson = NetworkTool.getContent(Config.UPDATE_SERVER
					+ Config.UPDATE_VERJSON);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				try {
					updateObj.setNewVerCode(Integer.parseInt(obj
							.getString("verCode")));
					updateObj.setNewVerName(obj.getString("verName"));
				} catch (Exception e) {
					updateObj.setNewVerCode(-1);
					updateObj.setNewVerName("");
					return false;
				}
			}
		} catch (Exception e) {
			//Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	public static void notNewVersionShow(Context context) {
		String verName = Config.getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append("\n已是最新版,无需更新！");
		Common.alert((Activity) context, R.string.prompt, sb.toString(),
				R.drawable.alert_information);
	}

	public static void notNetWok(Context context) {
		StringBuffer sb = new StringBuffer();
		sb.append("无法访问www.wmptool.com");
		sb.append("\n请确保网络已经开启哦！");
		Common.alert((Activity) context, R.string.prompt, sb.toString(),
				R.drawable.alert_information);
	}

	public static void doNewVersionUpdate(final Context context,
			String newVerName, final Handler handler) {
		String verName = Config.getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("您当前使用的版本为：V_");
		sb.append(verName);
		sb.append("\n发现最新版本：V_");
		sb.append(newVerName);
		sb.append("\n是否立即升级到最新版?");
		CustomDialog dialog = new CustomDialog.Builder(context)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("立即升级",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								pBar = new ProgressDialog(context);
								pBar.setMessage("正在下载最新版，请稍候片刻...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								downFile(Config.UPDATE_SERVER
										+ Config.UPDATE_APKNAME, context,
										handler);
							}
						})
				.setNeutralButton("新版本介绍",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(context, HelpWebActivity.class);
								intent.putExtra("WebType","NEW_VERSION");
								((Activity) context).startActivityForResult(intent,
										Constant.REQ_NEW_VERSION_WEB);
							}
						})
				.setNegativeButton("下次再说",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								// finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	private static void downFile(final String url, final Context context,
			final Handler handler) {
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								Config.UPDATE_SAVENAME);
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
						// int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							// count += ch;
							if (length > 0) {
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down(context, handler);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void down(final Context context, Handler handler) {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update(context);
			}
		});
	}

	private static void update(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
