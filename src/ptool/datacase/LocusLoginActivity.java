package ptool.datacase;

import ptool.datacase.util.Common;
import ptool.datacase.util.locus.LocusPassWordView;
import ptool.datacase.util.locus.LocusPassWordView.OnCompleteListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
 

public class LocusLoginActivity extends Activity
{
	private LocusPassWordView lpwv;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
		setContentView(R.layout.act_locus_login);
	
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{
				//���������ȷ,�������ҳ�档
				if (lpwv.verifyPassword(mPassword))
				{
					Intent intent = new Intent();
					intent.setClass(LocusLoginActivity.this, MainActivity.class);
					startActivity(intent);
					LocusLoginActivity.this.finish();
				}
				else
				{
					Common.showMessage(LocusLoginActivity.this,
							getResources().getString(R.string.error_pass),
							Toast.LENGTH_SHORT, R.drawable.error);
					lpwv.clearPassword();
				}
			}
		});

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// �������Ϊ��,�������������Ľ���
//		View noSetPassword = (View) this.findViewById(R.id.tvNoSetPassword);
//		if (lpwv.isPasswordEmpty())
//		{
//			noSetPassword.setOnClickListener(new OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					Intent intent = new Intent(LocusLogin.this, LocusPasswordSet.class);
//					// ���µ�Activity
//					startActivity(intent);
//				}
//			});
//			noSetPassword.setVisibility(View.VISIBLE);
//		}
//		else
//		{
//			noSetPassword.setVisibility(View.GONE);
//		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

}
