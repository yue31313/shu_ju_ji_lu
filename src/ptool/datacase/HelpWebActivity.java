package ptool.datacase;

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

import ptool.datacase.net.CustomWebViewClient;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.SystemConfig;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HelpWebActivity extends Activity {
	private SystemConfig app;
	private String url;
	private WebView mWebView;
	private TextView tvTitle;
	public ProgressDialog pBar;
	private Handler handler = new Handler();
	private String iconPath;
	private String webType;
	private String fileName;
	private ProgressBar progressBar1;
	private boolean blockLoadingNetworkImage = false;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		 
		app = (SystemConfig) this.getApplication();

		if (app.getIsFullScreen()) {
			this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.act_help_web);

		progressBar1 = (ProgressBar) this.findViewById(R.id.progressBar1);
		progressBar1.setProgress(0);

		Intent intent = getIntent();
		url = intent.getStringExtra("Url");
		iconPath = intent.getStringExtra("IconPath");
		webType = intent.getStringExtra("WebType");

		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText(R.string.help);

		TextView tvSecondTitle = (TextView) this
				.findViewById(R.id.tvSecondTitle);
		tvSecondTitle.setText(((SystemConfig) getApplication()).getVersion());

		mWebView = (WebView) findViewById(R.id.webView);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 缓存
		//webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		mWebView.setWebViewClient(new CustomWebViewClient());
		// 取出右侧滚动条白边
		//mWebView.setScrollbarFadingEnabled(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// 把图片加载放在最后来加载渲染。
				//mWebView.getSettings().setBlockNetworkImage(true);
				blockLoadingNetworkImage =false;
				
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				progressBar1.setProgress(progress);
				if (progress == 100) {
					progressBar1.setVisibility(View.GONE);
					if (blockLoadingNetworkImage) {
						mWebView.getSettings().setBlockNetworkImage(false);
						blockLoadingNetworkImage = false;
					}
				}
			}
		});
		// 实现下载监听
		mWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				fileName = URLUtil.guessFileName(url, contentDisposition,
						mimetype);

				pBar = new ProgressDialog(HelpWebActivity.this);
				pBar.setMessage("正在下载“" + fileName + "”，请稍候片刻...");
				pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				downFile(url, iconPath, fileName);
			}
		});

		if (url == null) {
			if (iconPath != null) {
				tvTitle.setText(R.string.lable_code_icon_down);
				if (iconPath.equals(Constant.IMAGE_PATH)) {
					mWebView.loadUrl("http://www.wmptool.com/icon_datacase.html");
				} 
			} else if (webType != null) {
				mWebView.loadUrl("http://www.wmptool.com/datacase_new_version.html");
			} else {
				mWebView.loadUrl("http://www.wmptool.com/datacase_help.html");
			}
		} else {
			mWebView.loadUrl(url);
		}
		setListener();
	}

	private void setListener() {
		LinearLayout layoutHelp = (LinearLayout) this
				.findViewById(R.id.layoutHelp);
		layoutHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HelpWebActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void downFile(final String url, final String dir,
			final String fileName) {
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
						File file = new File(dir, fileName);
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
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void down() {
		handler.post(new Runnable() {
			public void run() {
				Common.setDataChangedFlag(HelpWebActivity.this);
				Common.showMessage(HelpWebActivity.this, "图标“" + fileName + "”，下载成功！",
						Toast.LENGTH_SHORT, R.drawable.success);
				pBar.cancel();
			}
		});
	}
}
