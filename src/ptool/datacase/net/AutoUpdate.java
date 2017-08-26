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
 * ��������
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
		sb.append("��ǰ�汾:");
		sb.append(verName);
		sb.append("\n�������°�,������£�");
		Common.alert((Activity) context, R.string.prompt, sb.toString(),
				R.drawable.alert_information);
	}

	public static void notNetWok(Context context) {
		StringBuffer sb = new StringBuffer();
		sb.append("�޷�����www.wmptool.com");
		sb.append("\n��ȷ�������Ѿ�����Ŷ��");
		Common.alert((Activity) context, R.string.prompt, sb.toString(),
				R.drawable.alert_information);
	}

	public static void doNewVersionUpdate(final Context context,
			String newVerName, final Handler handler) {
		String verName = Config.getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("����ǰʹ�õİ汾Ϊ��V_");
		sb.append(verName);
		sb.append("\n�������°汾��V_");
		sb.append(newVerName);
		sb.append("\n�Ƿ��������������°�?");
		CustomDialog dialog = new CustomDialog.Builder(context)
				.setTitle("�������")
				.setMessage(sb.toString())
				// ��������
				.setPositiveButton("��������",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								pBar = new ProgressDialog(context);
								pBar.setMessage("�����������°棬���Ժ�Ƭ��...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								downFile(Config.UPDATE_SERVER
										+ Config.UPDATE_APKNAME, context,
										handler);
							}
						})
				.setNeutralButton("�°汾����",
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
				.setNegativeButton("�´���˵",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ���"ȡ��"��ť֮���˳�����
								// finish();
							}
						}).create();// ����
		// ��ʾ�Ի���
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
