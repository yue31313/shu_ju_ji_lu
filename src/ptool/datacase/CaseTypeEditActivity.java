package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ptool.datacase.data.ConfigData;
import ptool.datacase.data.service.DBCaseTypeService;
import ptool.datacase.obj.DBCaseType;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.Control;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.control.IconEditorView;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CaseTypeEditActivity extends Activity {
	private int state;// 记录当前的状态
	private int typeNums;
	private String typeId;
	private Button btnTypeName;
	private IconEditorView imageIcon;
	private Button btnTypeIcon;
	private Button btnRemark;
	private LinearLayout btnOk;
	private LinearLayout btnManage;
	private DBCaseType obj;
	private DBCaseTypeService dBCaseTypeService;
	private List<Map<String, Object>> iconItems = new ArrayList<Map<String, Object>>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_type_edit);
		dBCaseTypeService = new DBCaseTypeService(this);
		Intent intent = getIntent();
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_INSERT)) {
			state = Constant.STATE_NEW;
			typeNums = intent.getIntExtra("TypeNums", 0);
			// typeName = intent.getStringExtra("TypeName");
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = Constant.STATE_MOIFY;
			typeId = intent.getStringExtra("TypeId");
			// typeName = intent.getStringExtra("TypeName");
		}
		loadControls();
		setListener();
		initData();
	}

	private void loadControls() {
		btnTypeName = (Button) this.findViewById(R.id.btnTypeName);
		btnTypeIcon = (Button) this.findViewById(R.id.btnTypeIcon);
		btnRemark = (Button) this.findViewById(R.id.btnRemark);
		imageIcon = (IconEditorView) this.findViewById(R.id.icon);
		btnOk = (LinearLayout) this.findViewById(R.id.layoutOk);
		LinearLayout btnCancel = (LinearLayout) this
				.findViewById(R.id.layoutCancel);
		btnManage = (LinearLayout) this.findViewById(R.id.layoutManage);
		btnOk.setOnClickListener(okListener);
		btnCancel.setOnClickListener(cancelListener);
		btnManage.setOnClickListener(manageListener);

	}

	private void setListener() {
		imageIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (iconItems.size() == 0) {
					ConfigData.GetIcon(iconItems, CaseTypeEditActivity.this,
							Constant.IMAGE_PATH);
				}
				Control.IconChoice(CaseTypeEditActivity.this, iconItems,
						btnTypeIcon, imageIcon,
						getResources().getString(R.string.type_icon),
						Constant.IMAGE_PATH);
			}
		});
		btnTypeIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (iconItems.size() == 0) {
					ConfigData.GetIcon(iconItems, CaseTypeEditActivity.this,
							Constant.IMAGE_PATH);
				}
				Control.IconChoice(CaseTypeEditActivity.this, iconItems,
						btnTypeIcon, imageIcon,
						getResources().getString(R.string.type_icon),
						Constant.IMAGE_PATH);
			}
		});

		btnTypeName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Control.ModifyContent(CaseTypeEditActivity.this, btnTypeName,
						R.string.type_name, true);
			}
		});
		btnRemark.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Control.ModifyContent(CaseTypeEditActivity.this, btnRemark,
						R.string.type_mark, false);
			}
		});
	}

	private void initData() {
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		if (state == Constant.STATE_NEW) {
			obj = new DBCaseType();
			btnTypeName.setText("");
			obj.setTypeName("");
			btnTypeIcon.setText("");
			obj.setIconName("");
			btnRemark.setText("");
			obj.setMark("");
			btnManage.setVisibility(View.GONE);
			tvTitle.setText(getResources().getString(R.string.type_new));
		} else {

			obj = dBCaseTypeService.getObj(typeId);
			btnTypeName.setText(obj.getTypeName());
			btnRemark.setText(obj.getMark());
			btnTypeIcon.setText(obj.getIconName());
			Drawable d = Common.getImage(obj.getIconName());
			if (d == null) {
				d = Common.getDefaultImage(this);
			}
			imageIcon.setPhotoDrawable(d);
			btnManage.setVisibility(View.VISIBLE);
			tvTitle.setText(getResources().getString(R.string.type_edit));
		}
	}

	private View.OnClickListener okListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (saveData()) {
				if (state == Constant.STATE_NEW) {
					state = Constant.STATE_MOIFY;
					typeId = obj.getTypeId();
					initData();
				} else {
					CaseTypeEditActivity.this.finish();
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

	private View.OnClickListener manageListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent1 = new Intent(CaseTypeEditActivity.this,
					CharListActivity.class);
			intent1.putExtra("TypeId", typeId);
			intent1.putExtra("TypeName", btnTypeName.getText().toString());
			startActivityForResult(intent1, Constant.REQ_CASE_TYPE);
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

	/**
	 * 检测数据是否发生变化
	 */
	private void checkDatachanged() {
		// 判断内容是否发生变化，提示保存
		if (!obj.getTypeName().equals(btnTypeName.getText())
				|| !obj.getIconName().equals(btnTypeIcon.getText())
				|| !obj.getMark().equals(btnRemark.getText())) {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					CaseTypeEditActivity.this);
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
								CaseTypeEditActivity.this.finish();
							}
						}
					});
			builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					CaseTypeEditActivity.this.finish();
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
			CaseTypeEditActivity.this.finish();
		}
	}

	private Boolean saveData() {
		try {
			if (btnTypeName.getText().toString().equals("")) {
				Animation shakeAnim = AnimationUtils.loadAnimation(this,
						R.anim.shake_x);
				btnTypeName.startAnimation(shakeAnim);
				String message = getResources().getString(R.string.not_null);
				message = MessageFormat.format(message, getResources()
						.getString(R.string.type_name2));
				Common.showMessage(this, message, Toast.LENGTH_SHORT,
						R.drawable.warning);
				return false;
			} else {
				obj.setTypeName(btnTypeName.getText().toString());
			}
			btnOk.setClickable(false);
			Common.setDataChangedFlag(CaseTypeEditActivity.this);
			obj.setIconName(btnTypeIcon.getText().toString());
			obj.setMark(btnRemark.getText().toString());

			dBCaseTypeService = new DBCaseTypeService(this);
			if (state == Constant.STATE_NEW) {
				UUID newId = UUID.randomUUID();
				obj.setSequence(typeNums);
				obj.setTypeId(newId.toString());
				if (dBCaseTypeService.add(obj)) {
					String message = getResources().getString(
							R.string.message_add_success);
					message = MessageFormat.format(message, obj.getTypeName());
					Common.showMessage(CaseTypeEditActivity.this, message,
							Toast.LENGTH_SHORT, R.drawable.success);
				}
			} else {
				if (dBCaseTypeService.update(obj)) {
					String message = getResources().getString(
							R.string.message_update_success);
					message = MessageFormat.format(message, obj.getTypeName());
					Common.showMessage(CaseTypeEditActivity.this, message,
							Toast.LENGTH_SHORT, R.drawable.success);
				}
			}
			new Thread() {
				@Override
				public void run() {
					//
					handler.sendEmptyMessage(0);
				}
			}.start();
			return true;
		} catch (Exception e) {
			Common.showMessage(CaseTypeEditActivity.this, e.getMessage(),
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
		}
	};

	// 返回后，刷新数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle localBundle = Common.getIntentExtras(data);
		if ((localBundle != null)
				&& (localBundle.getString("DataChanged").equals("Y"))) {
			iconItems.clear();
			Common.setDataChangedFlag(this);
		}
	}

}
