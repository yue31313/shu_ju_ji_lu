package ptool.datacase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ptool.datacase.data.DataCommon;
import ptool.datacase.data.service.DBCaseCharsService;
import ptool.datacase.data.service.DBCaseDataService;
import ptool.datacase.data.service.DBCaseTypeService;
import ptool.datacase.obj.DBCaseData;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.CustomProgressDialog;
import ptool.datacase.util.PopupToolType;
import ptool.datacase.util.PopupWindowUtil;
import ptool.datacase.util.control.DataListViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DataListActivity extends Activity {
	private CustomProgressDialog progressDialog;
	private String typeId;
	private String typeName;
	private DBCaseDataService dBCaseDataService;
	private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	private ArrayList<String> dataIdList = new ArrayList<String>();
	private List<Map<String, Object>> charItems = new ArrayList<Map<String, Object>>();

	private int showFirstVisible;
	private ListView list;
	private ProgressBar progressBar1;
	private EditText etSearch;
	private ImageButton imgBtnClear;
	private LinearLayout layNew;
	private Button btnBack;
	private ImageButton imgBtnRight;

	private TextView tvRecordNums;
	private TextView tvCountName;
	private TextView tvCountValue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_data_list);

		dBCaseDataService = new DBCaseDataService(this);
		showFirstVisible = 0;

		Intent intent = getIntent();
		typeId = intent.getStringExtra("TypeId");
		typeName = intent.getStringExtra("TypeName");

		loadControls();
		loadData();

	}

	private void loadControls() {
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(typeName);

		layNew = (LinearLayout) findViewById(R.id.layNew);

		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.setText("");
		btnBack = (Button) findViewById(R.id.btnBack);
		imgBtnClear = (ImageButton) findViewById(R.id.imgBtnClear);
		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		ImageButton imgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);
		imgBtnSearch.requestFocus();
		tvRecordNums = (TextView) findViewById(R.id.tvRecordNums);
		tvCountName = (TextView) findViewById(R.id.tvCountName);
		tvCountValue = (TextView) findViewById(R.id.tvCountValue);
		list = (ListView) findViewById(R.id.lvDataList);
		setListener();
	}

	private void setListener() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DataListActivity.this.finish();
			}
		});

		imgBtnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				loadData2();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		layNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent1 = new Intent(DataListActivity.this,
						DataNoteActivity.class);
				intent1.setAction(Intent.ACTION_INSERT);
				intent1.putExtra("TypeId", typeId);
				intent1.putExtra("TypeName", typeName);
				startActivityForResult(intent1, Constant.REQ_DATA_NOTE);
			}
		});
		imgBtnRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupWindowTool dw = new PopupWindowTool(v, mHandler);
				dw.setBackgroundDrawable(DataListActivity.this.getResources()
						.getDrawable(R.drawable.title_function_bg));
				dw.showLikePopDownMenu();
			}
		});
	}

	private void loadData2() {
		imgBtnClear.setVisibility(View.GONE);
		progressBar1.setVisibility(View.VISIBLE);
		// 新建线程
		new Thread() {
			@Override
			public void run() {
				// 需要花时间计算的方法
				searchData();
				// 向handler发消息
				handler2.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressBar1.setVisibility(View.GONE);
			setData();
		}
	};

	private void loadData() {
		CustomProgressDialog.Builder builder = new CustomProgressDialog.Builder(
				DataListActivity.this);
		builder.setTitle(R.string.data_loading);
		progressDialog = builder.create();
		progressDialog.setCancelable(false);
		progressDialog.show();
		// 新建线程
		new Thread() {
			@Override
			public void run() {
				// 需要花时间计算的方法
				searchData();
				// 向handler发消息
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void searchData() {
		DBCaseCharsService dBCaseCharsService = new DBCaseCharsService(this);
		dBCaseCharsService.queryByType(this, charItems, typeId);

		StringBuilder str = new StringBuilder();
		if (!(etSearch.getText().toString().trim().equals(""))) {
			str.append(" and (");
			for (int i = 1; i <= charItems.size(); i++) {
				if(i == 1)
				{
				str.append(" data1 like '%");
				str.append(etSearch.getText().toString().trim());
				str.append("%'");
				}else
				{
				str.append(" or data"+i+" like '%");
				str.append(etSearch.getText().toString().trim());
				str.append("%'");
				}
			}
			str.append(" )");
		}
		
		// if (charItems.size() >= 4) {
		// if (!(etSearch.getText().toString().trim().equals(""))) {
		// str.append(" and (");
		// str.append(" data1 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" or data2 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" or data3 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" or data4 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" )");
		// }
		// } else if (charItems.size() == 3) {
		// if (!(etSearch.getText().toString().trim().equals(""))) {
		// str.append(" and (");
		// str.append(" data1 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" or data2 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" or data3 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" )");
		// }
		// } else if (charItems.size() == 2) {
		// if (!(etSearch.getText().toString().trim().equals(""))) {
		// str.append(" and (");
		// str.append(" data1 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" or data2 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" )");
		// }
		// } else if (charItems.size() == 1) {
		// if (!(etSearch.getText().toString().trim().equals(""))) {
		// str.append(" and (");
		// str.append(" data1 like '%");
		// str.append(etSearch.getText().toString().trim());
		// str.append("%'");
		// str.append(" )");
		// }
		// }
		dBCaseDataService.queryAll(this, listItems, typeId, str.toString());
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			progressDialog.dismiss();
			progressBar1.setVisibility(View.GONE);
			setData();
		}
	};

	private void setData() {
		if (etSearch.getText().toString().trim().equals("")) {
			imgBtnClear.setVisibility(View.GONE);
		} else {
			imgBtnClear.setVisibility(View.VISIBLE);
		}
		String viewType = DataCommon.getConfigValue(this, "ViewType+" + typeId);

		// 生成适配器的Item和动态数组对应的元素
		DataListViewAdapter listViewAdapter = new DataListViewAdapter(this,
				listItems, charItems, mAdpBtnClickHandler, viewType); // 创建适配器
		list.setAdapter(listViewAdapter);
		list.setSelection(showFirstVisible);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// 打开编辑界面
				showFirstVisible = list.getFirstVisiblePosition();
				editDataNote(arg2);
			}
		});
		// 添加长按点击
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			// 长点时处理
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				final int indexValue = arg2;
				CustomDialog dialog = new CustomDialog.Builder(
						DataListActivity.this)
						.setTitle(R.string.operate_menu)
						.setItems(
								new String[] {
										getResources().getString(
												R.string.lable_new),
										getResources().getString(
												R.string.lable_edit),
										getResources().getString(
												R.string.lable_del) },
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if (which == 0) {// 添加
											addDataNote();
										} else if (which == 1) {// 修改
											showFirstVisible = list
													.getFirstVisiblePosition();
											editDataNote(indexValue);
										} else if (which == 2) {// 删除
											showFirstVisible = list
													.getFirstVisiblePosition();
											deleteDataNote(indexValue);
										}
									}
								})
						.setPositiveButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();
				dialog.show();
				return false;
			};
		});
		// 底部统计信息显示
		setBottomView();
	}

	private void setBottomView() {
		dataIdList.clear();
		for (int i = 0; i < listItems.size(); i++) {
			dataIdList.add(listItems.get(i).get("ListDataId").toString());
		}
		tvRecordNums.setText(listItems.size()
				+ getResources().getString(R.string.lable_unit_tiao));

		// 统计计算
		String name = "";
		int countIndex = -1;
		String countType = "";
		for (int i = 0; i < charItems.size(); i++) {
			if (charItems.get(i).get("char_count").equals("是")) {
				countIndex = i;
				if (countIndex <= 3) {
					countType = charItems.get(i).get("count_type").toString();
					name = charItems.get(i).get("char_name").toString();
					if (charItems.get(i).get("count_type").equals("0")) {
						name = name + "总和：";
					} else if (charItems.get(i).get("count_type").equals("1")) {
						name = name + "平均：";
					} else if (charItems.get(i).get("count_type").equals("2")) {
						// name = name +"计数：";
					} else if (charItems.get(i).get("count_type").equals("3")) {
						name = name + "最大：";
					} else if (charItems.get(i).get("count_type").equals("4")) {
						name = name + "最小：";
					}
					tvCountName.setText(name);
				}
				break;
			}
		}
		if (countIndex > -1 && countIndex <= 3) {
			double val = 0;
			double temp = 0;
			String res = "";
			for (int i = 0; i < listItems.size(); i++) {
				try {
					temp = Common.parseDouble(listItems.get(i)
							.get("ListItem" + (countIndex + 1)).toString());
				} catch (Exception e) {
				}
				if (countType.equals("0") || countType.equals("1")) {
					val = val + temp;
				} else if (countType.equals("3")) {
					if (val == 0)
						val = temp;
					if (val < temp)
						val = temp;
				} else if (countType.equals("4")) {
					if (val == 0)
						val = temp;
					if (val > temp)
						val = temp;
				}
			}
			if (countType.equals("1")) {
				res = Common.covertToDouble(
						val / listItems.size(),
						Common.parseInt(charItems.get(countIndex)
								.get("char_decimal").toString()));
			} else {
				res = Common.covertToDouble(
						val,
						Common.parseInt(charItems.get(countIndex)
								.get("char_decimal").toString()));
			}
			tvCountValue.setText(res
					+ charItems.get(countIndex).get("char_unit").toString());
		}
	}

	private void addDataNote() {
		Intent intent1 = new Intent(DataListActivity.this,
				DataNoteActivity.class);
		intent1.setAction(Intent.ACTION_INSERT);
		intent1.putExtra("TypeId", typeId);
		intent1.putExtra("TypeName", typeName);
		startActivityForResult(intent1, Constant.REQ_DATA_NOTE);
	}

	private void editDataNote(int indexValue) {
		Intent intent = new Intent(DataListActivity.this,
				DataNoteActivity.class);
		intent.putExtra("IndexValue", indexValue);
		intent.putExtra("TypeId", typeId);
		intent.putExtra("TypeName", typeName);
		intent.putStringArrayListExtra("DataIdList", dataIdList);
		intent.setAction(Intent.ACTION_EDIT);
		startActivityForResult(intent, Constant.REQ_DATA_NOTE);
	}

	private void deleteDataNote(int indexValue) {
		final String selectedId = listItems.get(indexValue).get("ListDataId")
				.toString();
		CustomDialog.Builder builder = new CustomDialog.Builder(
				DataListActivity.this);
		builder.setMessage(R.string.lable_delete_confirm);
		builder.setTitle(R.string.prompt);
		builder.setIcon(R.drawable.alert_question);
		builder.setPositiveButton(R.string.lable_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBCaseData obj = dBCaseDataService.getObj(selectedId, typeId);
				dBCaseDataService.delete(obj);
				loadData();
				Common.showMessage(DataListActivity.this, getResources()
						.getString(R.string.message_delete_success),
						Toast.LENGTH_SHORT, R.drawable.success);
			}
		});
		builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	public Handler mAdpBtnClickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PopupToolType.BUTTON_ONE:
				addDataNote();
				break;
			case PopupToolType.BUTTON_TWO:
				editDataNote(msg.arg1);
				break;
			case PopupToolType.BUTTON_THREE:
				deleteDataNote(msg.arg1);
				break;
			}
		}
	};

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PopupToolType.BUTTON_ONE:
				editCaseType();
				break;
			case PopupToolType.BUTTON_TWO:
				delCaseType();
				break;
			case PopupToolType.BUTTON_THREE:
				changeView();
				break;
			}
		}
	};

	private void editCaseType() {
		Intent intent1 = new Intent(this, CaseTypeEditActivity.class);
		intent1.setAction(Intent.ACTION_EDIT);
		intent1.putExtra("TypeId", typeId);
		intent1.putExtra("TypeName", typeName);
		startActivityForResult(intent1, Constant.REQ_CASE_TYPE);

		Common.setDataChangedFlag(DataListActivity.this);
	}

	private void delCaseType() {
		CustomDialog.Builder builder = new CustomDialog.Builder(
				DataListActivity.this);
		builder.setMessage(R.string.type_trash_alarm);
		builder.setTitle(R.string.prompt);
		builder.setIcon(R.drawable.alert_question);
		builder.setPositiveButton(R.string.lable_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				CustomProgressDialog.Builder builder = new CustomProgressDialog.Builder(
						DataListActivity.this);
				builder.setTitle(R.string.data_deleteing);
				progressDialog = builder.create();
				progressDialog.setCancelable(false);
				progressDialog.show();
				// 新建线程
				new Thread() {
					@Override
					public void run() {
						// 需要花时间计算的方法
						deleteAllData();
						// 向handler发消息
						handlerDelete.sendEmptyMessage(0);
					}
				}.start();

			}
		});
		builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	private void changeView() {
		if (DataCommon.getConfigValue(this, "ViewType+" + typeId).equals("1")) {
			DataCommon.setConfigValue(this, "ViewType+" + typeId, "2");
		} else {
			DataCommon.setConfigValue(this, "ViewType+" + typeId, "1");
		}
		progressDialog.show();
		// 新建线程
		new Thread() {
			@Override
			public void run() {
				// 向handler发消息
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void deleteAllData() {
		DBCaseTypeService dBCaseTypeService = new DBCaseTypeService(
				DataListActivity.this);
		// 还要删除其他关联
		dBCaseTypeService.delete(typeId);
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handlerDelete = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			progressDialog.dismiss();
			Common.setDataChangedFlag(DataListActivity.this);
			Common.showMessage(DataListActivity.this,
					getResources().getString(R.string.message_delete_success),
					Toast.LENGTH_SHORT, R.drawable.success);
			DataListActivity.this.finish();
		}
	};

	// 返回后，刷新数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle localBundle = Common.getIntentExtras(data);
		if ((localBundle != null)
				&& (localBundle.getString("DataChanged").equals("Y"))) {
			loadData();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();

			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private static class PopupWindowTool extends PopupWindowUtil implements
			OnClickListener {

		private Handler mHandler;

		public PopupWindowTool(View anchor, Handler handler) {
			super(anchor);
			this.mHandler = handler;
		}

		@Override
		protected void onCreate() {
			// inflate layout
			LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View root = null;
			int[] location = new int[2];
			anchor.getLocationOnScreen(location);
			root = (View) inflater.inflate(R.layout.popup_top_right, null);

			// ---------------------------------------------------------------------------
			LinearLayout btnOne = (LinearLayout) root.findViewById(R.id.btnOne);
			btnOne.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = mHandler.obtainMessage();
					msg.what = PopupToolType.BUTTON_ONE;
					mHandler.sendMessage(msg);
					dismiss();
				}
			});

			LinearLayout btnTwo = (LinearLayout) root.findViewById(R.id.btnTwo);
			btnTwo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Message msg = mHandler.obtainMessage();
					msg.what = PopupToolType.BUTTON_TWO;
					mHandler.sendMessage(msg);
					dismiss();
				}
			});

			LinearLayout btnThree = (LinearLayout) root
					.findViewById(R.id.btnThree);
			btnThree.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Message msg = mHandler.obtainMessage();
					msg.what = PopupToolType.BUTTON_THREE;
					mHandler.sendMessage(msg);
					dismiss();
				}
			});

			this.setContentView(root);
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub

		}
	}
}
