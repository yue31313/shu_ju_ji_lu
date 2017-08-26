package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ptool.datacase.data.ConfigData;
import ptool.datacase.data.DataCommon;
import ptool.datacase.data.service.DBCaseCharsMathService;
import ptool.datacase.data.service.DBCaseCharsService;
import ptool.datacase.obj.DBCaseChars;
import ptool.datacase.obj.DBCaseCharsMath;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.Control;
import ptool.datacase.util.CustomDialog;
import ptool.datacase.util.control.CommonChoiceListViewAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CharEditActivity extends Activity {
	private int state;// 记录当前的状态
	private String typeId;
	// private String typeName;
	private String charId;
	private int charNums;
	private LinearLayout btnOk;
	private LinearLayout btnCancel;
	private DBCaseChars obj;
	private DBCaseCharsMath objMath;
	private DBCaseCharsService dBCaseCharsService;
	private DBCaseCharsMathService dBCaseCharsMathService;

	private Button btnCharName;
	private Button btnCharType;
	private Button btnCharTypeId;
	private ToggleButton tbtnCharCanNull;
	private TableRow tableRowCharDecimal;
//	private TableRow tableRowCharCanNull;
	private Button btnCharDecimal;
	private TableRow tableRowCharUnit;
	private Button btnCharUnit;
	private TableRow tableRowCharNeedCount;
	private ToggleButton tbtnCharNeedCount;
	private TableRow tableRowCharCountType;
	private Button btnCharCountType;
	private Button btnCharCountTypeId;
	private TableRow tableRowCharValues;
	private Button btnCharValues;
	private TableRow tableRowCharValuesMutli;
	private ToggleButton tbtnCharValuesMutli;
	private TableRow tableRowCharHide;
	private ToggleButton tbtnCharHide;
	private TableRow tableRowCharMathA;
	private Button btnCharMathA;
	private Button btnCharMathAId;
	private TableRow tableRowCharMathB;
	private Button btnCharMathB;
	private Button btnCharMathBId;
	private LinearLayout layCharMathType;
	private Button btnCharMathType;
	private Button btnCharMathTypeId;
	private TextView tvCharMathDesc;

	private TableRow tableRowFixValue;
	private Button btnCharFixValue;

	private List<Map<String, Object>> charTypeItems = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> countTypeItems = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mathTypeItems = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> charItems = new ArrayList<Map<String, Object>>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_char_edit);
		dBCaseCharsService = new DBCaseCharsService(this);
		dBCaseCharsMathService = new DBCaseCharsMathService(this);

		Intent intent = getIntent();
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_INSERT)) {
			state = Constant.STATE_NEW;
			charNums = intent.getIntExtra("CharNums", 0);
			typeId = intent.getStringExtra("TypeId");
			// typeName = intent.getStringExtra("TypeName");
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = Constant.STATE_MOIFY;
			charId = intent.getStringExtra("CharId");
			typeId = intent.getStringExtra("TypeId");
			// typeName = intent.getStringExtra("TypeName");
		}
		loadControls();
		setListener();
		initData();
	}

	private void loadControls() {
		btnCharName = (Button) this.findViewById(R.id.btnCharName);
		btnCharType = (Button) this.findViewById(R.id.btnCharType);
		btnCharTypeId = (Button) this.findViewById(R.id.btnCharTypeId);
		tbtnCharCanNull = (ToggleButton) this
				.findViewById(R.id.tbtnCharCanNull);
//		tableRowCharCanNull = (TableRow) this
//				.findViewById(R.id.tableRowCharCanNull);
		tableRowCharDecimal = (TableRow) this
				.findViewById(R.id.tableRowCharDecimal);
		btnCharDecimal = (Button) this.findViewById(R.id.btnCharDecimal);
		tableRowCharUnit = (TableRow) this.findViewById(R.id.tableRowCharUnit);
		btnCharUnit = (Button) this.findViewById(R.id.btnCharUnit);
		tableRowCharNeedCount = (TableRow) this
				.findViewById(R.id.tableRowCharNeedCount);
		tbtnCharNeedCount = (ToggleButton) this
				.findViewById(R.id.tbtnCharNeedCount);
		tableRowCharCountType = (TableRow) this
				.findViewById(R.id.tableRowCharCountType);
		btnCharCountType = (Button) this.findViewById(R.id.btnCharCountType);
		btnCharCountTypeId = (Button) this
				.findViewById(R.id.btnCharCountTypeId);
		tableRowCharValues = (TableRow) this
				.findViewById(R.id.tableRowCharValues);
		btnCharValues = (Button) this.findViewById(R.id.btnCharValues);
		tableRowCharValuesMutli = (TableRow) this
				.findViewById(R.id.tableRowCharValuesMutli);
		tbtnCharValuesMutli = (ToggleButton) this
				.findViewById(R.id.tbtnCharValuesMutli);
		tableRowCharHide = (TableRow) this.findViewById(R.id.tableRowCharHide);
		tbtnCharHide = (ToggleButton) this.findViewById(R.id.tbtnCharHide);
		tableRowCharMathA = (TableRow) this
				.findViewById(R.id.tableRowCharMathA);
		btnCharMathA = (Button) this.findViewById(R.id.btnCharMathA);
		btnCharMathAId = (Button) this.findViewById(R.id.btnCharMathAId);
		tableRowCharMathB = (TableRow) this
				.findViewById(R.id.tableRowCharMathB);
		btnCharMathB = (Button) this.findViewById(R.id.btnCharMathB);
		btnCharMathBId = (Button) this.findViewById(R.id.btnCharMathBId);
		layCharMathType = (LinearLayout) this
				.findViewById(R.id.layCharMathType);
		btnCharMathType = (Button) this.findViewById(R.id.btnCharMathType);
		btnCharMathTypeId = (Button) this.findViewById(R.id.btnCharMathTypeId);
		tvCharMathDesc = (TextView) this.findViewById(R.id.tvCharMathDesc);

		tableRowFixValue = (TableRow) this.findViewById(R.id.tableRowFixValue);
		btnCharFixValue = (Button) this.findViewById(R.id.btnCharFixValue);

		btnOk = (LinearLayout) this.findViewById(R.id.layoutOk);
		btnCancel = (LinearLayout) this.findViewById(R.id.layoutCancel);

	}

	private void initData() {
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		if (state == Constant.STATE_NEW) {
			obj = new DBCaseChars();
			btnCharName.setText("");
			obj.setCharName("");
			btnCharType.setText("数字");
			obj.setCharDataType("1");
			btnCharTypeId.setText("1");
			tbtnCharCanNull.setChecked(false);
			obj.setCharNull(Constant.FLAG_NO);
			btnCharDecimal.setText("0");
			obj.setCharDecimal(0);
			btnCharUnit.setText("");
			obj.setCharUnit("");
			tbtnCharNeedCount.setChecked(false);
			obj.setCharCount(Constant.FLAG_NO);
			btnCharCountType.setText("总和");
			btnCharCountTypeId.setText("0");
			obj.setCountType("0");
			tbtnCharValuesMutli.setChecked(false);
			obj.setCharMulti(Constant.FLAG_NO);
			tbtnCharHide.setChecked(false);
			obj.setIsHide(Constant.FLAG_NO);
			btnCharFixValue.setText("0");
			obj.setFixValue("0");

			obj.setCharRows(5);
			obj.setCharWidth(30);

			obj.setEditMask("");
			obj.setForSearch(Constant.FLAG_NO);
			obj.setTypeId(typeId);

			charTypeChanged();

			objMath = new DBCaseCharsMath();
			btnCharMathAId.setText("");
			btnCharMathA.setText("");
			objMath.setCharFrom("");
			btnCharMathBId.setText("");
			btnCharMathB.setText("");
			objMath.setCharTo("");

			btnCharMathType.setText("");
			btnCharMathTypeId.setText("");
			objMath.setCharMath("");

			tvTitle.setText(getResources().getString(R.string.char_new));
		} else {
			obj = dBCaseCharsService.getObj(charId);

			btnCharName.setText(obj.getCharName());
			btnCharType.setText(DataCommon.getTypeName(Common.parseInt(obj
					.getCharDataType())));
			btnCharTypeId.setText(obj.getCharDataType());
			charTypeChanged();

			if (obj.getCharNull().equals(Constant.FLAG_NO)) {
				tbtnCharCanNull.setChecked(false);
			} else {
				tbtnCharCanNull.setChecked(true);
			}
			btnCharDecimal.setText(String.valueOf(obj.getCharDecimal()));
			btnCharFixValue.setText(String.valueOf(obj.getFixValue()));
			btnCharUnit.setText(obj.getCharUnit());
			btnCharCountType.setText(DataCommon.getCoutTypeName(obj
					.getCountType()));
			btnCharCountTypeId.setText(obj.getCountType());
			if (obj.getCharCount().equals(Constant.FLAG_NO)) {
				tbtnCharNeedCount.setChecked(false);
			} else {
				tbtnCharNeedCount.setChecked(true);
			}
			if (obj.getCharMulti().equals(Constant.FLAG_NO)) {
				tbtnCharValuesMutli.setChecked(false);
			} else {
				tbtnCharValuesMutli.setChecked(true);
			}
			if (obj.getIsHide().equals(Constant.FLAG_NO)) {
				tbtnCharHide.setChecked(false);
			} else {
				tbtnCharHide.setChecked(true);
			}
			//
			if (obj.getCharDataType().equals("9")) {
				objMath = dBCaseCharsMathService.getObj(charId);
				if (objMath.getCharFrom() != null
						&& objMath.getCharTo() != null
						&& objMath.getCharMath() != null) {
					DBCaseChars objTempA = dBCaseCharsService.getObj(objMath
							.getCharFrom());
					btnCharMathAId.setText(objMath.getCharFrom());
					btnCharMathA.setText(objTempA.getCharName());
					DBCaseChars objTempB = dBCaseCharsService.getObj(objMath
							.getCharTo());
					btnCharMathBId.setText(objMath.getCharTo());
					btnCharMathB.setText(objTempB.getCharName());

					btnCharMathType.setText(DataCommon.getMathTypeName(objMath
							.getCharMath()));
					btnCharMathTypeId.setText(objMath.getCharMath());

					charMathTypeChanged();
				} else {
					objMath = new DBCaseCharsMath();
					btnCharMathAId.setText("");
					btnCharMathA.setText("");
					objMath.setCharFrom("");
					btnCharMathBId.setText("");
					btnCharMathB.setText("");
					objMath.setCharTo("");

					btnCharMathType.setText("");
					btnCharMathTypeId.setText("");
					objMath.setCharMath("");
				}
			} else {
				objMath = new DBCaseCharsMath();
				btnCharMathAId.setText("");
				btnCharMathA.setText("");
				objMath.setCharFrom("");
				btnCharMathBId.setText("");
				btnCharMathB.setText("");
				objMath.setCharTo("");

				btnCharMathType.setText("");
				btnCharMathTypeId.setText("");
				objMath.setCharMath("");
			}
			tvTitle.setText(getResources().getString(R.string.char_edit));
		}
	}

	private void setListener() {
		btnCharName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Control.ModifyContent(CharEditActivity.this, btnCharName,
						R.string.char_name, true);
			}
		});
		btnCharFixValue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(btnCharTypeId.getText().equals("1"))
				{
					Control.ModifyContent(CharEditActivity.this, btnCharFixValue,
						R.string.char_fix_value, true, true);
				}else if(btnCharTypeId.getText().equals("2") 
						||btnCharTypeId.getText().equals("3")
						||btnCharTypeId.getText().equals("4")
						||btnCharTypeId.getText().equals("5"))
				{
					Control.ModifyContent(CharEditActivity.this, btnCharFixValue,
							R.string.char_fix_value, true);
				}
			}
		});

		btnCharType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (charTypeItems.size() == 0) {
					ConfigData.CharTypeData(charTypeItems);
				}
				CustomDialog.Builder builder = new CustomDialog.Builder(
						CharEditActivity.this);
				builder.setTitle(R.string.char_type);
				LayoutInflater inflater = (LayoutInflater) CharEditActivity.this
						.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.list_view_select, null);
				builder.setView(layout);
				final CustomDialog dialog = builder.create();
				int selectIndex = 0;
				Map<String, Object> map = new HashMap<String, Object>();
				Boolean isEqual = false;

				for (int i = 0; i < charTypeItems.size(); i++) {
					isEqual = false;
					map = new HashMap<String, Object>();
					if (!btnCharType.getText().equals("")) {
						if (btnCharType.getText().equals(
								charTypeItems.get(i).get("ListItemTitle"))) {
							isEqual = true;
						}
					} else if (charTypeItems.get(i).get("ListItemTitle")
							.equals("")) {
						isEqual = true;
					}
					if (isEqual) {
						selectIndex = i;
						map.put("ListItemImage",
								charTypeItems.get(i).get("ListItemImage"));// 图像资源的ID
						map.put("ListItemTitle",
								charTypeItems.get(i).get("ListItemTitle"));
						map.put("ListItemId",
								charTypeItems.get(i).get("ListItemId"));
						map.put("ListItemCheck", R.drawable.success);
						charTypeItems.set(i, map);
					} else {
						if (charTypeItems.get(i).get("ListItemCheck") != null) {
							map.put("ListItemImage",
									charTypeItems.get(i).get("ListItemImage"));// 图像资源的ID
							map.put("ListItemTitle",
									charTypeItems.get(i).get("ListItemTitle"));
							map.put("ListItemId",
									charTypeItems.get(i).get("ListItemId"));
							map.put("ListItemCheck", null);
							charTypeItems.set(i, map);
						}
					}
				}
				// 绑定Layout里面的ListView
				ListView list = (ListView) layout.findViewById(R.id.lvSelect);
				// 生成适配器的Item和动态数组对应的元素
				CommonChoiceListViewAdapter listViewAdapter = new CommonChoiceListViewAdapter(
						CharEditActivity.this, charTypeItems); // 创建适配器
				list.setAdapter(listViewAdapter);
				list.setSelection(selectIndex);
				// 添加点击
				list.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						HashMap<String, Object> item = (HashMap<String, Object>) arg0
								.getItemAtPosition(arg2);
						btnCharType.setText(item.get("ListItemTitle")
								.toString());
						btnCharTypeId
								.setText(item.get("ListItemId").toString());

						charTypeChanged();

						dialog.dismiss();
					}
				});
				dialog.show();
			}

		});

		btnCharDecimal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Control.ModifyContent(CharEditActivity.this, btnCharDecimal,
						R.string.char_decimal, true, true);
			}
		});

		btnCharUnit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Control.ModifyContent(CharEditActivity.this, btnCharUnit,
						R.string.char_unit, true);
			}
		});
		tbtnCharNeedCount
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							tableRowCharCountType.setVisibility(View.VISIBLE);
							if (state == Constant.STATE_NEW) {
								if (btnCharCountType.getText().equals("")) {
									btnCharCountType.setText("总和");
									btnCharCountTypeId.setText("0");
								}
							} else {
								if (!obj.getCountType().equals("")) {
									btnCharCountType.setText(DataCommon
											.getCoutTypeName(obj.getCountType()));
									btnCharCountTypeId.setText(obj
											.getCountType());
								} else {
									btnCharCountType.setText("总和");
									btnCharCountTypeId.setText("0");
								}
							}
						} else {
							tableRowCharCountType.setVisibility(View.GONE);
							if (state == Constant.STATE_NEW) {
								if (!btnCharCountType.getText().equals("")) {
									btnCharCountType.setText("");
									btnCharCountTypeId.setText("");
								}
							} else {
								btnCharCountType.setText(DataCommon
										.getCoutTypeName(obj.getCountType()));
								btnCharCountTypeId.setText(obj.getCountType());
							}
						}
					}
				});

		btnCharCountType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (countTypeItems.size() == 0) {
					ConfigData.CountTypeData(countTypeItems);
				}
				Control.DataChoice(CharEditActivity.this, countTypeItems,
						btnCharCountType, btnCharCountTypeId,
						R.string.char_count_type);
			}
		});

		btnCharMathType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mathTypeItems.size() == 0) {
					ConfigData.MathTypeData(mathTypeItems);
				}

				CustomDialog.Builder builder = new CustomDialog.Builder(
						CharEditActivity.this);
				builder.setTitle(R.string.char_math_type);
				LayoutInflater inflater = (LayoutInflater) CharEditActivity.this
						.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.list_view_select, null);
				builder.setView(layout);
				final CustomDialog dialog = builder.create();
				int selectIndex = 0;
				Map<String, Object> map = new HashMap<String, Object>();
				Boolean isEqual = false;

				for (int i = 0; i < mathTypeItems.size(); i++) {
					isEqual = false;
					map = new HashMap<String, Object>();
					if (!btnCharMathType.getText().equals("")) {
						if (btnCharMathType.getText().equals(
								mathTypeItems.get(i).get("ListItemTitle"))) {
							isEqual = true;
						}
					} else if (mathTypeItems.get(i).get("ListItemTitle")
							.equals("")) {
						isEqual = true;
					}
					if (isEqual) {
						selectIndex = i;
						map.put("ListItemImage",
								mathTypeItems.get(i).get("ListItemImage"));// 图像资源的ID
						map.put("ListItemTitle",
								mathTypeItems.get(i).get("ListItemTitle"));
						map.put("ListItemId",
								mathTypeItems.get(i).get("ListItemId"));
						map.put("ListItemCheck", R.drawable.success);
						mathTypeItems.set(i, map);
					} else {
						if (mathTypeItems.get(i).get("ListItemCheck") != null) {
							map.put("ListItemImage",
									mathTypeItems.get(i).get("ListItemImage"));// 图像资源的ID
							map.put("ListItemTitle",
									mathTypeItems.get(i).get("ListItemTitle"));
							map.put("ListItemId",
									mathTypeItems.get(i).get("ListItemId"));
							map.put("ListItemCheck", null);
							mathTypeItems.set(i, map);
						}
					}
				}
				// 绑定Layout里面的ListView
				ListView list = (ListView) layout.findViewById(R.id.lvSelect);
				// 生成适配器的Item和动态数组对应的元素
				CommonChoiceListViewAdapter listViewAdapter = new CommonChoiceListViewAdapter(
						CharEditActivity.this, mathTypeItems); // 创建适配器
				list.setAdapter(listViewAdapter);
				list.setSelection(selectIndex);
				// 添加点击
				list.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						HashMap<String, Object> item = (HashMap<String, Object>) arg0
								.getItemAtPosition(arg2);
						btnCharMathType.setText(item.get("ListItemTitle")
								.toString());
						btnCharMathTypeId.setText(item.get("ListItemId")
								.toString());

						charMathTypeChanged();

						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});

		btnCharMathA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dBCaseCharsService.queryMathByType(CharEditActivity.this,
						charItems, typeId);

				if (charItems.size() > 0) {
					for (int i = 0; i < charItems.size(); i++) {
						if (!btnCharMathBId.getText().equals("")) {
							if (charItems.get(i).get("ListItemId")
									.equals(btnCharMathBId.getText())) {
								charItems.remove(i);
							}
						}
					}
				}
				if (state == Constant.STATE_MOIFY) {
					if (charItems.size() > 0) {
						for (int i = 0; i < charItems.size(); i++) {
							if (charItems.get(i).get("ListItemId")
									.equals(charId)) {
								charItems.remove(i);
							}
						}
					}
				}
				if (charItems.size() == 0) {
					Common.showMessage(CharEditActivity.this, getResources()
							.getString(R.string.char_math_nochar),
							Toast.LENGTH_SHORT, R.drawable.alert_information);
				} else {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							CharEditActivity.this);
					builder.setTitle(R.string.char_math_a);
					LayoutInflater inflater = (LayoutInflater) CharEditActivity.this
							.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.list_view_select,
							null);
					builder.setView(layout);
					final CustomDialog dialog = builder.create();
					int selectIndex = 0;
					Map<String, Object> map = new HashMap<String, Object>();
					Boolean isEqual = false;

					for (int i = 0; i < charItems.size(); i++) {
						isEqual = false;
						map = new HashMap<String, Object>();
						if (!btnCharMathA.getText().equals("")) {
							if (btnCharMathA.getText().equals(
									charItems.get(i).get("ListItemTitle"))) {
								isEqual = true;
							}
						} else if (charItems.get(i).get("ListItemTitle")
								.equals("")) {
							isEqual = true;
						}
						if (isEqual) {
							selectIndex = i;
							map.put("ListItemImage",
									charItems.get(i).get("ListItemImage"));// 图像资源的ID
							map.put("ListItemTitle",
									charItems.get(i).get("ListItemTitle"));
							map.put("ListItemId",
									charItems.get(i).get("ListItemId"));
							map.put("ListItemCheck", R.drawable.success);
							charItems.set(i, map);
						} else {
							if (charItems.get(i).get("ListItemCheck") != null) {
								map.put("ListItemImage",
										charItems.get(i).get("ListItemImage"));// 图像资源的ID
								map.put("ListItemTitle",
										charItems.get(i).get("ListItemTitle"));
								map.put("ListItemId",
										charItems.get(i).get("ListItemId"));
								map.put("ListItemCheck", null);
								charItems.set(i, map);
							}
						}
					}
					// 绑定Layout里面的ListView
					ListView list = (ListView) layout
							.findViewById(R.id.lvSelect);
					// 生成适配器的Item和动态数组对应的元素
					CommonChoiceListViewAdapter listViewAdapter = new CommonChoiceListViewAdapter(
							CharEditActivity.this, charItems); // 创建适配器
					list.setAdapter(listViewAdapter);
					list.setSelection(selectIndex);
					// 添加点击
					list.setOnItemClickListener(new OnItemClickListener() {
						@SuppressWarnings("unchecked")
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							HashMap<String, Object> item = (HashMap<String, Object>) arg0
									.getItemAtPosition(arg2);
							btnCharMathA.setText(item.get("ListItemTitle")
									.toString());
							btnCharMathAId.setText(item.get("ListItemId")
									.toString());

							charMathTypeChanged();

							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
		});
		btnCharMathB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dBCaseCharsService.queryMathByType(CharEditActivity.this,
						charItems, typeId);

				if (charItems.size() > 0) {
					for (int i = 0; i < charItems.size(); i++) {
						if (!btnCharMathAId.getText().equals("")) {
							if (charItems.get(i).get("ListItemId")
									.equals(btnCharMathAId.getText())) {
								charItems.remove(i);
							}
						}
					}
				}
				if (state == Constant.STATE_MOIFY) {
					if (charItems.size() > 0) {
						for (int i = 0; i < charItems.size(); i++) {
							if (charItems.get(i).get("ListItemId")
									.equals(charId)) {
								charItems.remove(i);
							}
						}
					}
				}
				if (charItems.size() == 0) {
					Common.showMessage(CharEditActivity.this, getResources()
							.getString(R.string.char_math_nochar),
							Toast.LENGTH_SHORT, R.drawable.alert_information);
				} else {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							CharEditActivity.this);
					builder.setTitle(R.string.char_math_b);
					LayoutInflater inflater = (LayoutInflater) CharEditActivity.this
							.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.list_view_select,
							null);
					builder.setView(layout);
					final CustomDialog dialog = builder.create();
					int selectIndex = 0;
					Map<String, Object> map = new HashMap<String, Object>();
					Boolean isEqual = false;

					for (int i = 0; i < charItems.size(); i++) {
						isEqual = false;
						map = new HashMap<String, Object>();
						if (!btnCharMathB.getText().equals("")) {
							if (btnCharMathB.getText().equals(
									charItems.get(i).get("ListItemTitle"))) {
								isEqual = true;
							}
						} else if (charItems.get(i).get("ListItemTitle")
								.equals("")) {
							isEqual = true;
						}
						if (isEqual) {
							selectIndex = i;
							map.put("ListItemImage",
									charItems.get(i).get("ListItemImage"));// 图像资源的ID
							map.put("ListItemTitle",
									charItems.get(i).get("ListItemTitle"));
							map.put("ListItemId",
									charItems.get(i).get("ListItemId"));
							map.put("ListItemCheck", R.drawable.success);
							charItems.set(i, map);
						} else {
							if (charItems.get(i).get("ListItemCheck") != null) {
								map.put("ListItemImage",
										charItems.get(i).get("ListItemImage"));// 图像资源的ID
								map.put("ListItemTitle",
										charItems.get(i).get("ListItemTitle"));
								map.put("ListItemId",
										charItems.get(i).get("ListItemId"));
								map.put("ListItemCheck", null);
								charItems.set(i, map);
							}
						}
					}
					// 绑定Layout里面的ListView
					ListView list = (ListView) layout
							.findViewById(R.id.lvSelect);
					// 生成适配器的Item和动态数组对应的元素
					CommonChoiceListViewAdapter listViewAdapter = new CommonChoiceListViewAdapter(
							CharEditActivity.this, charItems); // 创建适配器
					list.setAdapter(listViewAdapter);
					list.setSelection(selectIndex);
					// 添加点击
					list.setOnItemClickListener(new OnItemClickListener() {
						@SuppressWarnings("unchecked")
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							HashMap<String, Object> item = (HashMap<String, Object>) arg0
									.getItemAtPosition(arg2);
							btnCharMathB.setText(item.get("ListItemTitle")
									.toString());
							btnCharMathBId.setText(item.get("ListItemId")
									.toString());

							charMathTypeChanged();

							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
		});

		btnCharValues.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 打开值集维护界面
				if (state == Constant.STATE_NEW) {
					Common.showMessage(CharEditActivity.this, getResources()
							.getString(R.string.char_save_first),
							Toast.LENGTH_SHORT, R.drawable.alert_information);
				} else {
					Intent intent1 = new Intent(CharEditActivity.this,
							ValueListActivity.class);
					intent1.putExtra("TypeId", typeId);
					intent1.putExtra("CharId", charId);
					intent1.putExtra("CharName", obj.getCharName());
					startActivityForResult(intent1, Constant.REQ_VALUE_LIST);
				}
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (saveData()) {
					if (state == Constant.STATE_NEW) {
						if (obj.getCharDataType().equals("5")) {
							state = Constant.STATE_MOIFY;
							charId = obj.getCharId();
							initData();

						} else {
							CharEditActivity.this.finish();
						}
					} else {
						CharEditActivity.this.finish();
					}
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkDatachanged();
			}
		});
	}

	private void charTypeChanged() {
		String typeId = btnCharTypeId.getText().toString();
		if (typeId.equals("1")) {
			tableRowCharDecimal.setVisibility(View.VISIBLE);
			tableRowCharUnit.setVisibility(View.VISIBLE);
			tableRowCharNeedCount.setVisibility(View.VISIBLE);
			if (tbtnCharNeedCount.isChecked()) {
				tableRowCharCountType.setVisibility(View.VISIBLE);
			} else {
				tableRowCharCountType.setVisibility(View.GONE);
			}
			tableRowCharValues.setVisibility(View.GONE);
			tableRowCharValuesMutli.setVisibility(View.GONE);
			tableRowCharMathA.setVisibility(View.GONE);
			tableRowCharMathB.setVisibility(View.GONE);
			layCharMathType.setVisibility(View.GONE);
			 tbtnCharValuesMutli.setChecked(false);

			tableRowCharHide.setVisibility(View.GONE);
			btnCharMathAId.setText("");
			btnCharMathA.setText("");
			btnCharMathBId.setText("");
			btnCharMathB.setText("");
			btnCharFixValue.setText("");
			btnCharMathType.setText("");
			btnCharMathTypeId.setText("");
		}   else if (typeId.equals("5")) {
			tableRowCharDecimal.setVisibility(View.GONE);
			tableRowCharUnit.setVisibility(View.GONE);
			tableRowCharNeedCount.setVisibility(View.GONE);
			tableRowCharCountType.setVisibility(View.GONE);
			tableRowCharValues.setVisibility(View.VISIBLE);
			tableRowCharValuesMutli.setVisibility(View.VISIBLE);
			tableRowCharMathA.setVisibility(View.GONE);
			tableRowCharMathB.setVisibility(View.GONE);
			layCharMathType.setVisibility(View.GONE);
		 
			btnCharFixValue.setText("");
			btnCharDecimal.setText("0");
			btnCharUnit.setText("");
			tbtnCharNeedCount.setChecked(false);
			btnCharCountType.setText("");
			btnCharCountTypeId.setText("");
			btnCharMathAId.setText("");
			btnCharMathA.setText("");
			btnCharMathBId.setText("");
			btnCharMathB.setText("");
			tableRowCharHide.setVisibility(View.GONE);
			btnCharMathType.setText("");
			btnCharMathTypeId.setText("");
		} else if (typeId.equals("9")) {
			tableRowCharDecimal.setVisibility(View.VISIBLE);
			tableRowCharUnit.setVisibility(View.VISIBLE);
			tableRowCharNeedCount.setVisibility(View.VISIBLE);
			tableRowCharCountType.setVisibility(View.GONE);
			tableRowCharValues.setVisibility(View.GONE);
			tableRowCharValuesMutli.setVisibility(View.GONE);
			tableRowCharMathA.setVisibility(View.VISIBLE);
			tableRowCharMathB.setVisibility(View.VISIBLE);
			layCharMathType.setVisibility(View.VISIBLE);
			tableRowCharHide.setVisibility(View.VISIBLE);
			 
			tbtnCharValuesMutli.setChecked(false);
		} else {
			tableRowCharDecimal.setVisibility(View.GONE);
			tableRowCharUnit.setVisibility(View.GONE);
			tableRowCharNeedCount.setVisibility(View.GONE);
			tableRowCharCountType.setVisibility(View.GONE);
			tableRowCharValues.setVisibility(View.GONE);
			tableRowCharValuesMutli.setVisibility(View.GONE);
			tableRowCharMathA.setVisibility(View.GONE);
			tableRowCharMathB.setVisibility(View.GONE);
			layCharMathType.setVisibility(View.GONE);
			btnCharFixValue.setText("");
			btnCharDecimal.setText("0");
			btnCharUnit.setText("");
			tbtnCharNeedCount.setChecked(false);
			btnCharCountType.setText("");
			btnCharCountTypeId.setText("");
			tbtnCharValuesMutli.setChecked(false);

			btnCharMathAId.setText("");
			btnCharMathA.setText("");
			btnCharMathBId.setText("");
			btnCharMathB.setText("");
			tableRowCharHide.setVisibility(View.GONE);
			btnCharMathType.setText("");
			btnCharMathTypeId.setText("");
		}
		if(typeId.equals("1")||typeId.equals("2")||typeId.equals("3")||typeId.equals("4")||typeId.equals("5"))
		{
			tableRowFixValue.setVisibility(View.VISIBLE);
		}else
		{
			tableRowFixValue.setVisibility(View.GONE);
		}
	}

	private void charMathTypeChanged() {
		if (!btnCharMathA.getText().equals("")
				&& !btnCharMathB.getText().equals("")
				&& !btnCharMathType.getText().equals("")) {
			String val = btnCharMathA.getText().toString()
					+ btnCharMathType.getText().toString()
					+ btnCharMathB.getText().toString();
			String message = this.getResources().getString(
					R.string.char_math_type_result);
			message = MessageFormat.format(message, val);
			tvCharMathDesc.setText(message);
		} else {
			tvCharMathDesc.setText("");
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

	private boolean saveData() {
		try {
			if (btnCharName.getText().toString().equals("")) {
				Animation shakeAnim = AnimationUtils.loadAnimation(this,
						R.anim.shake_x);
				btnCharName.startAnimation(shakeAnim);
				String message = getResources().getString(R.string.not_null);
				message = MessageFormat.format(message, getResources()
						.getString(R.string.char_name));
				Common.showMessage(this, message, Toast.LENGTH_SHORT,
						R.drawable.warning);
				return false;
			} else {
				obj.setCharName(btnCharName.getText().toString());
			}
			btnOk.setClickable(false);
			Common.setDataChangedFlag(CharEditActivity.this);

			obj.setCharNull(tbtnCharCanNull.getText().toString());
			obj.setCharUnit(btnCharUnit.getText().toString());
			obj.setCharCount(tbtnCharNeedCount.getText().toString());
			obj.setCountType(btnCharCountTypeId.getText().toString());
			obj.setCharMulti(tbtnCharValuesMutli.getText().toString());
			obj.setIsHide(tbtnCharHide.getText().toString());
			obj.setCharDecimal(Common.parseInt(btnCharDecimal.getText()
					.toString()));
			obj.setFixValue(btnCharFixValue.getText()
					.toString());
			if (state == Constant.STATE_NEW) {
				UUID newId = UUID.randomUUID();
				obj.setCharSequence(charNums);
				obj.setCharId(newId.toString());
				obj.setCharDataType(btnCharTypeId.getText().toString());
				obj.setColumnName(getColumnName());
				Log.e("obj.getColumnName()", obj.getColumnName());
				if (obj.getColumnName().equals("")) {
					Common.showMessage(this,
							getResources()
									.getString(R.string.char_math_noplace),
							Toast.LENGTH_SHORT, R.drawable.warning);
					return false;
				} else {
					if (dBCaseCharsService.add(obj)) {
						if (obj.getCharDataType().equals("9")) {
							objMath = new DBCaseCharsMath();
							objMath.setCharFrom(btnCharMathAId.getText()
									.toString());
							objMath.setCharTo(btnCharMathBId.getText()
									.toString());
							objMath.setCharMath(btnCharMathTypeId.getText()
									.toString());
							objMath.setCharId(obj.getCharId());
							dBCaseCharsMathService.add(objMath);
						}

						String message = getResources().getString(
								R.string.char_add_success);
						message = MessageFormat.format(message,
								obj.getCharName());
						Common.showMessage(CharEditActivity.this, message,
								Toast.LENGTH_SHORT, R.drawable.success);
					}
				}
			} else {
				if (obj.getCharDataType().equals("9")
						&& !btnCharTypeId.getText().equals("9")) {
					dBCaseCharsMathService.delete(charId);
				} else if (!obj.getCharDataType().equals("9")
						&& btnCharTypeId.getText().equals("9")) {
					objMath = new DBCaseCharsMath();
					objMath.setCharFrom(btnCharMathAId.getText().toString());
					objMath.setCharTo(btnCharMathBId.getText().toString());
					objMath.setCharMath(btnCharMathTypeId.getText().toString());
					objMath.setCharId(obj.getCharId());
					dBCaseCharsMathService.add(objMath);
				} else if (obj.getCharDataType().equals("9")
						&& btnCharTypeId.getText().equals("9")) {
					objMath = dBCaseCharsMathService.getObj(charId);
					objMath.setCharFrom(btnCharMathAId.getText().toString());
					objMath.setCharTo(btnCharMathBId.getText().toString());
					objMath.setCharMath(btnCharMathTypeId.getText().toString());
					dBCaseCharsMathService.update(objMath);
				}
				obj.setCharDataType(btnCharTypeId.getText().toString());
				if (dBCaseCharsService.update(obj)) {
					String message = getResources().getString(
							R.string.char_update_success);
					message = MessageFormat.format(message, obj.getCharName());
					Common.showMessage(CharEditActivity.this, message,
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
			Common.showMessage(CharEditActivity.this, e.getMessage(),
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

	private String getColumnName() {
		List<Map<String, Object>> tempItems = new ArrayList<Map<String, Object>>();
		dBCaseCharsService.queryAll(CharEditActivity.this, tempItems, typeId);
		if (tempItems.size() == 40) {
			return "";
		} else {
			List<Map<String, String>> values = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			for (int j = 1; j <= 40; j++) {
				map = new HashMap<String, String>();
				String val = String.valueOf(j);
				map.put("KEY", val);
				values.add(map);
			}
			for (int i = 0; i < tempItems.size(); i++) {
				String val = tempItems.get(i).get("column_name").toString();
				val = val.replace("character", "");
				map = new HashMap<String, String>();
				map.put("KEY", val);
				values.remove(map);
			}
			return "character" + values.get(0).get("KEY");
		}
	}

	/**
	 * 检测数据是否发生变化
	 */
	private void checkDatachanged() {
		if (!obj.getCharName().equals(btnCharName.getText())
				|| !obj.getCharDataType().equals(btnCharTypeId.getText())
				|| !obj.getCharNull().equals(tbtnCharCanNull.getText())
				|| !obj.getCharUnit().equals(btnCharUnit.getText())
				|| !obj.getCharCount().equals(tbtnCharNeedCount.getText())
				|| !obj.getCountType().equals(btnCharCountTypeId.getText())
				|| !obj.getCharMulti().equals(tbtnCharValuesMutli.getText())
				|| !obj.getIsHide().equals(tbtnCharHide.getText())
				|| !obj.getFixValue().equals(
						btnCharFixValue.getText())
				|| !objMath.getCharFrom().equals(btnCharMathAId.getText())
				|| !objMath.getCharTo().equals(btnCharMathBId.getText())
				|| !objMath.getCharMath().equals(btnCharMathTypeId.getText())
				|| !String.valueOf(obj.getCharDecimal()).equals(
						btnCharDecimal.getText())) {

			CustomDialog.Builder builder = new CustomDialog.Builder(
					CharEditActivity.this);
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
								CharEditActivity.this.finish();
							}
						}
					});
			builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					CharEditActivity.this.finish();
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
			CharEditActivity.this.finish();
		}
	}

	// 返回后，刷新数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle localBundle = Common.getIntentExtras(data);
		if ((localBundle != null)
				&& (localBundle.getString("DataChanged").equals("Y"))) {
			Common.setDataChangedFlag(this);
		}
	}
}
