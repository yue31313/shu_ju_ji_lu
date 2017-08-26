package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ptool.datacase.data.service.DBCaseValuesService;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.CustomProgressDialog;
import ptool.datacase.util.PopupToolType;
import ptool.datacase.util.control.ValueListViewAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ValueListActivity extends Activity {
	private CustomProgressDialog progressDialog;
	private String typeId;
	private String charId;
	private String charName;
	private DBCaseValuesService dBCaseValuesService;
	private List<Map<String, Object>> valueItems = new ArrayList<Map<String, Object>>();
	private LinearLayout layNew;
	 private Button btnBack;
	private ImageButton imgBtnRight;
	private TextView tvRecordNums;
	private ListView list;
	private int showFirstVisible;
	
	private ArrayList<String> valueIdList = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_value_list);
 
		dBCaseValuesService = new DBCaseValuesService(this);
		showFirstVisible = 0;
		
		Intent intent = getIntent();
		typeId = intent.getStringExtra("TypeId");
		charId = intent.getStringExtra("CharId");
		charName = intent.getStringExtra("CharName");

		loadControls();
		loadData();
	}
	
	private void loadControls() {
		String message = getResources().getString(R.string.value_list_title);
		message = MessageFormat.format(message, charName);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(message);

		layNew = (LinearLayout) findViewById(R.id.layNew);
	 	btnBack = (Button) findViewById(R.id.btnBack);
		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setVisibility(View.GONE);
		tvRecordNums = (TextView) this.findViewById(R.id.tvRecordNums);
		list = (ListView) findViewById(R.id.lvValueList);
		setListener();
	}
	
	private void setListener() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ValueListActivity.this.finish();
			}
		});

		layNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addValue();
			}
		});
	}
	
	private void loadData() {
		CustomProgressDialog.Builder builder = new CustomProgressDialog.Builder(
				ValueListActivity.this);
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
		dBCaseValuesService.queryValues(this, valueItems, typeId, charId);
	}
	
	private void addValue() {
		Intent intent1 = new Intent(ValueListActivity.this,
				ValueEditActivity.class);
		intent1.setAction(Intent.ACTION_INSERT);
		intent1.putExtra("TypeId", typeId);
		intent1.putExtra("CharId", charId);
	 	startActivityForResult(intent1, Constant.REQ_VALUE_EDIT);
	}
	
	
	private void editValue(int indexValue) {
	 	Intent intent1 = new Intent(ValueListActivity.this,
	 			ValueEditActivity.class);
		intent1.setAction(Intent.ACTION_EDIT);
		intent1.putExtra("TypeId", typeId);
		intent1.putExtra("CharId", charId);
		intent1.putExtra("IndexValue", indexValue);
		intent1.putStringArrayListExtra("ValueIdList", valueIdList);
		startActivityForResult(intent1, Constant.REQ_VALUE_EDIT);
	}
	
	private void deleteValue(int indexValue) {
		final String selectedId = valueItems.get(indexValue).get("ListItemId")
				.toString();
		CustomDialog.Builder builder = new CustomDialog.Builder(
				ValueListActivity.this);
		builder.setMessage(R.string.lable_delete_confirm);
		builder.setTitle(R.string.prompt);
		builder.setIcon(R.drawable.alert_question);
		builder.setPositiveButton(R.string.lable_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dBCaseValuesService.delete(selectedId);
				loadData();
				Common.showMessage(ValueListActivity.this, getResources()
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
				addValue();
				break;
			case PopupToolType.BUTTON_TWO:
				editValue(msg.arg1);
				break;
			case PopupToolType.BUTTON_THREE:
				deleteValue(msg.arg1);
				break;
			}
		}
	};
	
	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 关闭ProgressDialog
			progressDialog.dismiss();
			setData();
		}
	};
	
	private void setData() {
	 	// 生成适配器的Item和动态数组对应的元素
		ValueListViewAdapter listViewAdapter = new ValueListViewAdapter(this,
				valueItems,  mAdpBtnClickHandler); // 创建适配器
		list.setAdapter(listViewAdapter);
		list.setSelection(showFirstVisible);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 打开编辑界面
				showFirstVisible = list.getFirstVisiblePosition();
				editValue(arg2);
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
						ValueListActivity.this)
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
											addValue();
										} else if (which == 1) {// 修改
											showFirstVisible = list
													.getFirstVisiblePosition();
											editValue(indexValue);
										} else if (which == 2) {// 删除
											showFirstVisible = list
													.getFirstVisiblePosition();
											deleteValue(indexValue);
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
		valueIdList.clear();
		for (int i = 0; i < valueItems.size(); i++) {
			valueIdList.add(valueItems.get(i).get("ListItemId").toString());
		}
		tvRecordNums.setText(valueItems.size()
				+ getResources().getString(R.string.lable_unit_tiao));
	}

	// 返回后，刷新数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle localBundle = Common.getIntentExtras(data);
		if ((localBundle != null)
				&& (localBundle.getString("DataChanged").equals("Y"))) {
			loadData();
		}
	}
}
