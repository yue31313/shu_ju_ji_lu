package ptool.datacase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.data.DataCommon;
import ptool.datacase.data.service.DBCaseTypeService;
import ptool.datacase.net.AutoUpdate;
import ptool.datacase.net.Config;
import ptool.datacase.net.UpdateEntity;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.CustomProgressDialog;
import ptool.datacase.util.PopupToolType;
import ptool.datacase.util.PopupWindowUtil;
import ptool.datacase.util.control.GridImageAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
 	private GridView gridview;
	private ImageButton imgBtnRight;
	private DBCaseTypeService dBCaseTypeService;
	private List<Map<String, Object>> typeItems = new ArrayList<Map<String, Object>>();
	private CustomProgressDialog progressDialog;
	
	
	// 系统升级
		private int vercode;
		private UpdateEntity updateObj;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
 
		dBCaseTypeService = new DBCaseTypeService(this);
		
	 	imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupWindowTool dw = new PopupWindowTool(v, mHandler);
				dw.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.title_function_bg));
				dw.showLikePopDownMenu();
			}
		});
		String notFirstFlag = DataCommon.getConfigValue(this,
				"FIRST_FLAG");
		if (!notFirstFlag.equals("是")) {

			Common.showMessage(this, "拖拽图标，可以实现排序", Toast.LENGTH_LONG, R.drawable.warning);
			DataCommon.setConfigValue(this, "FIRST_FLAG","是");
		} 
		// 新建线程
		new Thread() {
			@Override
			public void run() {
				// 需要花时间计算的方法
				LoadData();
				// 向handler发消息
				handler.sendEmptyMessage(0);
			}
		}.start();
		
		// --------------自动升级-----------------------------------
				updateObj = new UpdateEntity();
				//
				new Thread() {
					@Override
					public void run() {
						// 需要花时间计算的方法
						if (AutoUpdate.getServerVerCode(updateObj)) {
							vercode = Config.getVerCode(MainActivity.this);
						}
						// 向handler发消息
						handlerUpdate.sendEmptyMessage(0);
					}
				}.start();
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			loadIcons();
		}
	};
	/**
	 * 用Handler来更新UI
	 */
	private Handler handlerUpdate = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (updateObj.getNewVerCode() > vercode) {
				AutoUpdate.doNewVersionUpdate(MainActivity.this, updateObj.getNewVerName(), handler);
			}
		}
	};
	private void LoadData() {
		dBCaseTypeService.queryAll(this, typeItems);
	}

	private void loadIcons() {
		gridview = (GridView) findViewById(R.id.gridview);
		GridImageAdapter gridViewAdapter = new GridImageAdapter(this,
				typeItems); // 创建适配器
		gridview.setAdapter(gridViewAdapter);
		// 添加点击
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> item = (HashMap<String, Object>) arg0
						.getItemAtPosition(arg2);
				if (arg2 > 0) {
					Intent intent1 = new Intent(MainActivity.this,
							DataListActivity.class);
					intent1.putExtra("TypeId", item.get("ListItemId")
							.toString());
					intent1.putExtra("TypeName", item.get("ListItemTitle")
							.toString());
					startActivityForResult(intent1, Constant.REQ_DATA_LIST);

				} else {
					Intent intent1 = new Intent(MainActivity.this,
							CaseTypeEditActivity.class);
					intent1.setAction(Intent.ACTION_INSERT);
					intent1.putExtra("TypeNums", typeItems.size());
					intent1.putExtra("TypeName", item.get("ListItemTitle")
							.toString());
					startActivityForResult(intent1, Constant.REQ_CASE_TYPE);
				}
			}
		});
	}

	private void saveSort(){
		String typeId = "";
		for(int i = 1; i< typeItems.size(); i ++)
		{
			typeId = typeItems.get(i).get("ListItemId").toString();
			dBCaseTypeService.updateSequence(typeId, i -1);
		}
	}
	
	/**
	 * 软件退出提示
	 */
	protected void ExitDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(
				MainActivity.this);
		builder.setTitle(R.string.confirm_exit);
		builder.setIcon(R.drawable.alert_question);
		builder.setPositiveButton(R.string.ok,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CustomProgressDialog.Builder builder = new CustomProgressDialog.Builder(
								MainActivity.this);
						builder.setTitle(R.string.data_exiting);
						progressDialog = builder.create();
						progressDialog.setCancelable(false);
						progressDialog.show();
						new Thread() {
							@Override
							public void run() {
								saveSort();
								DataCommon.backUpDatabase(MainActivity.this);
								handlerExit.sendEmptyMessage(0);
							}
						}.start();
						
					}
				});

		builder.setNegativeButton(R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	private Handler handlerExit = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			System.exit(0);
		}
	};
	/**
	 * 按键事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			ExitDialog();

			return false;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 返回后，刷新数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle localBundle = Common.getIntentExtras(data);
		if ((localBundle != null)
				&& (localBundle.getString("DataChanged").equals("Y"))) {
			LoadData();
			loadIcons();
		}
	}

	private void CaseConfig()
	{
		Intent intent1 = new Intent(MainActivity.this,
				SysConfigActivity.class);
		startActivityForResult(intent1, Constant.REQ_SYSCONFIG);
	}
	
	private void AboutUs()
	{
		Intent intent1 = new Intent(MainActivity.this,
				AboutUsActivity.class);
		startActivityForResult(intent1, Constant.REQ_ABOUT_US);
	}
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PopupToolType.BUTTON_ONE:
				CaseConfig();
				break;
			case PopupToolType.BUTTON_TWO:
				AboutUs();
				break;
			case PopupToolType.BUTTON_THREE:
				saveSort();
				DataCommon.backUpDatabase(MainActivity.this);
				System.exit(0);
				break;
			}
		}
	};

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
			root = (View) inflater
					.inflate(R.layout.popup_top_right, null);

			// ---------------------------------------------------------------------------
			TextView txtOne = (TextView) root.findViewById(R.id.txtOne);
			txtOne.setText(R.string.case_config);
			ImageView imgOne = (ImageView) root.findViewById(R.id.imgOne);
			imgOne.setImageResource(R.drawable.bar_config);
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

			TextView txtTwo = (TextView) root.findViewById(R.id.txtTwo);
			txtTwo.setText(R.string.about_us);
			ImageView imgTwo = (ImageView) root.findViewById(R.id.imgTwo);
			imgTwo.setImageResource(R.drawable.bar_about);
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
			
			TextView txtThree = (TextView) root.findViewById(R.id.txtThree);
			txtThree.setText(R.string.case_exit);
			ImageView imgThree = (ImageView) root.findViewById(R.id.imgThree);
			imgThree.setImageResource(R.drawable.bar_exit);
			LinearLayout btnThree = (LinearLayout) root.findViewById(R.id.btnThree);
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
