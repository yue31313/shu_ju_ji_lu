package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

import ptool.datacase.data.service.DBCaseValuesService;
import ptool.datacase.obj.DBCaseValues;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.Control;
import ptool.datacase.util.CustomDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ValueEditActivity extends Activity {
	private int state;// 记录当前的状态
	private int indexValue;
	private String typeId;
	private String charId;
	private Button btnValueName;
	private TextView tvRecordIndex;
	private TextView tvTitle;
	private LinearLayout btnOk;
	private LinearLayout btnOkNew;
	private ArrayList<String> valueIdList = new ArrayList<String>();

	private DBCaseValuesService dBCaseValuesService;
	private DBCaseValues obj;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_value_edit);

		dBCaseValuesService = new DBCaseValuesService(this);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		btnValueName = (Button) this.findViewById(R.id.btnValueName);
		tvRecordIndex = (TextView) this.findViewById(R.id.tvSecondTitle);
		
		Intent intent = getIntent();
		String action = intent.getAction();
		typeId = intent.getStringExtra("TypeId");
		charId = intent.getStringExtra("CharId");
		if (action.equals(Intent.ACTION_INSERT)) {
			state = Constant.STATE_NEW;
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = Constant.STATE_MOIFY;
			indexValue = intent.getIntExtra("IndexValue", 0);
			valueIdList = intent.getStringArrayListExtra("ValueIdList");
		}
		// -begin--载入表单项--------------------------------------
		 	 	setListener();
			initData();
	 	// -end--载入表单项----------------------------------------

		// -begin--工具栏设置--------------------------------------
		setToolBar();
		// -end--工具栏设置----------------------------------------
	}

	private void initData() {
		// 判断是否是新增
		if (state == Constant.STATE_NEW) {
			obj = new DBCaseValues();

			btnValueName.setText("");
			obj.setValueName("");
			obj.setCharId(charId);
			obj.setTypeId(typeId);
			tvTitle.setText(R.string.value_new);

		} else {
			setData();
			
			tvTitle.setText(R.string.value_edit);
		}
	}
	
	private void setData()
	{
		obj = dBCaseValuesService.getObj(valueIdList.get(indexValue));
		btnValueName.setText(obj.getValueName());
		tvRecordIndex.setText((indexValue + 1) + "/" +valueIdList.size());
	}
	
	private void setListener() {
		btnValueName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Control.ModifyContent(ValueEditActivity.this, btnValueName,
						R.string.value_name, true);
			}
		});
	}
	
	private void setToolBar() {
		try {
			LinearLayout btnGetBack = (LinearLayout) this
					.findViewById(R.id.layoutGetBack);
			LinearLayout btnGetNext = (LinearLayout) this
					.findViewById(R.id.layoutGetNext);
		 	btnOkNew = (LinearLayout) this
					.findViewById(R.id.layoutOkNew);
		 	if (state == Constant.STATE_NEW) {
				btnOkNew.setOnClickListener(okNewListener);
				btnGetBack.setVisibility(View.GONE);
				btnGetNext.setVisibility(View.GONE);
			} else {
				btnOkNew.setVisibility(View.GONE);
				if (valueIdList.size() == 1) {
					btnGetBack.setVisibility(View.GONE);
					btnGetNext.setVisibility(View.GONE);
				} else {
					btnGetBack.setOnClickListener(getBackListener);
					btnGetNext.setOnClickListener(getNextListener);
				}
			}
			btnOk = (LinearLayout) this
					.findViewById(R.id.layoutOk);
			LinearLayout btnCancel = (LinearLayout) this
					.findViewById(R.id.layoutCancel);

			btnOk.setOnClickListener(okListener);
			btnCancel.setOnClickListener(cancelListener);
		} catch (Exception ex) {
			Log.e("ValueEditActivity.setToolBar", "获取句柄失败！" + ex.getMessage());
		}
	}

	private View.OnClickListener getBackListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (indexValue == 0) {
				indexValue = valueIdList.size() - 1;
			} else {
				indexValue--;
			}
			setData();
		}
	};

	private View.OnClickListener getNextListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (indexValue == valueIdList.size() - 1) {
				indexValue = 0;
			} else {
				indexValue++;
			}
			setData();
		}
	};

	private View.OnClickListener okNewListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (saveData()) {
				initData();
			}
		}
	};

	private View.OnClickListener okListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (saveData()) {
				if (state == Constant.STATE_NEW) {
					ValueEditActivity.this.finish();
				} else {
					setData();
				}
			}
		}
	};
	private View.OnClickListener cancelListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			checkDatachanged();
		}
	};
	
	private void checkDatachanged() {
		// 判断内容是否发生变化，提示保存
		if ( !obj.getValueName().equals(btnValueName.getText())) {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					ValueEditActivity.this);
			builder.setMessage(R.string.lable_data_changed);
			builder.setTitle(R.string.lable_close_info);
			builder.setIcon(R.drawable.alert_question);
			builder.setPositiveButton(R.string.lable_yes,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 保存
							if (saveData()) {
								dialog.dismiss();
								ValueEditActivity.this.finish();
							}
						}
					});
			builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					ValueEditActivity.this.finish();
				}
			});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} else {
			ValueEditActivity.this.finish();
		}
	}

	private boolean canSave() {
		if (btnValueName.getText().equals("")) {
			Animation shakeAnim = AnimationUtils.loadAnimation(
					this, R.anim.shake_x);
			btnValueName.startAnimation(shakeAnim);
			String message = getResources().getString(R.string.not_null);
			message = MessageFormat.format(message, getResources()
					.getString(R.string.value_name));
			Common.showMessage(this, message, Toast.LENGTH_SHORT,
					R.drawable.warning);
			return false;
		}
		return true;
	}

	/**
	 * 保存账户
	 * 
	 * @return
	 */
	private boolean saveData() {
		try {
			if (!canSave()) {
				return false;
			}
			btnOk.setClickable(false);
			btnOkNew.setClickable(false);
			obj.setValueName(btnValueName.getText()
					.toString());
			if (state == Constant.STATE_NEW) {
				UUID newId = UUID.randomUUID();
				obj.setValueId(newId.toString());

				if (dBCaseValuesService.add(obj)) {
					String message = getResources().getString(
							R.string.char_add_success);
					message = MessageFormat.format(message,
							obj.getValueName());
					Common.showMessage(ValueEditActivity.this, message,
							Toast.LENGTH_SHORT, R.drawable.success);
				} else {
					Common.showMessage(ValueEditActivity.this, getResources()
							.getString(R.string.message_save_error),
							Toast.LENGTH_SHORT, R.drawable.error);
				}
			} else {
				if (dBCaseValuesService.update(obj)) {
					String message = getResources().getString(
							R.string.message_update_success);
					message = MessageFormat.format(message,
							obj.getValueName());
					Common.showMessage(ValueEditActivity.this, message,
							Toast.LENGTH_SHORT, R.drawable.success);
				} else {
					Common.showMessage(ValueEditActivity.this, getResources()
							.getString(R.string.message_save_error),
							Toast.LENGTH_SHORT, R.drawable.error);
				}
			}
			Common.setDataChangedFlag(ValueEditActivity.this);
			new Thread() {
				@Override
				public void run() {
					//
					handler.sendEmptyMessage(0);
				}
			}.start();
			// ------------------------------------------------------------------------------
			return true;
		} catch (Exception e) {
			Common.showMessage(ValueEditActivity.this, e.getMessage(),
					Toast.LENGTH_SHORT, R.drawable.error);
			return false;
		}
	}
	
	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			btnOk.setClickable(true);
			btnOkNew.setClickable(true);
		}
	};
	
	/**
	 * 按键事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			checkDatachanged();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	
}
