package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.data.DataCommon;
import ptool.datacase.data.DatabaseConstant;
import ptool.datacase.data.DatabaseHelper;
import ptool.datacase.data.service.SysPassService;
import ptool.datacase.util.Common;
import ptool.datacase.util.Control;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.SystemConfig;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SysConfigActivity extends Activity {
	private CustomDialog dialog;

	private SystemConfig app;
	private String backupNums;
	private String passWord;
	private String autoBackup;
	private String onlyInt;
	private String onlyPicture;

	private Button btnBackupNums;
	private Button btnBackupResume;
	private Button btnPassword;
	private Button btnPasswordValue;
	private ToggleButton tbtnAutoBackup;
	private ToggleButton tbtnOnlyInt;
	private ToggleButton tbtnOnlyPicture;
	private TableRow tableRowBackup;
	private TableRow tableRowOnlyInt;
	private Button btnBack;
	 
	private SysPassService sysPassService;
	private List<Map<String, Object>> backupFiles = new ArrayList<Map<String, Object>>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		sysPassService = new SysPassService(this);
		app = (SystemConfig) this.getApplication();
		setContentView(R.layout.act_sys_config);
		

		loadControls();
		setListener();

	}

	private void loadControls() {
		try {
			TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
			tvTitle.setText(R.string.case_config);

			TextView tvDatabasePath = (TextView) this
					.findViewById(R.id.tvDatabasePath);
			tvDatabasePath.setText("SD/PToolDataCase/an_db.db");

			TextView tvBackupPath = (TextView) this.findViewById(R.id.tvBackupPath);
			tvBackupPath.setText("SD/PToolDataCase/Backup");
			
			ImageButton imgBtnRight = (ImageButton) this
			.findViewById(R.id.imgBtnRight);
			imgBtnRight.setVisibility(View.GONE);
			btnBack = (Button) this
					.findViewById(R.id.btnBack);
			
			tbtnOnlyPicture = (ToggleButton) this
					.findViewById(R.id.tbtnOnlyPicture);
			tbtnOnlyInt = (ToggleButton) this.findViewById(R.id.tbtnOnlyInt);
			tbtnAutoBackup = (ToggleButton) this
					.findViewById(R.id.tbtnAutoBackup);
			btnBackupResume = (Button) this.findViewById(R.id.btnBackupResume);
				btnPassword = (Button) this.findViewById(R.id.btnPassword);
			btnPasswordValue = (Button) this
					.findViewById(R.id.btnPasswordValue);
			btnBackupNums = (Button) this.findViewById(R.id.btnBackupNums);
					tableRowBackup = (TableRow) this.findViewById(R.id.tableRowBackup);
				tableRowOnlyInt = (TableRow) this
					.findViewById(R.id.tableRowOnlyInt);
			// ---------------------------------------------------------------------
			btnPasswordValue.setText(sysPassService.getValue("main")
					.toUpperCase());
			passWord = btnPasswordValue.getText().toString();
			if (sysPassService.getValue("only_picture").equals("Y")) {
				tbtnOnlyPicture.setChecked(true);
					tableRowOnlyInt.setVisibility(View.GONE);
			} else {
				tbtnOnlyPicture.setChecked(false);
				tableRowOnlyInt.setVisibility(View.VISIBLE);
			}
			onlyPicture = tbtnOnlyPicture.getText().toString();

			if (sysPassService.getValue("only_int").equals("Y")) {
				tbtnOnlyInt.setChecked(true);
			} else {
				tbtnOnlyInt.setChecked(false);
			}
			onlyInt = tbtnOnlyInt.getText().toString();
			 
			backupNums = sysPassService.getValue("auto_backup_nums");
			if (backupNums.equals(""))
				backupNums = "10";
			btnBackupNums.setText(backupNums);
			if (sysPassService.getValue("auto_backup").equals("Y")) {
				tbtnAutoBackup.setChecked(true);
				tableRowBackup.setVisibility(View.VISIBLE);
			} else {
				tbtnAutoBackup.setChecked(false);
				tableRowBackup.setVisibility(View.GONE);
			}
			autoBackup = tbtnAutoBackup.getText().toString();

		} catch (Exception ex) {
			Common.showMessage(SysConfigActivity.this, ex.getMessage(),
					Toast.LENGTH_SHORT, R.drawable.error);
		}
	}

	private void setListener() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkDatachanged();
			}
		});
		
		tbtnOnlyPicture
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							tableRowOnlyInt.setVisibility(View.GONE);
						} else {
							tableRowOnlyInt.setVisibility(View.VISIBLE);
						}
					}
				});
		tbtnOnlyInt.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					Common.showMessage(SysConfigActivity.this, getResources()
							.getString(R.string.config_only_int_warning),
							Toast.LENGTH_LONG, R.drawable.warning);
				}
			}
		});
		 
		tbtnAutoBackup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							tableRowBackup.setVisibility(View.VISIBLE);
						} else {
							tableRowBackup.setVisibility(View.GONE);
						}
					}
				});

		btnPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changePassword();
			}
		});

		btnBackupNums.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Control.ModifyContent(SysConfigActivity.this, btnBackupNums,
						R.string.config_autobackup_nums, true, true);
			}
		});

		btnBackupResume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (backupFiles.size() == 0) {
					DataCommon.getBackupFilesData(backupFiles);
				}
				if (backupFiles.size() > 0) {
					backupFileChoice();
				} else {
					Common.showMessage(SysConfigActivity.this, getResources()
							.getString(R.string.lable_backup_empty),
							Toast.LENGTH_SHORT, R.drawable.warning);
				}
			}
		});
	}

	private void backupFileChoice() {
		try {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					SysConfigActivity.this);
			builder.setTitle(R.string.lable_select_backup);
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.list_view_select, null);
			builder.setView(layout);

			// 绑定Layout里面的ListView
			ListView list = (ListView) layout.findViewById(R.id.lvSelect);

			// 生成适配器的Item和动态数组对应的元素
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					this,
					backupFiles,// 数据源
					R.layout.list_item,// ListItem的XML实现
					// 动态数组与ImageItem对应的子项
					new String[] { "ListItemImage", "ListItemTitle" },
					new int[] { R.id.ListItemImage, R.id.ListItemTitle });

			// 添加并且显示
			list.setAdapter(listItemAdapter);
			list.setSelectionAfterHeaderView();
			// 添加点击
			list.setOnItemClickListener(new OnItemClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					HashMap<String, Object> item = (HashMap<String, Object>) arg0
							.getItemAtPosition(arg2);
					final String fileName = item.get("ListItemTitle")
							.toString();
					String messageConfirm = getResources().getString(
							R.string.lable_backup_confirm);
					messageConfirm = MessageFormat.format(messageConfirm,
							fileName.replace(
									DatabaseConstant.DATABASE_BACKUP_NAME, ""));

					CustomDialog.Builder confirm = new CustomDialog.Builder(
							SysConfigActivity.this);
					confirm.setMessage(messageConfirm);
					confirm.setTitle(R.string.prompt);
					confirm.setIcon(R.drawable.alert_question);
					confirm.setPositiveButton(R.string.lable_yes,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dlg,
										int which) {
									DataCommon.resumeDatabase(fileName);
									DataCommon.checkDatabase(SysConfigActivity.this,
											app);
									String messageOk = getResources()
											.getString(
													R.string.lable_resume_success);
									messageOk = MessageFormat.format(
											messageOk,
											fileName.replace(
													DatabaseConstant.DATABASE_BACKUP_NAME,
													""));
									Common.showMessage(SysConfigActivity.this,
											messageOk, Toast.LENGTH_SHORT,
											R.drawable.success);

									dlg.dismiss();
									dialog.dismiss();
									Common.setDataChangedFlag(SysConfigActivity.this);
									SysConfigActivity.this.finish();
								}
							});
					confirm.setNeutralButton(R.string.lable_no,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dlg,
										int which) {
									dlg.dismiss();
									dialog.dismiss();
								}
							});
					confirm.create().show();
				}
			});

			dialog = builder.create();
			// dialog.setInverseBackgroundForced(true);
			dialog.show();
		} catch (Exception e) {
			Common.showMessage(this, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			Log.e("SysConfig", "备份文件列表载入失败！" + e.getMessage());
			e.printStackTrace();
		}
	}

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

	private void checkDatachanged() {
		if (!backupNums.equals(btnBackupNums.getText())
				|| !passWord.equals(btnPasswordValue.getText())
				|| !onlyInt.equals(tbtnOnlyInt.getText())
				|| !onlyPicture.equals(tbtnOnlyPicture.getText())
				|| !autoBackup.equals(tbtnAutoBackup.getText())) {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					SysConfigActivity.this);
			builder.setMessage(R.string.lable_data_changed);
			builder.setTitle(R.string.lable_close_info);
			builder.setIcon(R.drawable.alert_question);
			builder.setPositiveButton(R.string.lable_yes,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 保存
							if (saveConfig()) {

								dialog.dismiss();
								SysConfigActivity.this.finish();
							}
						}
					});
			builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					SysConfigActivity.this.finish();
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
			SysConfigActivity.this.finish();
		}
	}

	private boolean saveConfig() {
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		SQLiteDatabase db = databaseHelper.openDatabase();
		try {
			if (!passWord.equals(btnPasswordValue.getText())) {
				sysPassService.update(db, "main", btnPasswordValue.getText()
						.toString());
			}
			if (!onlyPicture.equals(tbtnOnlyPicture.getText())) {
				if (tbtnOnlyPicture.isChecked()) {
					sysPassService.update(db, "only_picture", "Y");
					sysPassService.update(db, "main", "");
				} else {
					sysPassService.update(db, "only_picture", "N");
					sysPassService.update(db, "locus", "");
				}
			}
			if (!onlyInt.equals(tbtnOnlyInt.getText())) {
				if (tbtnOnlyInt.isChecked()) {
					sysPassService.update(db, "only_int", "Y");
				} else {
					sysPassService.update(db, "only_int", "N");
				}
			}
		
			if (!autoBackup.equals(tbtnAutoBackup.getText())) {
				if (tbtnAutoBackup.isChecked()) {
					sysPassService.update(db, "auto_backup", "Y");
				} else {
					sysPassService.update(db, "auto_backup", "N");
				}
			}
			if (!backupNums.equals(btnBackupNums.getText())) {
				if (!btnBackupNums.getText().equals("")
						&& !btnBackupNums.getText().equals("0"))
					sysPassService.update(db, "auto_backup_nums", btnBackupNums
							.getText().toString());
			}
			SysConfigActivity.this.finish();
			return true;
		} catch (Exception ex) {
			Log.e("SysConfig", "系统配置修改失败！" + ex.getMessage());
			return false;
		} finally {
			db.close();
		}
	}

	// 创建菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 建立菜单
		menu.add(0, Menu.FIRST, 1, R.string.lable_config_save).setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(0, Menu.FIRST + 1, 2, R.string.lable_config_exit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}

	// 菜单选择事件处理
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST: // 保存设置
			saveConfig();
			break;
		case Menu.FIRST + 1: // 退出
			SysConfigActivity.this.finish();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void changePassword() {
		if (tbtnOnlyPicture.isChecked()) {
			Intent intent = new Intent();
			intent.setClass(this, LocusPasswordSetActivity.class);
			startActivity(intent);
		} else {
			if (btnPasswordValue.getText().equals("")
					|| btnPasswordValue.getText().equals(Common.MD5(""))) {
				newPassword();
			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(
						SysConfigActivity.this);
				builder.setTitle(R.string.title_pass);
				builder.setIcon(R.drawable.key);
				LayoutInflater inflater = (LayoutInflater) this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.password_check, null);
				builder.setView(layout);

				final EditText myEditText = (EditText) layout
						.findViewById(R.id.editText_pass);
				if (tbtnOnlyInt.isChecked()) {
					myEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				Common.showInputMethod(this);
				builder.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 转译输入的密码明文
						String passInput = myEditText.getText().toString();
						if (passInput.equals(btnPasswordValue.getText())
								|| Common.MD5(passInput).equals(
										btnPasswordValue.getText())) {
							Common.showMessage(SysConfigActivity.this, getResources()
									.getString(R.string.message_loading),
									Toast.LENGTH_SHORT, R.drawable.success);

							dialog.dismiss();
							newPassword();

						} else {
							Common.showMessage(SysConfigActivity.this, getResources()
									.getString(R.string.error_pass),
									Toast.LENGTH_SHORT, R.drawable.error);
							changePassword();
						}
					}
				});

				builder.setNegativeButton(R.string.cancel,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		}
	}

	private void newPassword() {
		CustomDialog.Builder builder = new CustomDialog.Builder(SysConfigActivity.this);
		builder.setTitle(R.string.config_password);
		builder.setIcon(R.drawable.lock);
		LayoutInflater inflater = (LayoutInflater) SysConfigActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.password_change, null);
		builder.setView(layout);

		final EditText etPassword = (EditText) layout
				.findViewById(R.id.etPassword);
		final EditText etPasswordRepeat = (EditText) layout
				.findViewById(R.id.etPasswordRepeat);
		if (tbtnOnlyInt.isChecked()) {
			etPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
			etPasswordRepeat.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		Common.showInputMethod(this);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String pass = etPassword.getText().toString();
				String passRepeat = etPasswordRepeat.getText().toString();

				if (pass.equals(passRepeat)) {
					btnPasswordValue.setText(Common.MD5(pass));
					// Log.i("New Password", Common.MD5(pass));
					if (pass.equals("")) {
						Common.alert(SysConfigActivity.this, R.string.prompt,
								R.string.config_pass_clear, R.drawable.success);
					} else {
						String messageOk = getResources().getString(
								R.string.config_pass_ok);
						messageOk = MessageFormat.format(messageOk, pass);
						Common.alert(SysConfigActivity.this, R.string.prompt,
								messageOk, R.drawable.success);
					}
					dialog.dismiss();

				} else {
					Common.showMessage(SysConfigActivity.this, getResources()
							.getString(R.string.config_pass_deferent),
							Toast.LENGTH_SHORT, R.drawable.error);
					changePassword();
				}
			}
		});

		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// 返回后，刷新数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle localBundle = Common.getIntentExtras(data);
		if ((localBundle != null)
				&& (localBundle.getString("DataChanged").equals("Y"))) {
			Common.setDataChangedFlag(SysConfigActivity.this);
		}
	}
}
