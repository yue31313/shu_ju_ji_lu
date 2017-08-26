package ptool.datacase.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.HelpWebActivity;
import ptool.datacase.R;
import ptool.datacase.util.control.ArrayWheelAdapter;
import ptool.datacase.util.control.CommonChoiceGridViewAdapter;
import ptool.datacase.util.control.CommonChoiceListViewAdapter;
import ptool.datacase.util.control.IconEditorView;
import ptool.datacase.util.control.NumericWheelAdapter;
import ptool.datacase.util.control.OnWheelChangedListener;
import ptool.datacase.util.control.WheelView;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 共用控件方法
 * 
 * @author ZhaoJJ
 * 
 */
public class Control {

	private static Calendar cal;
	private static Button btnReturn;
	private static Button btnReturnId;
	private static TextView tvHideReturn;

	private static IconEditorView iconReturn;

	private static EditText etTemp;
	private static String count_type; // 計算類型
	private static boolean have_input; // 判断選擇計算類型後，有否錄入值。
	private static boolean have_count; // 判断是否已经計算。
	private static double first_num; // 計算時，第一参数值
	private static StringBuffer calLog;
	private static boolean fromCal;
	private static String lastFlag;
	private static boolean digClick;

	private static View layout;

	private static CustomDialog dialog;
	private static Activity activity;

	/**
	 * 
	 * @param act
	 * @param btn
	 * @param titleInt
	 * @param singLine
	 */
	public static void ModifyContent(Activity act, Button btn, int titleInt,
			boolean singLine) {
		ModifyContent(act, btn, act.getResources().getString(titleInt),
				singLine, false);
	}

	/**
	 * 
	 * @param act
	 * @param btn
	 * @param titleStr
	 * @param singLine
	 */
	public static void ModifyContent(Activity act, Button btn, String titleStr,
			boolean singLine) {
		ModifyContent(act, btn, titleStr, singLine, false);
	}

	/**
	 * 
	 * @param act
	 * @param btn
	 * @param titleInt
	 * @param singLine
	 * @param isNumber
	 */
	public static void ModifyContent(Activity act, Button btn, int titleInt,
			boolean singLine, boolean isNumber) {
		ModifyContent(act, btn, act.getResources().getString(titleInt),
				singLine, isNumber);
	}

	/**
	 * 文本录入控件
	 * 
	 * @param act
	 * @param btn
	 *            映射控件
	 * @param titleStr
	 *            弹出框标题
	 * @param singLine
	 *            是否单行
	 * @param isNumber
	 *            是否数字
	 */
	public static void ModifyContent(Activity act, Button btn, String titleStr,
			boolean singLine, boolean isNumber) {
		btnReturn = btn;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			if (!titleStr.equals(""))
				builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.widget_content, null);

			final EditText etContent = (EditText) layout
					.findViewById(R.id.etContent);
			Common.showInputMethod(act);
			etContent.setText(btn.getText());
			if (singLine) {
				etContent.setSingleLine(true);
				LayoutParams lp = etContent.getLayoutParams();
				lp.height = LayoutParams.WRAP_CONTENT;
				etContent.setLayoutParams(lp);
			}
			if (isNumber) {
				etContent.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
			builder.setView(layout);
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					btnReturn.setText(etContent.getText());
				}
			});
			builder.create().show();

		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	/**
	 * 一层列表单选控件，无ID时
	 * 
	 * @param act
	 * @param listItem
	 * @param btn
	 */
	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, int titleId) {
		Button btnTmp = new Button(act);
		DataChoice(act, listItem, btn, btnTmp, titleId);
	}

	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, String titleStr) {
		Button btnTmp = new Button(act);
		DataChoice(act, listItem, btn, btnTmp, titleStr);
	}

	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, Button btnId,
			int titleId) {
		DataChoice(act, listItem, btn, btnId,
				act.getResources().getString(titleId));
	}

	/**
	 * 一层列表单选控件
	 * 
	 * @param act
	 * @param listItem
	 * @param btn
	 * @param btnId
	 */
	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, Button btnId,
			String titleStr) {
		btnReturnId = btnId;
		// activity = act;
		btnReturn = btn;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.list_view_select, null);
			builder.setView(layout);
			//
			int selectIndex = 0;
			Map<String, Object> map = new HashMap<String, Object>();
			Boolean isEqual = false;

			for (int i = 0; i < listItem.size(); i++) {
				isEqual = false;
				map = new HashMap<String, Object>();
				if (!btn.getText().toString().equals("")) {
					if (btn.getText().toString()
							.equals(listItem.get(i).get("ListItemTitle"))) {
						isEqual = true;
					}
				} else if (listItem.get(i).get("ListItemTitle").equals("")) {
					isEqual = true;
				}
				if (isEqual) {
					selectIndex = i;
					map.put("ListItemImage",
							listItem.get(i).get("ListItemImage"));// 图像资源的ID
					map.put("ListItemTitle",
							listItem.get(i).get("ListItemTitle"));
					map.put("ListItemTitleTwo",
							listItem.get(i).get("ListItemTitleTwo"));
					map.put("ListItemId", listItem.get(i).get("ListItemId"));
					map.put("ListItemCheck", R.drawable.success);
					listItem.set(i, map);
				} else {
					if (listItem.get(i).get("ListItemCheck") != null) {
						map.put("ListItemImage",
								listItem.get(i).get("ListItemImage"));// 图像资源的ID
						map.put("ListItemTitle",
								listItem.get(i).get("ListItemTitle"));
						map.put("ListItemTitleTwo",
								listItem.get(i).get("ListItemTitleTwo"));
						map.put("ListItemId", listItem.get(i).get("ListItemId"));
						map.put("ListItemCheck", null);
						listItem.set(i, map);
					}
				}
			}

			// 绑定Layout里面的ListView
			ListView list = (ListView) layout.findViewById(R.id.lvSelect);

			CommonChoiceListViewAdapter listViewAdapter = new CommonChoiceListViewAdapter(
					act, listItem); // 创建适配器
			list.setAdapter(listViewAdapter);
			list.setSelection(selectIndex);

			// 添加点击
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> item = (HashMap<String, Object>) arg0
							.getItemAtPosition(arg2);

					btnReturn.setText(item.get("ListItemTitle").toString());
					btnReturnId.setText(item.get("ListItemId").toString());
					dialog.dismiss();
				}
			});

			dialog = builder.create();
			// dialog.setInverseBackgroundForced(true);
			dialog.show();
		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, Button btnId,
			IconEditorView icon, int titleId) {
		DataChoice(act, listItem, btn, btnId, icon, act.getResources()
				.getString(titleId));
	}

	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn,
			IconEditorView icon, String title) {
		Button btnTmp = new Button(act);
		DataChoice(act, listItem, btn, btnTmp, icon, title);
	}

	public static void DataChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, Button btnId,
			IconEditorView icon, String titleStr) {
		btnReturnId = btnId;
		iconReturn = icon;
		btnReturn = btn;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.list_view_select, null);
			builder.setView(layout);
			//
			int selectIndex = 0;
			Map<String, Object> map = new HashMap<String, Object>();
			Boolean isEqual = false;

			for (int i = 0; i < listItem.size(); i++) {
				isEqual = false;
				map = new HashMap<String, Object>();
				if (!btn.getText().toString().equals("")) {
					if (btn.getText().toString()
							.equals(listItem.get(i).get("ListItemTitle"))) {
						isEqual = true;
					}
				} else if (listItem.get(i).get("ListItemTitle").equals("")) {
					isEqual = true;
				}
				if (isEqual) {
					selectIndex = i;
					map.put("ListItemImage",
							listItem.get(i).get("ListItemImage"));// 图像资源的ID
					map.put("ListItemTitle",
							listItem.get(i).get("ListItemTitle"));
					map.put("ListItemId", listItem.get(i).get("ListItemId"));
					map.put("ListItemCheck", R.drawable.success);
					listItem.set(i, map);
				} else {
					if (listItem.get(i).get("ListItemCheck") != null) {
						map.put("ListItemImage",
								listItem.get(i).get("ListItemImage"));// 图像资源的ID
						map.put("ListItemTitle",
								listItem.get(i).get("ListItemTitle"));
						map.put("ListItemId", listItem.get(i).get("ListItemId"));
						map.put("ListItemCheck", null);
						listItem.set(i, map);
					}
				}
			}

			// 绑定Layout里面的ListView
			ListView list = (ListView) layout.findViewById(R.id.lvSelect);

			CommonChoiceListViewAdapter listViewAdapter = new CommonChoiceListViewAdapter(
					act, listItem); // 创建适配器
			list.setAdapter(listViewAdapter);
			list.setSelection(selectIndex);

			// 添加点击
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> item = (HashMap<String, Object>) arg0
							.getItemAtPosition(arg2);

					btnReturn.setText(item.get("ListItemTitle").toString());
					btnReturnId.setText(item.get("ListItemId").toString());
					if (item.get("ListItemTitle").toString().equals("")
							|| item.get("ListItemTitle").toString()
									.equals(Constant.FLAG_EMPTY)) {
						iconReturn.setPhotoDrawable(null);
					} else {
						Drawable d = (Drawable) item.get("ListItemImage");
						iconReturn.setPhotoDrawable(d);
					}

					dialog.dismiss();
				}
			});

			dialog = builder.create();
			// dialog.setInverseBackgroundForced(true);
			dialog.show();
		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	public static void IconChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn,
			IconEditorView icon, String titleStr, final String iconPath) {
		activity = act;
		iconReturn = icon;
		btnReturn = btn;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.grid_view_select, null);
			builder.setView(layout);
			//
			int selectIndex = 0;
			Map<String, Object> map = new HashMap<String, Object>();
			Boolean isEqual = false;
			for (int i = 0; i < listItem.size(); i++) {
				isEqual = false;
				map = new HashMap<String, Object>();
				if (!btn.getText().toString().equals("")) {
					if (btn.getText().toString()
							.equals(listItem.get(i).get("ListItemTitle"))) {
						isEqual = true;
					}
				} else if (listItem.get(i).get("ListItemTitle").equals("")) {
					isEqual = true;
				}
				if (isEqual) {
					selectIndex = i;
					map.put("ListItemImage",
							listItem.get(i).get("ListItemImage"));// 图像资源的ID
					map.put("ListItemTitle",
							listItem.get(i).get("ListItemTitle"));
					// map.put("ListItemId", listItem.get(i).get("ListItemId"));
					map.put("ListItemCheck", R.drawable.edit_focused);
					listItem.set(i, map);
				} else {
					if (listItem.get(i).get("ListItemCheck") != null) {
						map.put("ListItemImage",
								listItem.get(i).get("ListItemImage"));// 图像资源的ID
						map.put("ListItemTitle",
								listItem.get(i).get("ListItemTitle"));
						// map.put("ListItemId",
						// listItem.get(i).get("ListItemId"));
						map.put("ListItemCheck", null);
						listItem.set(i, map);
					}
				}
			}
			GridView list = (GridView) layout.findViewById(R.id.gridSelect);
			CommonChoiceGridViewAdapter gridViewAdapter = new CommonChoiceGridViewAdapter(
					act, listItem); // 创建适配器
			list.setAdapter(gridViewAdapter);
			list.setSelection(selectIndex);

			// 添加点击
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> item = (HashMap<String, Object>) arg0
							.getItemAtPosition(arg2);
					btnReturn.setText(item.get("ListItemTitle").toString());
					if (item.get("ListItemTitle").toString().equals("")
							|| item.get("ListItemTitle").toString()
									.equals(Constant.FLAG_EMPTY)) {
						iconReturn.setPhotoDrawable(null);
					} else {
						Drawable d = (Drawable) item.get("ListItemImage");
						iconReturn.setPhotoDrawable(d);
					}
					dialog.dismiss();
				}
			});

			DialogInterface.OnClickListener downListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					Intent intent = new Intent(activity, HelpWebActivity.class);
					intent.putExtra("IconPath", iconPath);
					activity.startActivityForResult(intent,
							Constant.REQ_HELP_WEB);
					dialog.dismiss();
				}
			};
			builder.setPositiveButton(R.string.lable_code_icon_down,
					downListener);

			dialog = builder.create();
			dialog.show();
		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void MultiChoice(Activity act,
			List<Map<String, Object>> listItem, Button btn, String titleStr) {
		// activity = act;
		btnReturn = btn;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.list_view_select, null);
			builder.setView(layout);

			int selectIndex = 0;
			Map<String, Object> map = new HashMap<String, Object>();
			String[] sels = btn.getText().toString().split(",");
			List<String> tempList = Arrays.asList(sels); // 通过asList转换的List
															// 不能直接Add
			final List selList = new ArrayList(tempList);
			for (int i = 0; i < listItem.size(); i++) {
				map = new HashMap<String, Object>();
				map.put("ListItemImage", listItem.get(i).get("ListItemImage"));// 图像资源的ID
				map.put("ListItemTitle", listItem.get(i).get("ListItemTitle"));
				map.put("ListItemId", listItem.get(i).get("ListItemId"));
				if (selList.contains(listItem.get(i).get("ListItemTitle"))) {
					map.put("ListItemCheck", R.drawable.success);
					selectIndex = i;
				} else {
					map.put("ListItemCheck", null);
				}
				listItem.set(i, map);
			}

			// 绑定Layout里面的ListView
			ListView list = (ListView) layout.findViewById(R.id.lvSelect);
			// if (listItem.size() > 1) { //只有一条记录时，点击记录即关闭选择
			setMultiData(act, listItem, list, dialog, selList);

			list.setSelection(selectIndex);
			DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					btnReturn.setText(Common.listToString(selList, ","));
					dialog.dismiss();
				}
			};
			builder.setPositiveButton(R.string.ok, okListener);

			DialogInterface.OnClickListener clearListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					selList.clear();
					btnReturn.setText("");
					dialog.dismiss();
				}
			};
			builder.setNegativeButton(R.string.clear, clearListener);

			dialog = builder.create();
			dialog.show();
		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	private static void setMultiData(final Activity act,
			final List<Map<String, Object>> listItem, final ListView list,
			final CustomDialog dialog, final List<String> selList) {
		CommonChoiceListViewAdapter listItemAdapter = new CommonChoiceListViewAdapter(
				act, listItem); // 创建适配器
		list.setAdapter(listItemAdapter);
		// 添加点击
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Map<String, Object> map = listItem.get(arg2);
				if (selList.size() > 0)
					selList.remove(map.get("ListItemTitle").toString()); // 暂时，只需要处理标题
				if (map.get("ListItemCheck") == null) {
					map.put("ListItemCheck", R.drawable.success);
					selList.add(map.get("ListItemTitle").toString());
				} else {
					map.put("ListItemCheck", null);
				}
				listItem.set(arg2, map);
				int v = list.getFirstVisiblePosition();
				setMultiData(act, listItem, list, dialog, selList);
				list.setSelection(v);
			}
		});
	}

	/**
	 * 
	 * @param act
	 * @param arrayOfString
	 * @param arrayList
	 * @param btn
	 * @param titleStr
	 */
	public static void MultiChoice(Activity act, final String[] arrayOfString,
			final ArrayList<Integer> arrayList, Button btn, String titleStr) {
		btnReturn = btn;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			if (arrayOfString.length > 0) {
				final boolean[] selected = new boolean[arrayOfString.length];
				if (arrayList.size() > 0) {
					for (int i = 0; i < arrayOfString.length; i++) {
						selected[i] = false;
					}
					for (int k = 0; k < arrayList.size(); k++) {
						selected[arrayList.get(k)] = true;
					}
				}
				CustomDialog.Builder builder = new CustomDialog.Builder(act);
				builder.setTitle(titleStr);
				DialogInterface.OnMultiChoiceClickListener mutiListener = new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface,
							int which, boolean isChecked) {
						selected[which] = isChecked;
					}
				};
				builder.setMultiChoiceItems(btnReturn.getText().toString(),
						arrayOfString, selected, mutiListener);
				DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface,
							int which) {
						String selectedStr = "";
						arrayList.clear();
						for (int i = 0; i < selected.length; i++) {
							if (selected[i] == true) {
								selectedStr = selectedStr + ","
										+ arrayOfString[i];
								arrayList.add(i);
							}
						}
						if (selectedStr.length() > 1)
							selectedStr = selectedStr.substring(1,
									selectedStr.length());
						btnReturn.setText(selectedStr);
						dialog.dismiss();
					}
				};
				builder.setPositiveButton(R.string.ok, okListener);

				DialogInterface.OnClickListener clearListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface,
							int which) {
						arrayList.clear();
						btnReturn.setText("");
						dialog.dismiss();
					}
				};
				builder.setNegativeButton(R.string.clear, clearListener);
				dialog = builder.create();
				dialog.show();
			}
		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	/**
	 * 日期时间选择控件
	 * 
	 * @param act
	 * @param calendar
	 * @param btnDate
	 */
	public static void DateTimeChoice(Activity act, TextView tvHide,
			Button btnDate, String titleStr, Boolean canBlank) {
		if (tvHide.getText().equals("")) {
			cal = Calendar.getInstance();
		} else {
			cal = Common.getCalendarFromString(tvHide.getText().toString());
		}
		tvHideReturn = tvHide;
		btnReturn = btnDate;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.widget_datetime_choice, null);

			TabHost myTabhost = (TabHost) layout.findViewById(R.id.tabhost);
			myTabhost.setup();
			myTabhost.addTab(myTabhost
					.newTabSpec("One")
					// make a new Tab
					.setIndicator("",
							act.getResources().getDrawable(R.drawable.date))
					.setContent(R.id.datePicker));
			myTabhost.addTab(myTabhost
					.newTabSpec("Two")
					// make a new Tab
					.setIndicator("",
							act.getResources().getDrawable(R.drawable.time))
					.setContent(R.id.timerPicker));

			builder.setView(layout);

			final WheelView hours = (WheelView) layout.findViewById(R.id.hour);
			hours.setAdapter(new NumericWheelAdapter(0, 23));
			hours.setLabel(Constant.UNIT_HOUR);
			hours.setCyclic(true);

			final WheelView mins = (WheelView) layout.findViewById(R.id.mins);
			mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
			mins.setLabel(Constant.UNIT_MINUTE);
			mins.setCyclic(true);

			// set current time
			int curHours = cal.get(Calendar.HOUR_OF_DAY);
			int curMinutes = cal.get(Calendar.MINUTE);

			hours.setCurrentItem(curHours);
			mins.setCurrentItem(curMinutes);

			String years[] = new String[60];
			for (int i = 0; i < 60; i++) {
				years[i] = String.valueOf(cal.get(Calendar.YEAR) - 30 + i);
			}
			final WheelView year = (WheelView) layout.findViewById(R.id.year1);
			year.setAdapter(new ArrayWheelAdapter<String>(years));
			year.setLabel(Constant.UNIT_YEAR);

			final WheelView month = (WheelView) layout
					.findViewById(R.id.month1);
			month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
			month.setLabel(Constant.UNIT_MONTH);
			month.setCyclic(true);

			final WheelView date = (WheelView) layout.findViewById(R.id.date1);
			date.setAdapter(new NumericWheelAdapter(1, cal
					.getActualMaximum(Calendar.DATE), "%02d"));
			date.setCyclic(true);

			DisplayMetrics dm = new DisplayMetrics();
			dm = act.getResources().getDisplayMetrics();
			Log.i("dm.heightPixels", dm.heightPixels + "--------------");
			if (dm.heightPixels == 320 || dm.heightPixels == 640) {
				hours.setVisibleItems(5);
				mins.setVisibleItems(5);
				year.setVisibleItems(5);
				month.setVisibleItems(5);
				date.setVisibleItems(5);
			}
			// set current time
			int yearValue = cal.get(Calendar.YEAR);
			int monthValue = cal.get(Calendar.MONTH);
			int dateValue = cal.get(Calendar.DATE);

			year.setCurrentItem(yearValue - cal.get(Calendar.YEAR) + 30);
			month.setCurrentItem(monthValue);
			date.setCurrentItem(dateValue - 1);
			date.setLabel(Constant.UNIT_DAY + "|"
					+ Common.getWeek(cal.get(Calendar.DAY_OF_WEEK)));

			month.addChangingListener(new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					Calendar calTemp = Calendar.getInstance();
					calTemp.set(year.getCurrentItem() + cal.get(Calendar.YEAR)
							- 30, month.getCurrentItem(),
							date.getCurrentItem() + 1);
					if (calTemp.get(Calendar.MONTH) != month.getCurrentItem()) {
						calTemp.add(Calendar.MONTH, -1);
						calTemp = Common.getLastDayOfMonth(calTemp);
						date.setAdapter(new NumericWheelAdapter(1, calTemp
								.getActualMaximum(Calendar.DATE), "%02d"));
						date.setCurrentItem(calTemp
								.getActualMaximum(Calendar.DATE) - 1);
					} else {
						date.setAdapter(new NumericWheelAdapter(1, calTemp
								.getActualMaximum(Calendar.DATE), "%02d"));
					}
					date.setLabel(Constant.UNIT_DAY + "|"
							+ Common.getWeek(calTemp.get(Calendar.DAY_OF_WEEK)));
				}
			});
			date.addChangingListener(new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					Calendar calTemp = Calendar.getInstance();
					calTemp.set(year.getCurrentItem() + cal.get(Calendar.YEAR)
							- 30, month.getCurrentItem(),
							date.getCurrentItem() + 1);
					date.setLabel(Constant.UNIT_DAY + "|"
							+ Common.getWeek(calTemp.get(Calendar.DAY_OF_WEEK)));
				}
			});

			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNeutralButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cal.set(year.getCurrentItem() + cal.get(Calendar.YEAR) - 30,
							month.getCurrentItem(), date.getCurrentItem() + 1,
							hours.getCurrentItem(), mins.getCurrentItem());
					tvHideReturn.setText(Common.getDateTime(cal));
					btnReturn.setText(cal.get(Calendar.YEAR)
							+ Constant.UNIT_YEAR
							+ (cal.get(Calendar.MONTH) + 1)
							+ Constant.UNIT_MONTH
							+ cal.get(Calendar.DAY_OF_MONTH)
							+ Constant.UNIT_DAY + " " + hours.getCurrentItem()
							+ ":" + mins.getCurrentItem());
				}
			});
			if (canBlank) {
				builder.setPositiveButton(R.string.blank,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// cal.set(year.getCurrentItem() +
								// cal.get(Calendar.YEAR) - 30,
								// month.getCurrentItem(), date.getCurrentItem()
								// + 1);
								tvHideReturn.setText("");
								btnReturn.setText("");
							}
						});
			}
			builder.create().show();

		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	public static void DateChoice(Activity act, TextView tvHide,
			Button btnDate, String titleStr, Boolean canBlank) {
		if (tvHide.getText().equals("")) {
			cal = Calendar.getInstance();
		} else {
			cal = Common.getCalendarFromString(tvHide.getText().toString());
		}
		btnReturn = btnDate;
		tvHideReturn = tvHide;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.widget_date_choice, null);

			builder.setView(layout);

			String years[] = new String[60];
			for (int i = 0; i < 60; i++) {
				years[i] = String.valueOf(cal.get(Calendar.YEAR) - 30 + i);
			}
			final WheelView year = (WheelView) layout.findViewById(R.id.year);
			year.setAdapter(new ArrayWheelAdapter<String>(years));
			year.setLabel(Constant.UNIT_YEAR);

			final WheelView month = (WheelView) layout.findViewById(R.id.month);
			month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
			month.setLabel(Constant.UNIT_MONTH);
			month.setCyclic(true);

			final WheelView date = (WheelView) layout.findViewById(R.id.date);
			date.setAdapter(new NumericWheelAdapter(1, cal
					.getActualMaximum(Calendar.DATE), "%02d"));
			date.setCyclic(true);

			// set current time
			int yearValue = cal.get(Calendar.YEAR);
			int monthValue = cal.get(Calendar.MONTH);
			int dateValue = cal.get(Calendar.DATE);

			year.setCurrentItem(yearValue - cal.get(Calendar.YEAR) + 30);
			month.setCurrentItem(monthValue);
			date.setCurrentItem(dateValue - 1);
			date.setLabel(Constant.UNIT_DAY + "|"
					+ Common.getWeek(cal.get(Calendar.DAY_OF_WEEK)));

			month.addChangingListener(new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					Calendar calTemp = Calendar.getInstance();
					calTemp.set(year.getCurrentItem() + cal.get(Calendar.YEAR)
							- 30, month.getCurrentItem(),
							date.getCurrentItem() + 1);
					if (calTemp.get(Calendar.MONTH) != month.getCurrentItem()) {
						calTemp.add(Calendar.MONTH, -1);
						calTemp = Common.getLastDayOfMonth(calTemp);
						date.setAdapter(new NumericWheelAdapter(1, calTemp
								.getActualMaximum(Calendar.DATE), "%02d"));
						date.setCurrentItem(calTemp
								.getActualMaximum(Calendar.DATE) - 1);
					} else {
						date.setAdapter(new NumericWheelAdapter(1, calTemp
								.getActualMaximum(Calendar.DATE), "%02d"));
					}
					date.setLabel(Constant.UNIT_DAY + "|"
							+ Common.getWeek(calTemp.get(Calendar.DAY_OF_WEEK)));
				}
			});
			date.addChangingListener(new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					Calendar calTemp = Calendar.getInstance();
					calTemp.set(year.getCurrentItem() + cal.get(Calendar.YEAR)
							- 30, month.getCurrentItem(),
							date.getCurrentItem() + 1);
					date.setLabel(Constant.UNIT_DAY + "|"
							+ Common.getWeek(calTemp.get(Calendar.DAY_OF_WEEK)));
				}
			});

			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNeutralButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cal.set(year.getCurrentItem() + cal.get(Calendar.YEAR) - 30,
							month.getCurrentItem(), date.getCurrentItem() + 1);
					tvHideReturn.setText(Common.getDateTime(cal));
					btnReturn.setText(cal.get(Calendar.YEAR)
							+ Constant.UNIT_YEAR
							+ (cal.get(Calendar.MONTH) + 1)
							+ Constant.UNIT_MONTH
							+ (cal.get(Calendar.DAY_OF_MONTH))
							+ Constant.UNIT_DAY + " ");

				}
			});
			if (canBlank) {
				builder.setPositiveButton(R.string.blank,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								tvHideReturn.setText("");
								btnReturn.setText("");
							}
						});
			}
			builder.create().show();

		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	public static void DateTime(Activity act, Button btnDate, String titleStr,
			Boolean canBlank) {
		btnReturn = btnDate;
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setTitle(titleStr);
			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.widget_time_choice, null);

			builder.setView(layout);

			final WheelView hours = (WheelView) layout.findViewById(R.id.hour);
			hours.setAdapter(new NumericWheelAdapter(0, 23));
			hours.setLabel(Constant.UNIT_HOUR);
			hours.setCyclic(true);
			final WheelView mins = (WheelView) layout.findViewById(R.id.mins);
			mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
			mins.setLabel(Constant.UNIT_MINUTE);
			mins.setCyclic(true);

			// set current time
			int curHours = 0;
			int curMinutes = 0;
			if (btnReturn.getText().equals("")) {
				curHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				curMinutes = Calendar.getInstance().get(Calendar.MINUTE);
			} else {
				curHours = Integer.parseInt(btnReturn.getText().toString()
						.substring(0, 1));
				curMinutes = Integer.parseInt(btnReturn.getText().toString()
						.substring(3, 4));
			}
			hours.setCurrentItem(curHours);
			mins.setCurrentItem(curMinutes);

			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNeutralButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					java.text.DateFormat ft = new java.text.SimpleDateFormat(
							"HH:mm");
					btnReturn.setText(ft.format(cal.getTime()));

				}
			});
			if (canBlank) {
				builder.setPositiveButton(R.string.blank,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								btnReturn.setText("");
							}
						});
			}
			builder.create().show();

		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// e.printStackTrace();
		}
	}

	/**
	 * 调用计算器面板
	 * 
	 * @param act
	 * @param btnCount
	 * @param parent
	 * @param forSearch
	 */
	public static void Calculator(Activity act, Button btnCount, View parent,
			final int decimal) {
		activity = act;
		btnReturn = btnCount;
		have_input = false;
		digClick = false;
		have_count = false;
		count_type = "";
		first_num = 0.0;
		calLog = new StringBuffer();
		fromCal = false;
		digClick = true;
		lastFlag = "";
		try {
			if (dialog != null && dialog.isShowing())
				return;
			CustomDialog.Builder builder = new CustomDialog.Builder(act);
			builder.setView(layout);

			LayoutInflater inflater = (LayoutInflater) act
					.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.widget_calculator, null);

			builder.setView(layout);

			dialog = builder.create();

			dialog.show();

			etTemp = (EditText) layout.findViewById(R.id.et_value);
			String valTemp = btnCount.getText().toString();
			valTemp = valTemp.replace(",", "");
			if (valTemp.equals("")) {
				etTemp.setText("");
			} else {
				if (Double.parseDouble(valTemp) != 0) {
					etTemp.setText(valTemp);
				} else
					etTemp.setText("");
			}
			Button btn_0 = (Button) layout.findViewById(R.id.btn_0);
			btn_0.setOnClickListener(digitListener);

			Button btn_1 = (Button) layout.findViewById(R.id.btn_1);
			btn_1.setOnClickListener(digitListener);

			Button btn_2 = (Button) layout.findViewById(R.id.btn_2);
			btn_2.setOnClickListener(digitListener);

			Button btn_3 = (Button) layout.findViewById(R.id.btn_3);
			btn_3.setOnClickListener(digitListener);

			Button btn_4 = (Button) layout.findViewById(R.id.btn_4);
			btn_4.setOnClickListener(digitListener);

			Button btn_5 = (Button) layout.findViewById(R.id.btn_5);
			btn_5.setOnClickListener(digitListener);

			Button btn_6 = (Button) layout.findViewById(R.id.btn_6);
			btn_6.setOnClickListener(digitListener);

			Button btn_7 = (Button) layout.findViewById(R.id.btn_7);
			btn_7.setOnClickListener(digitListener);

			Button btn_8 = (Button) layout.findViewById(R.id.btn_8);
			btn_8.setOnClickListener(digitListener);

			Button btn_9 = (Button) layout.findViewById(R.id.btn_9);
			btn_9.setOnClickListener(digitListener);

			Button btn_dot = (Button) layout.findViewById(R.id.btn_dot);
			btn_dot.setOnClickListener(digitListener);

			Button btnAdd = (Button) layout.findViewById(R.id.btn_add);
			btnAdd.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!fromCal)
						calLog.append(etTemp.getText().toString());
					Calculator();
					if (etTemp.getText().toString().length() > 0) {
						try {
							first_num = Double.parseDouble(etTemp.getText()
									.toString());

							if (digClick) {
								calLog.append(" + ");
							} else {
								if (!lastFlag.equals(""))
									if (calLog.length() > lastFlag.length())
										calLog.replace(calLog.length()
												- lastFlag.length(),
												calLog.length(), " + ");
							}
							lastFlag = " + ";
							count_type = "＋";
							have_input = false;
							digClick = false;
							have_count = false;

						} catch (Exception e) {
							Common.showMessage(activity, e.getMessage(),
									Toast.LENGTH_SHORT, R.drawable.error);
						}
					}
				}
			});

			Button btnSubtract = (Button) layout
					.findViewById(R.id.btn_subtract);
			btnSubtract.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!fromCal)
						calLog.append(etTemp.getText().toString());
					Calculator();
					if (etTemp.getText().toString().length() > 0) {
						try {
							first_num = Double.parseDouble(etTemp.getText()
									.toString());
							if (digClick)
								calLog.append(" - ");
							else {
								if (!lastFlag.equals(""))
									if (calLog.length() > lastFlag.length())
										calLog.replace(calLog.length()
												- lastFlag.length(),
												calLog.length(), " - ");
							}
							lastFlag = " - ";
							count_type = "-";
							have_input = false;
							digClick = false;
							have_count = false;

						} catch (Exception e) {
							Common.showMessage(activity, e.getMessage(),
									Toast.LENGTH_SHORT, R.drawable.error);
						}
					}
				}
			});

			Button btnMult = (Button) layout.findViewById(R.id.btn_mult);
			btnMult.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!fromCal)
						calLog.append(etTemp.getText().toString());
					Calculator();
					if (etTemp.getText().toString().length() > 0) {
						try {
							first_num = Double.parseDouble(etTemp.getText()
									.toString());
							if (digClick) {
								if (!lastFlag.equals("")) {
									String temp = calLog.toString();
									calLog = new StringBuffer();
									calLog.append("(");
									calLog.append(temp);
									calLog.append(")");
									calLog.append(" × ");
								} else {
									calLog.append(" × ");
								}
							} else {
								if (!lastFlag.equals(""))
									if (calLog.length() > lastFlag.length())
										calLog.replace(calLog.length()
												- lastFlag.length(),
												calLog.length(), " × ");
							}
							lastFlag = " × ";
							count_type = "×";
							have_input = false;
							digClick = false;
							have_count = false;

						} catch (Exception e) {
							Common.showMessage(activity, e.getMessage(),
									Toast.LENGTH_SHORT, R.drawable.error);
						}
					}
				}
			});

			Button btnDiv = (Button) layout.findViewById(R.id.btn_div);
			btnDiv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!fromCal)
						calLog.append(etTemp.getText().toString());
					Calculator();
					if (etTemp.getText().toString().length() > 0) {
						try {
							first_num = Double.parseDouble(etTemp.getText()
									.toString());

							if (digClick) {
								if (!lastFlag.equals("")) {
									String temp = calLog.toString();
									calLog = new StringBuffer();
									calLog.append("(");
									calLog.append(temp);
									calLog.append(")");
									calLog.append(" ÷ ");
								} else {
									calLog.append(" ÷ ");
								}
							} else {
								if (!lastFlag.equals(""))
									if (calLog.length() > lastFlag.length())
										calLog.replace(calLog.length()
												- lastFlag.length(),
												calLog.length(), " ÷ ");
							}
							lastFlag = " ÷ ";
							count_type = "÷";
							digClick = false;
							have_input = false;
							have_count = false;

						} catch (Exception e) {
							Common.showMessage(activity, e.getMessage(),
									Toast.LENGTH_SHORT, R.drawable.error);
						}
					}
				}
			});

			Button btnNegative = (Button) layout
					.findViewById(R.id.btn_negative);
			btnNegative.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (calLog.length() > 0) {
						String temp = calLog.toString();
						calLog = new StringBuffer();
						calLog.append(" - (");
						calLog.append(temp);
						calLog.append(" )");
					}

					Calculator();
					if (etTemp.getText().toString().length() > 0) {
						if (etTemp.getText().toString().contains("-")) {
							etTemp.setText(etTemp.getText().toString()
									.replace("-", ""));
						} else {
							etTemp.getText().insert(0, "-");
						}
					}
				}
			});

			Button btnResult = (Button) layout.findViewById(R.id.btn_result);
			btnResult.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!fromCal)
						calLog.append(etTemp.getText().toString());
					Calculator();
				}
			});

			Button btnClear = (Button) layout.findViewById(R.id.btn_clear);
			btnClear.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					etTemp.setText("");
					first_num = 0.0;
					// ----------------------
					fromCal = false;
					digClick = false;
					lastFlag = "";
					calLog = new StringBuffer();
				}
			});

			ImageButton btnBack = (ImageButton) layout
					.findViewById(R.id.btn_back);
			btnBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (fromCal) {
						calLog = new StringBuffer();
						fromCal = false;
					}
					if (etTemp.getText().length() > 0) {
						etTemp.getText().delete(etTemp.getText().length() - 1,
								etTemp.getText().length());
					}
				}
			});

			Button btnOk = (Button) layout.findViewById(R.id.btn_cal_ok);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Calculator();
					try {
						if (etTemp.getText().toString().length() > 0) {
							btnReturn.setText(Common.covertToDouble(etTemp
									.getText().toString(), decimal));
						} else {

							btnReturn.setText("");

						}
						// calculatorWindow.dismiss();
						dialog.dismiss();
						// if (!forSearch) {
						// Common.setNormal(activity);
						// }
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			});

			Button btnCancel = (Button) layout
					.findViewById(R.id.btn_cal_cancel);
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					// calculatorWindow.dismiss();
					// if (!forSearch) {
					// Common.setNormal(activity);
					// }
				}
			});

			ImageButton btnLog = (ImageButton) layout
					.findViewById(R.id.btn_log);
			btnLog.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (digClick) {
						if (!fromCal) {
							calLog.append(etTemp.getText().toString());
						}
						Calculator();
					}
					CustomDialog.Builder builder = new CustomDialog.Builder(
							activity);
					builder.setTitle(R.string.calculator_step);
					builder.setMessage(calLog.toString());
					builder.setIcon(R.drawable.alert_information);

					builder.setNegativeButton(R.string.close,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.setPositiveButton(R.string.copy,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ClipboardManager clip = (ClipboardManager) activity
											.getSystemService(Context.CLIPBOARD_SERVICE);
									clip.setText(calLog.toString());
									Common.showMessage(
											activity,
											activity.getResources()
													.getString(
															R.string.calculator_copy_sucess),
											Toast.LENGTH_SHORT,
											R.drawable.success);
									dialog.dismiss();
								}
							});
					builder.create().show();
				}
			});

			// layout.setOnKeyListener(new View.OnKeyListener() {
			//
			// @Override
			// public boolean onKey(View v, int keyCode, KeyEvent event) {
			// // TODO Auto-generated method stub
			// if (keyCode == KeyEvent.KEYCODE_BACK) {
			//
			// Toast.makeText(v.getContext(), "11", Toast.LENGTH_SHORT)
			// .show();
			// dialog.dismiss();
			// return false;
			// } else {
			// Toast.makeText(v.getContext(), "22", Toast.LENGTH_SHORT)
			// .show();
			//
			// return false;
			// }
			// }
			// });
		} catch (Exception e) {
			Common.showMessage(act, e.getMessage(), Toast.LENGTH_SHORT,
					R.drawable.error);
			// calculatorWindow.dismiss();
			// Common.setNormal(activity);
			dialog.dismiss();
			// e.printStackTrace();
		}
	}

	// -begin-计算器计算过程-------------------------------------------------------------------

	private static void Calculator() {
		try {
			String temp = etTemp.getText().toString();
			if (temp.length() > 0) {
				Double val = 0.0;
				if (have_input && !have_count) // 判断已经錄入第二参数
				{
					if (count_type.equals("＋")) {
						val = first_num + Double.parseDouble(temp);
						etTemp.setText(val.toString());
					}
					if (count_type.equals("-")) {
						val = first_num - Double.parseDouble(temp);
						etTemp.setText(val.toString());
					}
					if (count_type.equals("×")) {
						val = first_num * Double.parseDouble(temp);
						etTemp.setText(val.toString());
					}
					if (count_type.equals("÷")) { // 除法有效位4位。
						val = first_num / Double.parseDouble(temp);

						etTemp.setText(Common.covertToCash(val));
					}
					have_count = true;
					fromCal = true;
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private static View.OnClickListener digitListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			try {
				String val = getButtonText(arg0.getId());
				digClick = true;
				if ((!count_type.equals("")) && (have_input == false)) {
					etTemp.setText("");
					have_input = true;
				}
				// 如果已经錄入了“.”，則不允許再添加“.”
				if (etTemp.getText().toString().contains(".")) {
					if (val.equals(".")) {
						return;
					}
				} else {
					// 如果錄入的第一個值為“.”
					if ((etTemp.getText().toString().length() == 0)
							&& (val.equals("."))) {
						return;
					}
					// 如果第一個為“0”，接下来錄入的不是“.”，則錄入值直接替代“0”
					if ((etTemp.getText().equals("0")) && (!val.equals("."))) {
						etTemp.setText(val);
						return;
					}
				}
				etTemp.getText().append(val);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	};

	private static String getButtonText(int buttonId) {
		String val = "";
		fromCal = false;
		switch (buttonId) {
		case R.id.btn_0:
			val = "0";
			break;
		case R.id.btn_1:
			val = "1";
			break;
		case R.id.btn_2:
			val = "2";
			break;
		case R.id.btn_3:
			val = "3";
			break;
		case R.id.btn_4:
			val = "4";
			break;
		case R.id.btn_5:
			val = "5";
			break;
		case R.id.btn_6:
			val = "6";
			break;
		case R.id.btn_7:
			val = "7";
			break;
		case R.id.btn_8:
			val = "8";
			break;
		case R.id.btn_9:
			val = "9";
			break;
		case R.id.btn_dot:
			val = ".";
			break;
		}
		return val;
	}
	// -end-计算器计算过程-------------------------------------------------------------------

}
