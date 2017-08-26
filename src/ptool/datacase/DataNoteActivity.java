package ptool.datacase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ptool.datacase.data.service.DBCaseCharsMathService;
import ptool.datacase.data.service.DBCaseCharsService;
import ptool.datacase.data.service.DBCaseDataService;
import ptool.datacase.data.service.DBCaseValuesService;
import ptool.datacase.obj.DBCaseCharsMath;
import ptool.datacase.obj.DBCaseData;
import ptool.datacase.util.Common;
import ptool.datacase.util.Constant;
import ptool.datacase.util.Control;
import ptool.datacase.util.CustomDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataNoteActivity extends Activity {
	// private SystemConfig app;
	private int state;// 记录当前的状态
	private String typeId;
	private String typeName;

	private int indexValue;
	private DBCaseData obj;

	private LinearLayout localLinearLayout;
	private LinearLayout btnOk;
	private LinearLayout btnOkNew;
	private LinearLayout btnCancel;
	private TextView tvRecordIndex;

	private DBCaseDataService dBCaseDataService;
	private DBCaseValuesService dBCaseValuesService;
	private List<Map<String, Object>> charItems = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> valuesItems = new ArrayList<Map<String, Object>>();
	private ArrayList<String> arrayMath = new ArrayList<String>();
	private ArrayList<String> dataIdList = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_note);
		// app = (SystemConfig) this.getApplication();

		dBCaseDataService = new DBCaseDataService(this);
		dBCaseValuesService = new DBCaseValuesService(this);

		Intent intent = getIntent();
		String action = intent.getAction();
		typeId = intent.getStringExtra("TypeId");
		typeName = intent.getStringExtra("TypeName");
		if (action.equals(Intent.ACTION_INSERT)) {
			state = Constant.STATE_NEW;

		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = Constant.STATE_MOIFY;
			indexValue = intent.getIntExtra("IndexValue", 0);
			dataIdList = intent.getStringArrayListExtra("DataIdList");
		}

		DBCaseCharsService dBCaseCharsService = new DBCaseCharsService(this);
		dBCaseCharsService.queryByTypeAll(this, charItems, typeId);

		setTitle();
		setBodyControl();
		initData();
		setToolBar();

	}

	private void initData() {
		if (state == Constant.STATE_NEW) {
			fillNewControl();
			
		} else {
			obj = dBCaseDataService.getObj(dataIdList.get(indexValue), typeId);
			fillModifyControl();
		}
	}

	private void setTitle() {
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		if (state == Constant.STATE_NEW) {
			tvTitle.setText(R.string.lable_new);
		} else {
			tvTitle.setText(R.string.lable_edit);
		}
		tvRecordIndex = (TextView) findViewById(R.id.tvSecondTitle);
	}

	private void setBodyControl() {
		localLinearLayout = (LinearLayout) findViewById(R.id.tableLayout1);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < charItems.size(); i++) {

			View localView = inflater.inflate(R.layout.ui_row, null);
			final TextView textView = ((TextView) localView
					.findViewById(R.id.textView_1));
			ImageButton imgBtn = (ImageButton) localView
					.findViewById(R.id.buttonAdd_1);
			final Button btnValue = (Button) localView
					.findViewById(R.id.button_1);
			TextView tvNotNull = ((TextView) localView
					.findViewById(R.id.tvNotNull_1));
			final TextView tvHide = ((TextView) localView
					.findViewById(R.id.tvHide));
			localView.setTag(100 + i);
			btnValue.setTag(i);
			tvHide.setTag(1000 + i);
			imgBtn.setTag(300 + i);
			String label = charItems.get(i).get("char_name").toString();
			if (!charItems.get(i).get("char_unit").equals("")) {
				label = label + "【"
						+ charItems.get(i).get("char_unit").toString() + "】";
			}
			textView.setText(label + "：");
			String isNull = charItems.get(i).get("char_null").toString();

			if (isNull.equals(Constant.FLAG_YES)) {
				tvNotNull.setVisibility(View.VISIBLE);
			}

			if (charItems.get(i).get("is_hide").equals("是")) {
				localView.setVisibility(View.GONE);
			}
			int charDataType = Integer.parseInt(charItems.get(i)
					.get("char_datatype").toString());
			switch (charDataType) {
			case 1: // 数字
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String charDecimal = charItems
								.get(Integer.parseInt(btnValue.getTag()
										.toString())).get("char_decimal")
								.toString();

						Control.Calculator(DataNoteActivity.this, btnValue,
								findViewById(R.id.layNoteMain),
								Integer.parseInt(charDecimal));
					}
				});
				break;
			case 2: // 单行普通
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Control.ModifyContent(DataNoteActivity.this, btnValue,
								textView.getText().toString(), true);
					}
				});
				break;
			case 3:// 多行文字
					// 安卓下，行数自动适应
					// int lines =
					// Integer.parseInt(charItems.get(i).get("char_rows").toString())
					// ;
				btnValue.setMinLines(5);
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Control.ModifyContent(DataNoteActivity.this, btnValue,
								textView.getText().toString(), false);
					}
				});
				break;
			case 4: // 带掩码
				// 无技术实现方案
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Control.ModifyContent(DataNoteActivity.this, btnValue,
								textView.getText().toString(), true);
					}
				});
				break;
			case 5: // 有值集合
				if (charItems.get(i).get("char_multi")
						.equals(Constant.FLAG_YES)) {
					btnValue.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String charId = charItems
									.get(Integer.parseInt(btnValue.getTag()
											.toString())).get("char_id")
									.toString();
							valuesItems = new ArrayList<Map<String, Object>>();
							dBCaseValuesService.queryValues(
									DataNoteActivity.this, valuesItems, typeId,
									charId);
							Control.MultiChoice(DataNoteActivity.this,
									valuesItems, btnValue, textView.getText()
											.toString());
						}
					});
				} else {
					btnValue.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String charId = charItems
									.get(Integer.parseInt(btnValue.getTag()
											.toString())).get("char_id")
									.toString();
							valuesItems = new ArrayList<Map<String, Object>>();
							dBCaseValuesService.queryValues(
									DataNoteActivity.this, valuesItems, typeId,
									charId);
							Control.DataChoice(DataNoteActivity.this,
									valuesItems, btnValue, textView.getText()
											.toString());
						}
					});
				}
				imgBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Control.ModifyContent(DataNoteActivity.this, btnValue,
								textView.getText().toString(), true);
					}
				});
				break;
			case 6: // 日期
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String isNull = charItems
								.get(Integer.parseInt(btnValue.getTag()
										.toString())).get("char_null")
								.toString();
						Boolean canBlank = false;
						if (isNull.equals(Constant.FLAG_YES)) {
							canBlank = false;
						} else {
							canBlank = true;
						}

						Control.DateChoice(DataNoteActivity.this, tvHide,
								btnValue, textView.getText().toString(),
								canBlank);
					}
				});
				break;
			case 7: // 时间
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String isNull = charItems
								.get(Integer.parseInt(btnValue.getTag()
										.toString())).get("char_null")
								.toString();
						Boolean canBlank = false;
						if (isNull.equals(Constant.FLAG_YES)) {
							canBlank = false;
						} else {
							canBlank = true;
						}
						Control.DateTime(DataNoteActivity.this, btnValue,
								textView.getText().toString(), canBlank);
					}
				});
				break;
			case 8: // 日期时间
				imgBtn.setVisibility(View.GONE);
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String isNull = charItems
								.get(Integer.parseInt(btnValue.getTag()
										.toString())).get("char_null")
								.toString();
						Boolean canBlank = false;
						if (isNull.equals(Constant.FLAG_YES)) {
							canBlank = false;
						} else {
							canBlank = true;
						}
						Control.DateTimeChoice(DataNoteActivity.this, tvHide,
								btnValue, textView.getText().toString(),
								canBlank);
					}
				});
				break;
			case 9: // 计算类
				arrayMath.add(btnValue.getTag().toString());
				imgBtn.setImageResource(R.drawable.icon_refresh);
				imgBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						btnValue.performClick();
					}
				});
				// ---实现计算--
				btnValue.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String charId = charItems
								.get(Integer.parseInt(btnValue.getTag()
										.toString())).get("char_id").toString();
						DBCaseCharsMathService dBCaseCharsMathService = new DBCaseCharsMathService(
								DataNoteActivity.this);
						DBCaseCharsMath objMath = dBCaseCharsMathService
								.getObj(charId);
						String valA = "";
						String valB = "";
						double result = 0;
						for (int i = 0; i < charItems.size(); i++) {
							LinearLayout view = (LinearLayout) localLinearLayout
									.findViewWithTag(100 + i);
							Button btnTmp = (Button) view.findViewWithTag(i);
							if (charItems.get(i).get("char_id")
									.equals(objMath.getCharFrom())) {
								valA = btnTmp.getText().toString();
								if (valA.equals("")) {
									String message = getResources().getString(
											R.string.not_ready);
									message = MessageFormat.format(message,
											charItems.get(i).get("char_name")
													.toString());
									Common.showMessage(DataNoteActivity.this,
											message, Toast.LENGTH_SHORT,
											R.drawable.warning);

									Animation shakeAnim = AnimationUtils
											.loadAnimation(
													DataNoteActivity.this,
													R.anim.shake_x);
									btnTmp.startAnimation(shakeAnim);
									return;
								}
							} else if (charItems.get(i).get("char_id")
									.equals(objMath.getCharTo())) {
								valB = btnTmp.getText().toString();
								if (valB.equals("")) {
									String message = getResources().getString(
											R.string.not_ready);
									message = MessageFormat.format(message,
											charItems.get(i).get("char_name")
													.toString());
									Common.showMessage(DataNoteActivity.this,
											message, Toast.LENGTH_SHORT,
											R.drawable.warning);

									Animation shakeAnim = AnimationUtils
											.loadAnimation(
													DataNoteActivity.this,
													R.anim.shake_x);
									btnTmp.startAnimation(shakeAnim);
									return;
								}
							}
						}
						if (!valA.equals("") && !valB.equals("")) {
							if (objMath.getCharMath().equals("0")) {
								result = Common.parseDouble(valA)
										+ Common.parseDouble(valB);
							} else if (objMath.getCharMath().equals("1")) {
								result = Common.parseDouble(valA)
										- Common.parseDouble(valB);
							} else if (objMath.getCharMath().equals("2")) {
								result = Common.parseDouble(valA)
										* Common.parseDouble(valB);
							} else if (objMath.getCharMath().equals("3")) {
								result = Common.parseDouble(valA)
										/ Common.parseDouble(valB);
							}
						}
						String charDecimal = charItems
								.get(Common.parseInt(btnValue.getTag()
										.toString())).get("char_decimal")
								.toString();
						btnValue.setText(Common.covertToDouble(result,
								Common.parseInt(charDecimal)));
					}
				});
				break;
			}
			localLinearLayout.addView(localView);
		}
	}

	private boolean saveData() {
		if (fillDataObj()) {
			if (state == Constant.STATE_NEW) {

				UUID newId = UUID.randomUUID();
				obj.setDataId(newId.toString());
				obj.setTypeId(typeId);
				if (dBCaseDataService.add(obj)) {
					String message = getResources().getString(
							R.string.message_add_success);
					message = MessageFormat.format(message, typeName);
					Common.showMessage(this, message, Toast.LENGTH_SHORT,
							R.drawable.success);
					Common.setDataChangedFlag(this);
					return true;
				} else {
					return false;
				}
			} else {
				if (dBCaseDataService.update(obj)) {
					String message = getResources().getString(
							R.string.message_update_success);
					message = MessageFormat.format(message, typeName);
					Common.showMessage(this, message, Toast.LENGTH_SHORT,
							R.drawable.success);
					Common.setDataChangedFlag(this);
					return true;
				} else {
					return false;
				}
			}

		} else {
			return false;
		}
	}

	private void checkDatachanged() {
		boolean changed = false;
		for (int i = 0; i < charItems.size(); i++) {
			LinearLayout view = (LinearLayout) localLinearLayout
					.findViewWithTag(100 + i);
			Button btnValue = (Button) view.findViewWithTag(i);
			TextView tvHideValue = (TextView) view.findViewWithTag(1000 + i);
			String value = btnValue.getText().toString();
			int charDataType = Integer.parseInt(charItems.get(i)
					.get("char_datatype").toString());
			switch (charDataType) {
			case 6: // 日期
				value = tvHideValue.getText().toString();
				break;

			case 8: // 日期时间
				value = tvHideValue.getText().toString();
				break;
			}

			if (charItems.get(i).get("column_name").equals("character1")) {
				if (!obj.getCharacter1().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character2")) {
				if (!obj.getCharacter2().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character3")) {
				if (!obj.getCharacter3().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character4")) {
				if (!obj.getCharacter4().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character5")) {
				if (!obj.getCharacter5().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character6")) {
				if (!obj.getCharacter6().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character7")) {
				if (!obj.getCharacter7().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character8")) {
				if (!obj.getCharacter8().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name").equals("character9")) {
				if (!obj.getCharacter9().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character10")) {
				if (!obj.getCharacter10().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character11")) {
				if (!obj.getCharacter11().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character12")) {
				if (!obj.getCharacter12().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character13")) {
				if (!obj.getCharacter13().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character14")) {
				if (!obj.getCharacter14().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character15")) {
				if (!obj.getCharacter15().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character16")) {
				if (!obj.getCharacter16().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character17")) {
				if (!obj.getCharacter17().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character18")) {
				if (!obj.getCharacter18().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character19")) {
				if (!obj.getCharacter19().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character20")) {
				if (!obj.getCharacter20().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character21")) {
				if (!obj.getCharacter21().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character22")) {
				if (!obj.getCharacter22().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character23")) {
				if (!obj.getCharacter23().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character24")) {
				if (!obj.getCharacter24().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character25")) {
				if (!obj.getCharacter25().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character26")) {
				if (!obj.getCharacter26().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character27")) {
				if (!obj.getCharacter27().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character28")) {
				if (!obj.getCharacter28().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character29")) {
				if (!obj.getCharacter29().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character30")) {
				if (!obj.getCharacter30().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character31")) {
				if (!obj.getCharacter31().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character32")) {
				if (!obj.getCharacter32().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character33")) {
				if (!obj.getCharacter33().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character34")) {
				if (!obj.getCharacter34().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character35")) {
				if (!obj.getCharacter35().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character36")) {
				if (!obj.getCharacter36().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character37")) {
				if (!obj.getCharacter37().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character38")) {
				if (!obj.getCharacter38().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character39")) {
				if (!obj.getCharacter39().equals(value)) {
					changed = true;
				}
			} else if (charItems.get(i).get("column_name")
					.equals("character40")) {
				if (!obj.getCharacter40().equals(value)) {
					changed = true;
				}
			}
		}
		if (changed) {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					DataNoteActivity.this);
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
								DataNoteActivity.this.finish();
							}
						}
					});
			builder.setNeutralButton(R.string.lable_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					DataNoteActivity.this.finish();
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
			DataNoteActivity.this.finish();
		}
	}

	private boolean fillDataObj() {
		if (arrayMath.size() > 0) {
			for (int j = 0; j < arrayMath.size(); j++) {
				int index = Common.parseInt(arrayMath.get(j));
				LinearLayout view = (LinearLayout) localLinearLayout
						.findViewWithTag(100 + index);
				Button btnValue = (Button) view.findViewWithTag(index);
				btnValue.performClick();
			}
			// 避免因计算要素顺序问题，而造成计算问题
			if (arrayMath.size() > 1) {
				for (int j = arrayMath.size() - 1; j > -1; j--) {
					int index = Common.parseInt(arrayMath.get(j));
					LinearLayout view = (LinearLayout) localLinearLayout
							.findViewWithTag(100 + index);
					Button btnValue = (Button) view.findViewWithTag(index);
					btnValue.performClick();
				}
			}
		}
		for (int i = 0; i < charItems.size(); i++) {
			LinearLayout view = (LinearLayout) localLinearLayout
					.findViewWithTag(100 + i);
			Button btnValue = (Button) view.findViewWithTag(i);
			ImageButton imgBtn = (ImageButton) view.findViewWithTag(300 + i);
			TextView tvHideValue = (TextView) view.findViewWithTag(1000 + i);

			String isNull = charItems.get(i).get("char_null").toString();
			// 非空提醒
			if (isNull.equals(Constant.FLAG_YES)) {
				if (btnValue.getText().equals("")) {
					String message = getResources()
							.getString(R.string.not_null);
					message = MessageFormat.format(message, charItems.get(i)
							.get("char_name").toString());
					Common.showMessage(this, message, Toast.LENGTH_SHORT,
							R.drawable.warning);

					Animation shakeAnim = AnimationUtils.loadAnimation(this,
							R.anim.shake_x);
					btnValue.startAnimation(shakeAnim);
					imgBtn.startAnimation(shakeAnim);
					return false;
				}
			}
			String value = btnValue.getText().toString();
			int charDataType = Integer.parseInt(charItems.get(i)
					.get("char_datatype").toString());
			switch (charDataType) {
			case 6: // 日期
				value = tvHideValue.getText().toString();
				break;
			case 8: // 日期时间
				value = tvHideValue.getText().toString();
				break;
			
			}
			if (charItems.get(i).get("column_name").equals("character1")) {
				obj.setCharacter1(value);
			} else if (charItems.get(i).get("column_name").equals("character2")) {
				obj.setCharacter2(value);
			} else if (charItems.get(i).get("column_name").equals("character3")) {
				obj.setCharacter3(value);
			} else if (charItems.get(i).get("column_name").equals("character4")) {
				obj.setCharacter4(value);
			} else if (charItems.get(i).get("column_name").equals("character5")) {
				obj.setCharacter5(value);
			} else if (charItems.get(i).get("column_name").equals("character6")) {
				obj.setCharacter6(value);
			} else if (charItems.get(i).get("column_name").equals("character7")) {
				obj.setCharacter7(value);
			} else if (charItems.get(i).get("column_name").equals("character8")) {
				obj.setCharacter8(value);
			} else if (charItems.get(i).get("column_name").equals("character9")) {
				obj.setCharacter9(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character10")) {
				obj.setCharacter10(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character11")) {
				obj.setCharacter11(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character12")) {
				obj.setCharacter12(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character13")) {
				obj.setCharacter13(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character14")) {
				obj.setCharacter14(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character15")) {
				obj.setCharacter15(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character16")) {
				obj.setCharacter16(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character17")) {
				obj.setCharacter17(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character18")) {
				obj.setCharacter18(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character19")) {
				obj.setCharacter19(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character20")) {
				obj.setCharacter20(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character21")) {
				obj.setCharacter21(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character22")) {
				obj.setCharacter22(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character23")) {
				obj.setCharacter23(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character24")) {
				obj.setCharacter24(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character25")) {
				obj.setCharacter25(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character26")) {
				obj.setCharacter26(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character27")) {
				obj.setCharacter27(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character28")) {
				obj.setCharacter28(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character29")) {
				obj.setCharacter29(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character30")) {
				obj.setCharacter30(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character31")) {
				obj.setCharacter31(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character32")) {
				obj.setCharacter32(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character33")) {
				obj.setCharacter33(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character34")) {
				obj.setCharacter34(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character35")) {
				obj.setCharacter35(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character36")) {
				obj.setCharacter36(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character37")) {
				obj.setCharacter37(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character38")) {
				obj.setCharacter38(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character39")) {
				obj.setCharacter39(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character40")) {
				obj.setCharacter40(value);
			}

		}
		return true;
	}

	private void fillNewControl()
	{
		obj = new DBCaseData();
	 	
		for (int i = 0; i < charItems.size(); i++) {
			LinearLayout view = (LinearLayout) localLinearLayout
					.findViewWithTag(100 + i);
			Button btnValue = (Button) view.findViewWithTag(i);
		 	String value = "";
		 	
		 	value = charItems.get(i)
						.get("fix_value").toString();
		 
			if (charItems.get(i).get("column_name").equals("character1")) {
				obj.setCharacter1(value);
			} else if (charItems.get(i).get("column_name").equals("character2")) {
				obj.setCharacter2(value);
			} else if (charItems.get(i).get("column_name").equals("character3")) {
				obj.setCharacter3(value);
			} else if (charItems.get(i).get("column_name").equals("character4")) {
				obj.setCharacter4(value);
			} else if (charItems.get(i).get("column_name").equals("character5")) {
				obj.setCharacter5(value);
			} else if (charItems.get(i).get("column_name").equals("character6")) {
				obj.setCharacter6(value);
			} else if (charItems.get(i).get("column_name").equals("character7")) {
				obj.setCharacter7(value);
			} else if (charItems.get(i).get("column_name").equals("character8")) {
				obj.setCharacter8(value);
			} else if (charItems.get(i).get("column_name").equals("character9")) {
				obj.setCharacter9(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character10")) {
				obj.setCharacter10(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character11")) {
				obj.setCharacter11(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character12")) {
				obj.setCharacter12(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character13")) {
				obj.setCharacter13(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character14")) {
				obj.setCharacter14(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character15")) {
				obj.setCharacter15(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character16")) {
				obj.setCharacter16(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character17")) {
				obj.setCharacter17(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character18")) {
				obj.setCharacter18(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character19")) {
				obj.setCharacter19(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character20")) {
				obj.setCharacter20(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character21")) {
				obj.setCharacter21(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character22")) {
				obj.setCharacter22(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character23")) {
				obj.setCharacter23(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character24")) {
				obj.setCharacter24(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character25")) {
				obj.setCharacter25(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character26")) {
				obj.setCharacter26(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character27")) {
				obj.setCharacter27(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character28")) {
				obj.setCharacter28(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character29")) {
				obj.setCharacter29(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character30")) {
				obj.setCharacter30(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character31")) {
				obj.setCharacter31(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character32")) {
				obj.setCharacter32(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character33")) {
				obj.setCharacter33(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character34")) {
				obj.setCharacter34(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character35")) {
				obj.setCharacter35(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character36")) {
				obj.setCharacter36(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character37")) {
				obj.setCharacter37(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character38")) {
				obj.setCharacter38(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character39")) {
				obj.setCharacter39(value);
			} else if (charItems.get(i).get("column_name")
					.equals("character40")) {
				 obj.setCharacter40(value);
			}
			btnValue.setText(value);
		}
	}
	
	private boolean fillModifyControl() {
		if (state == Constant.STATE_MOIFY) {
			tvRecordIndex.setText((indexValue + 1) + "/" + dataIdList.size());
		}
		for (int i = 0; i < charItems.size(); i++) {
			LinearLayout view = (LinearLayout) localLinearLayout
					.findViewWithTag(100 + i);
			Button btnValue = (Button) view.findViewWithTag(i);
			TextView tvHideValue = (TextView) view.findViewWithTag(1000 + i);
			String value = "";

			if (charItems.get(i).get("column_name").equals("character1")) {
				value = obj.getCharacter1();

			} else if (charItems.get(i).get("column_name").equals("character2")) {
				value = obj.getCharacter2();
			} else if (charItems.get(i).get("column_name").equals("character3")) {
				value = obj.getCharacter3();
			} else if (charItems.get(i).get("column_name").equals("character4")) {
				value = obj.getCharacter4();
			} else if (charItems.get(i).get("column_name").equals("character5")) {
				value = obj.getCharacter5();
			} else if (charItems.get(i).get("column_name").equals("character6")) {
				value = obj.getCharacter6();
			} else if (charItems.get(i).get("column_name").equals("character7")) {
				value = obj.getCharacter7();
			} else if (charItems.get(i).get("column_name").equals("character8")) {
				value = obj.getCharacter8();
			} else if (charItems.get(i).get("column_name").equals("character9")) {
				value = obj.getCharacter9();
			} else if (charItems.get(i).get("column_name")
					.equals("character10")) {
				value = obj.getCharacter10();
			} else if (charItems.get(i).get("column_name")
					.equals("character11")) {
				value = obj.getCharacter11();
			} else if (charItems.get(i).get("column_name")
					.equals("character12")) {
				value = obj.getCharacter12();
			} else if (charItems.get(i).get("column_name")
					.equals("character13")) {
				value = obj.getCharacter13();
			} else if (charItems.get(i).get("column_name")
					.equals("character14")) {
				value = obj.getCharacter14();
			} else if (charItems.get(i).get("column_name")
					.equals("character15")) {
				value = obj.getCharacter15();
			} else if (charItems.get(i).get("column_name")
					.equals("character16")) {
				value = obj.getCharacter16();
			} else if (charItems.get(i).get("column_name")
					.equals("character17")) {
				value = obj.getCharacter17();
			} else if (charItems.get(i).get("column_name")
					.equals("character18")) {
				value = obj.getCharacter18();
			} else if (charItems.get(i).get("column_name")
					.equals("character19")) {
				value = obj.getCharacter19();
			} else if (charItems.get(i).get("column_name")
					.equals("character20")) {
				value = obj.getCharacter20();
			} else if (charItems.get(i).get("column_name")
					.equals("character21")) {
				value = obj.getCharacter21();
			} else if (charItems.get(i).get("column_name")
					.equals("character22")) {
				value = obj.getCharacter22();
			} else if (charItems.get(i).get("column_name")
					.equals("character23")) {
				value = obj.getCharacter23();
			} else if (charItems.get(i).get("column_name")
					.equals("character24")) {
				value = obj.getCharacter24();
			} else if (charItems.get(i).get("column_name")
					.equals("character25")) {
				value = obj.getCharacter25();
			} else if (charItems.get(i).get("column_name")
					.equals("character26")) {
				value = obj.getCharacter26();
			} else if (charItems.get(i).get("column_name")
					.equals("character27")) {
				value = obj.getCharacter27();
			} else if (charItems.get(i).get("column_name")
					.equals("character28")) {
				value = obj.getCharacter28();
			} else if (charItems.get(i).get("column_name")
					.equals("character29")) {
				value = obj.getCharacter29();
			} else if (charItems.get(i).get("column_name")
					.equals("character30")) {
				value = obj.getCharacter30();
			} else if (charItems.get(i).get("column_name")
					.equals("character31")) {
				value = obj.getCharacter31();
			} else if (charItems.get(i).get("column_name")
					.equals("character32")) {
				value = obj.getCharacter32();
			} else if (charItems.get(i).get("column_name")
					.equals("character33")) {
				value = obj.getCharacter33();
			} else if (charItems.get(i).get("column_name")
					.equals("character34")) {
				value = obj.getCharacter34();
			} else if (charItems.get(i).get("column_name")
					.equals("character35")) {
				value = obj.getCharacter35();
			} else if (charItems.get(i).get("column_name")
					.equals("character36")) {
				value = obj.getCharacter36();
			} else if (charItems.get(i).get("column_name")
					.equals("character37")) {
				value = obj.getCharacter37();
			} else if (charItems.get(i).get("column_name")
					.equals("character38")) {
				value = obj.getCharacter38();
			} else if (charItems.get(i).get("column_name")
					.equals("character39")) {
				value = obj.getCharacter39();
			} else if (charItems.get(i).get("column_name")
					.equals("character40")) {
				value = obj.getCharacter40();
			}
			int charDataType = Integer.parseInt(charItems.get(i)
					.get("char_datatype").toString());
			switch (charDataType) {
			case 6: // 日期
				tvHideValue.setText(value);
				value = Common.getDateStringShort(value);
				break;
			case 8: // 日期时间
				tvHideValue.setText(value);
				value = Common.getDateString(value);
				break;
			}
			btnValue.setText(value);
		}
		return true;
	}

	private void setToolBar() {
		LinearLayout btnGetBack = (LinearLayout) this
				.findViewById(R.id.layoutGetBack);
		LinearLayout btnGetNext = (LinearLayout) this
				.findViewById(R.id.layoutGetNext);

		btnOkNew = (LinearLayout) this.findViewById(R.id.layoutOkNew);
		if (state == Constant.STATE_NEW) {
			btnOkNew.setOnClickListener(okNewListener);
			btnGetBack.setVisibility(View.GONE);
			btnGetNext.setVisibility(View.GONE);
		} else {
			btnOkNew.setVisibility(View.GONE);
			btnGetBack.setVisibility(View.GONE);
			btnGetNext.setVisibility(View.GONE);
			if (dataIdList != null) {
				if (dataIdList.size() > 1) {
					btnGetBack.setVisibility(View.VISIBLE);
					btnGetNext.setVisibility(View.VISIBLE);
					btnGetBack.setOnClickListener(getBackListener);
					btnGetNext.setOnClickListener(getNextListener);
				}
			}
		}
		btnOk = (LinearLayout) this.findViewById(R.id.layoutOk);
		btnCancel = (LinearLayout) this.findViewById(R.id.layoutCancel);
		btnOk.setOnClickListener(okListener);
		btnCancel.setOnClickListener(cancelListener);
	}

	private View.OnClickListener okNewListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (saveData()) {
				obj = new DBCaseData();
				fillModifyControl();
			}
		}
	};
	private View.OnClickListener okListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (saveData()) {
				if (state == Constant.STATE_NEW) {
					DataNoteActivity.this.finish();
				} else {
					obj = dBCaseDataService.getObj(dataIdList.get(indexValue),
							typeId);
					fillModifyControl();
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
	private View.OnClickListener getBackListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (indexValue == 0) {
				indexValue = dataIdList.size() - 1;
			} else {
				indexValue--;
			}
			obj = dBCaseDataService.getObj(dataIdList.get(indexValue), typeId);
			fillModifyControl();
		}
	};

	private View.OnClickListener getNextListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (indexValue == dataIdList.size() - 1) {
				indexValue = 0;
			} else {
				indexValue++;
			}
			obj = dBCaseDataService.getObj(dataIdList.get(indexValue), typeId);
			fillModifyControl();
		}
	};

}