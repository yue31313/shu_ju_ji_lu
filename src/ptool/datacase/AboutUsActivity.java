package ptool.datacase;

import ptool.datacase.net.AutoUpdate;
import ptool.datacase.net.Config;
import ptool.datacase.net.UpdateEntity;
import ptool.datacase.util.Constant;
import ptool.datacase.util.SystemConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutUsActivity extends Activity {
	SystemConfig app;
	private int vercode;
	private UpdateEntity updateObj;
	private static Handler handler = new Handler();

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		app = (SystemConfig) this.getApplication();
		updateObj = new UpdateEntity();

		setContentView(R.layout.act_about_us);
		TextView msgTextView = (TextView) this
				.findViewById(R.id.textViewVersion);
		msgTextView.setText(((SystemConfig) getApplication()).getVersion());

		Button btnShare = (Button) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(
						Intent.EXTRA_SUBJECT,
						"�������á������ִ�����ͦ���õģ���Ҳ���԰ɣ����ص�ַ��http://www.wmptool.com/Download/DataCase.apk");
				intent.putExtra(
						Intent.EXTRA_TEXT,
						"�������á������ִ�����ͦ���õģ���Ҳ���԰ɣ����ص�ַ��http://www.wmptool.com/Download/DataCase.apk");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, getResources().getString(R.string.about_share)));
			}
		});

		Button btnUpdateLog = (Button) findViewById(R.id.btnUpdateLog);
		btnUpdateLog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(
						new Intent(AboutUsActivity.this, HelpWebActivity.class),
						Constant.REQ_HELP_WEB);
			}
		});

		Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread() {
					@Override
					public void run() {
						// ��Ҫ��ʱ�����ķ���
						if (AutoUpdate.getServerVerCode(updateObj)) {
							vercode = Config.getVerCode(AboutUsActivity.this);
						}
						// ��handler����Ϣ
						handler2.sendEmptyMessage(0);
					}
				}.start();
			}
		});

		
		ImageView imageViewClose = (ImageView) this
				.findViewById(R.id.imageViewClose);
		imageViewClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutUsActivity.this.finish();
			}
		});
	}

	/**
	 * ��Handler������UI
	 */
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (updateObj.getNewVerCode() > vercode) {
				AutoUpdate.doNewVersionUpdate(AboutUsActivity.this,
						updateObj.getNewVerName(), handler);
			}else if(updateObj.getNewVerCode() == 0){
				AutoUpdate.notNetWok(AboutUsActivity.this);
			}
			else {
				AutoUpdate.notNewVersionShow(AboutUsActivity.this);
			}
		}
	};

}
