package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ptool.datacase.data.DataCommon;
import ptool.datacase.data.service.DBCaseCharsService;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.CustomProgressDialog;
import ptool.datacase.util.PopupToolType;
import ptool.datacase.util.PopupWindowUtil;
import ptool.datacase.util.control.DraggableListView;
import android.app.ListActivity;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CharListActivity extends ListActivity {
//	private SystemConfig app;

	private CustomProgressDialog progressDialog;
	private String typeId;
	private String typeName;
	private DBCaseCharsService dBCaseCharsService;
	private List<Map<String, Object>> charItems = new ArrayList<Map<String, Object>>();
	private ArrayList<String> arrayId = new ArrayList<String>();
	private ArrayList<String> arrayName = new ArrayList<String>();
	private ArrayList<String> arrayType = new ArrayList<String>();
	private ArrayList<String> arrayColumn = new ArrayList<String>();
	private Object arrayBackup = new ArrayList<String>();
	private IconicAdapter adapter = null;
	private LinearLayout layNew;
	private LinearLayout laySave;
	private Button btnBack;
	private ImageButton imgBtnRight;
	private TextView tvRecordNums;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_char_list);
//		app = (SystemConfig) this.getApplication();

		dBCaseCharsService = new DBCaseCharsService(this);

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
		laySave = (LinearLayout) findViewById(R.id.laySave);
		btnBack = (Button) findViewById(R.id.btnBack);
		imgBtnRight = (ImageButton) findViewById(R.id.imgBtnRight);
		imgBtnRight.setVisibility(View.GONE);
		tvRecordNums = (TextView) this.findViewById(R.id.tvRecordNums);
		setListener();
	}

	private void setListener() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitCheck();
			}
		});

		layNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addChar();
			}
		});
		laySave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveOrder();
			}
		});
	}

	private void loadData() {
		CustomProgressDialog.Builder builder = new CustomProgressDialog.Builder(
				CharListActivity.this);
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
		dBCaseCharsService.queryByTypeAll(this, charItems, typeId);
		arrayName.clear();
		arrayColumn.clear();
		arrayType.clear();
		arrayId.clear();
		for (int i = 0; i < charItems.size(); i++) {
			arrayName.add(charItems.get(i).get("char_name").toString());
			arrayColumn.add(DataCommon.getColumnName(charItems.get(i)
					.get("column_name").toString()));
			arrayType.add(DataCommon.getTypeName(Common.parseInt(charItems
					.get(i).get("char_datatype").toString())));
			arrayId.add(charItems.get(i).get("char_id").toString());
		}
		arrayBackup = arrayName.clone();
		
	}

	private void addChar() {
		Intent intent1 = new Intent(CharListActivity.this,
				CharEditActivity.class);
		intent1.setAction(Intent.ACTION_INSERT);
		intent1.putExtra("TypeId", typeId);
		intent1.putExtra("TypeName", typeName);
		intent1.putExtra("CharNums", charItems.size());
		startActivityForResult(intent1, Constant.REQ_CHAR_EDIT);
	}

	private void editChar(int indexValue) {
		String selectedId = (String) arrayId.get(indexValue);
		Intent intent1 = new Intent(CharListActivity.this,
				CharEditActivity.class);
		intent1.setAction(Intent.ACTION_EDIT);
		intent1.putExtra("TypeId", typeId);
		intent1.putExtra("TypeName", typeName);
		intent1.putExtra("CharId", selectedId);
		startActivityForResult(intent1, Constant.REQ_CHAR_EDIT);
	}

	public Handler mAdpBtnClickHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PopupToolType.BUTTON_ONE:
				addChar();
				break;
			case PopupToolType.BUTTON_TWO:
				editChar(msg.arg1);
				break;
			case PopupToolType.BUTTON_THREE:
				deleteChar(msg.arg1);
				break;
			}
		}
	};

	private void deleteChar(int indexValue) {
		final String selectedId = (String) arrayId.get(indexValue);
		final String selectedName = (String) arrayName.get(indexValue);

		CustomDialog.Builder builder = new CustomDialog.Builder(
				CharListActivity.this);
		builder.setMessage(R.string.lable_delete_confirm);
		builder.setTitle(R.string.prompt);
		builder.setIcon(R.drawable.alert_question);
		builder.setPositiveButton(R.string.lable_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dBCaseCharsService.getUseCount(selectedId) == 0) {
					dBCaseCharsService.delete(selectedId);
					loadData();
					Common.showMessage(CharListActivity.this, getResources()
							.getString(R.string.message_delete_success),
							Toast.LENGTH_SHORT, R.drawable.success);
				} else {
					String messageOk = getResources().getString(
							R.string.message_no_delete);
					messageOk = MessageFormat.format(messageOk, selectedName);
					Common.showMessage(CharListActivity.this, messageOk,
							Toast.LENGTH_SHORT, R.drawable.alert_information);
				}
			}
		});
		builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

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
		tvRecordNums.setText(charItems.size()
				+ getResources().getString(R.string.lable_unit_tiao));
		adapter = new IconicAdapter(mAdpBtnClickHandler);
		setListAdapter(adapter);
		DraggableListView tlv = (DraggableListView) getListView();
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
		tlv.getAdapter();

		tlv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				editChar(arg2);
			}
		});

		tlv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				final int indexValue = arg2;
				CustomDialog dialog = new CustomDialog.Builder(
						CharListActivity.this)
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
										if (which == 0) {// 添加
											addChar();
										} else if (which == 1) {
											editChar(indexValue);
										} else if (which == 2) {
											deleteChar(indexValue);
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
			}

		});
	}

	private Boolean saveOrder() {
		try {
			 	for (int i = 0; i < charItems.size(); i++) {
			 		dBCaseCharsService.updateSequence(arrayId.get(i), i);
			}
			Common.setDataChangedFlag(this);
			arrayBackup = arrayName.clone();
			Common.showMessage(
					CharListActivity.this,
					getResources().getString(
							R.string.char_order_save_ok),
					Toast.LENGTH_SHORT, R.drawable.success);
			return true;
		} catch (Exception e) {
			Common.showMessage(CharListActivity.this, e.getMessage(),
					Toast.LENGTH_SHORT, R.drawable.error);
			return false;
		}
	}

	private void exitCheck() {
		if (!arrayBackup.equals(arrayName)) {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					CharListActivity.this);
			builder.setMessage(R.string.lable_data_changed);
			builder.setTitle(R.string.lable_close_info);
			builder.setIcon(R.drawable.alert_question);
			builder.setPositiveButton(R.string.lable_yes,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// 保存
							Common.setDataChangedFlag(CharListActivity.this);
							if (saveOrder()) {
								dialog.dismiss();
								CharListActivity.this.finish();
							}
						}
					});
			builder.setNeutralButton(R.string.lable_no,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							CharListActivity.this.finish();
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
		} else {
			CharListActivity.this.finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitCheck();
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
			Common.setDataChangedFlag(CharListActivity.this);
			loadData();
		}
	}

	private DraggableListView.DropListener onDrop = new DraggableListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = adapter.getItem(from);

			adapter.remove(item);
			adapter.insert(item, to);

			String type = arrayType.get(from);
			arrayType.remove(type);
			arrayType.add(to, type);

			String id = arrayId.get(from);
			arrayId.remove(id);
			arrayId.add(to, id);

			String column = arrayColumn.get(from);
			arrayColumn.remove(column);
			arrayColumn.add(to, column);

		}
	};

	private DraggableListView.RemoveListener onRemove = new DraggableListView.RemoveListener() {
		@Override
		public void remove(int which) {
			adapter.remove(adapter.getItem(which));
		}
	};

	class IconicAdapter extends ArrayAdapter<String> {
		private Handler mHandler;

		IconicAdapter(Handler handler) {
			super(CharListActivity.this, R.layout.list_reorder_item, arrayName);
			this.mHandler = handler;
		}

		public ArrayList<String> getList() {
			return arrayName;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.list_reorder_item, parent,
						false);
			}
			TextView labelName = (TextView) row.findViewById(R.id.CharName);
			labelName.setText(arrayName.get(position));

			TextView labelType = (TextView) row.findViewById(R.id.CharType);
			labelType.setText(arrayType.get(position));

			TextView labelColumn = (TextView) row.findViewById(R.id.CharColumn);
			labelColumn.setText(arrayColumn.get(position));
			// 根据账户名称，获得对应的图标
			ImageView img = (ImageView) row.findViewById(R.id.ListOrderImage);
			img.setBackgroundResource(R.drawable.cross);

			LinearLayout layImage = (LinearLayout) row
					.findViewById(R.id.LayImage);

			layImage.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					// 判断点击行的位置
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					if (location[1] > 200) {
						PopupWindowTool dw = new PopupWindowTool(v, position,
								mHandler);
						dw.setBackgroundDrawable(CharListActivity.this
								.getResources().getDrawable(
										R.drawable.popup_window_up));
						dw.showLikeQuickAction(0, 0);
					} else {
						PopupWindowTool dw = new PopupWindowTool(v, position,
								mHandler);
						dw.setBackgroundDrawable(CharListActivity.this
								.getResources().getDrawable(
										R.drawable.popup_window_dowm));
						dw.showLikePopDownMenu(0, -30);
					}
				}
			});

			return (row);
		}

		private class PopupWindowTool extends PopupWindowUtil implements
				OnClickListener {
			private int position;
			private Handler mHandler;

			public PopupWindowTool(View anchor, int position, Handler handler) {
				super(anchor);
				this.position = position;
				this.mHandler = handler;
			}

			@Override
			protected void onCreate() {
				// inflate layout
				LayoutInflater inflater = (LayoutInflater) this.anchor
						.getContext().getSystemService(
								Context.LAYOUT_INFLATER_SERVICE);

				View root = null;
				int[] location = new int[2];
				anchor.getLocationOnScreen(location);
				if (location[1] > 200) {
					root = (View) inflater.inflate(R.layout.popup_tool_top,
							null);
				} else {
					root = (View) inflater.inflate(R.layout.popup_tool_bottom,
							null);
				}
				// ---------------------------------------------------------------------------
				LinearLayout btnOne = (LinearLayout) root
						.findViewById(R.id.btnOne);
				btnOne.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Message msg = mHandler.obtainMessage();
						msg.what = PopupToolType.BUTTON_ONE;
						msg.arg1 = position;
						mHandler.sendMessage(msg);
						dismiss();
					}
				});

				LinearLayout btnTwo = (LinearLayout) root
						.findViewById(R.id.btnTwo);
				btnTwo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Message msg = mHandler.obtainMessage();
						msg.what = PopupToolType.BUTTON_TWO;
						msg.arg1 = position;
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
						msg.arg1 = position;
						mHandler.sendMessage(msg);
						dismiss();
					}
				});

				LinearLayout sepThree = (LinearLayout) root
						.findViewById(R.id.sepThree);
				sepThree.setVisibility(View.GONE);

				TextView txtFour = (TextView) root.findViewById(R.id.txtFour);
				txtFour.setVisibility(View.GONE);
				ImageView imgFour = (ImageView) root.findViewById(R.id.imgFour);
				imgFour.setVisibility(View.GONE);

				// LinearLayout btnFour = (LinearLayout) root
				// .findViewById(R.id.btnFour);
				// btnFour.setVisibility(View.GONE);
				// btnFour.setOnClickListener(new View.OnClickListener() {
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// Message msg = mHandler.obtainMessage();
				// msg.what = PopupToolType.BUTTON_FOUR;
				// msg.arg1 = position;
				// mHandler.sendMessage(msg);
				// dismiss();
				// }
				// });

				LinearLayout sepFour = (LinearLayout) root
						.findViewById(R.id.sepFour);
				sepFour.setVisibility(View.GONE);

				LinearLayout btnFive = (LinearLayout) root
						.findViewById(R.id.btnFive);
				btnFive.setVisibility(View.GONE);
				// btnFive.setOnClickListener(new View.OnClickListener() {
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// dismiss();
				// }
				// });

				LinearLayout sepFive = (LinearLayout) root
						.findViewById(R.id.sepFive);
				sepFive.setVisibility(View.GONE);

				LinearLayout btnSix = (LinearLayout) root
						.findViewById(R.id.btnSix);
				btnSix.setVisibility(View.GONE);
				// btnSix.setOnClickListener(new View.OnClickListener() {
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// dismiss();
				// }
				// });
				this.setContentView(root);
			}

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		}
	}
}
