package ptool.datacase;

import ptool.datacase.util.Common;
import ptool.datacase.util.SystemConfig;
import ptool.datacase.util.locus.LocusPassWordView;
import ptool.datacase.util.locus.LocusPassWordView.OnCompleteListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocusPasswordSetActivity extends Activity {
	private SystemConfig app;

	private LocusPassWordView lpwv;
	private String password;
	private boolean needverify = true;
	private Button btnReset;
	private Button btnSave;
	private TextView tvTitle;
	private LinearLayout imgTool;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		app = (SystemConfig) this.getApplication();
		if (app.getIsFullScreen()) {
			this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.act_locus_password_set);
		btnReset = (Button) this.findViewById(R.id.btnReset);
		btnSave = (Button) this.findViewById(R.id.btnSave);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		imgTool = (LinearLayout) this.findViewById(R.id.imgTool);
		
		imgTool.setVisibility(View.GONE);
	 
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				password = mPassword;
				if (needverify) {
					if (lpwv.verifyPassword(mPassword)) {
						Common.showMessage(
								LocusPasswordSetActivity.this,
								getResources().getString(
										R.string.config_pass_can_set),
								Toast.LENGTH_SHORT, R.drawable.success);
						lpwv.clearPassword();
						needverify = false;
						password = "";
						imgTool.setVisibility(View.VISIBLE);
						tvTitle.setText(R.string.config_password);
					} else {
						// showDialog("错误的密码,请重新输入!");
						Common.showMessage(LocusPasswordSetActivity.this,
								getResources().getString(R.string.error_pass),
								Toast.LENGTH_SHORT, R.drawable.error);
						password = "";
						lpwv.clearPassword();
					}
				}
			}
		});

		OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btnSave:
//					if (StringUtil.isNotEmpty(password)) {//允许置空
						lpwv.resetPassWord(password);
						Common.showMessage(
								LocusPasswordSetActivity.this,
								getResources().getString(
										R.string.config_pass_ok_locus),
								Toast.LENGTH_SHORT, R.drawable.success);
						LocusPasswordSetActivity.this.finish();
//					} else {
//						Common.showMessage(
//								LocusPasswordSet.this,
//								getResources().getString(
//										R.string.config_new_pass),
//								Toast.LENGTH_SHORT, R.drawable.warning);
//						lpwv.clearPassword();
//					}
					break;
				case R.id.btnReset:
					lpwv.clearPassword();
					break;
				}
			}
		};
		btnSave.setOnClickListener(mOnClickListener);
		btnReset.setOnClickListener(mOnClickListener);
		// 如果密码为空,直接输入密码
		if (lpwv.isPasswordEmpty()) {
			needverify = false;
			imgTool.setVisibility(View.VISIBLE);
			Common.showMessage(LocusPasswordSetActivity.this,
					getResources().getString(R.string.config_new_pass),
					Toast.LENGTH_SHORT, R.drawable.warning);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
