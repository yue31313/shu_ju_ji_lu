package ptool.datacase.util.control;

import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.util.Common;
import ptool.datacase.util.PopupToolType;
import ptool.datacase.util.PopupWindowUtil;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> listItems; // 信息集合
	private List<Map<String, Object>> listChars;
	private LayoutInflater listContainer; // 视图容器
	private Handler mHandler;
	private String viewType;
	private Activity act;

	public final class ListItemView { // 自定义控件集合
		public TextView listItem1;
		public TextView listItem2;
		public TextView listItem3;
		public TextView listItem4;
		public LinearLayout layImage;
	}

	public DataListViewAdapter(Context context,
			List<Map<String, Object>> listItems,
			List<Map<String, Object>> listChars, Handler handler,
			String viewType) {
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		this.listChars = listChars;
		this.mHandler = handler;
		this.viewType = viewType;
		this.act = (Activity) context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 自定义视图
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			if (viewType.equals("1")) {
				convertView = listContainer.inflate(R.layout.data_list_item1,
						null);
			} else {
				convertView = listContainer.inflate(R.layout.data_list_item2,
						null);
			}
			listItemView.listItem1 = (TextView) convertView
					.findViewById(R.id.ListItem1);
			listItemView.listItem2 = (TextView) convertView
					.findViewById(R.id.ListItem2);
			listItemView.listItem3 = (TextView) convertView
					.findViewById(R.id.ListItem3);
			listItemView.listItem4 = (TextView) convertView
					.findViewById(R.id.ListItem4);
			listItemView.layImage = (LinearLayout) convertView
					.findViewById(R.id.LayImage);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		String value1 = (String) listItems.get(position).get("ListItem1");
		String value2 = (String) listItems.get(position).get("ListItem2");
		String value3 = (String) listItems.get(position).get("ListItem3");
		String value4 = (String) listItems.get(position).get("ListItem4");

		String uint1 = "";
		String uint2 = "";
		String uint3 = "";
		String uint4 = "";
		if (listChars.size() > 0) {
			if (!listChars.get(0).get("char_unit").equals("")) {
				uint1 = " " + listChars.get(0).get("char_unit").toString();
			}
		}
		if (listChars.size() > 1) {
			if (!listChars.get(1).get("char_unit").equals("")) {
				uint2 = " " + listChars.get(1).get("char_unit").toString();
			}
		}
		if (listChars.size() > 2) {
			if (!listChars.get(2).get("char_unit").equals("")) {
				uint3 = " " + listChars.get(2).get("char_unit").toString();
			}
		}
		if (listChars.size() > 3) {
			if (!listChars.get(3).get("char_unit").equals("")) {
				uint4 = " " + listChars.get(3).get("char_unit").toString();
			}
		}
		if (listChars.size() > 0) {
			if (listChars.get(0).get("char_datatype").equals("6")) {
				value1 = Common.getDateStringShort(value1);
			} else if (listChars.get(0).get("char_datatype").equals("8")) {
				value1 = Common.getDateString(value1);
			} else {
				value1 = value1 + uint1;
			}
		}
		if (listChars.size() > 1) {
			if (listChars.get(1).get("char_datatype").equals("6")) {
				value2 = Common.getDateStringShort(value2);
			} else if (listChars.get(1).get("char_datatype").equals("8")) {
				value2 = Common.getDateString(value2);
			} else {
				value2 = value2 + uint2;
			}
		}
		if (listChars.size() > 2) {
			if (listChars.get(2).get("char_datatype").equals("6")) {
				value3 = Common.getDateStringShort(value3);
			} else if (listChars.get(2).get("char_datatype").equals("8")) {
				value3 = Common.getDateString(value3);
			} else {
				value3 = value3 + uint3;
			}
		}
		if (listChars.size() > 3) {
			if (listChars.get(3).get("char_datatype").equals("6")) {
				value4 = Common.getDateStringShort(value4);
			} else if (listChars.get(3).get("char_datatype").equals("8")) {
				value4 = Common.getDateString(value4);
			} else {
				value4 = value4 + uint4;
			}
		}
		if (!viewType.equals("1")) {
			if (listChars.size() > 0) {
				value1 = listChars.get(0).get("char_name").toString() + "："
						+ value1;
			}
			if (listChars.size() > 1) {
				value2 = listChars.get(1).get("char_name").toString() + " ："
						+ value2;
			}
			if (listChars.size() > 2) {
				value3 = listChars.get(2).get("char_name").toString() + "："
						+ value3;
			}
			if (listChars.size() > 3) {
				value4 = listChars.get(3).get("char_name").toString() + "："
						+ value4;
			}
		}

		listItemView.listItem1.setText(value1);
		listItemView.listItem2.setText(value2);
		listItemView.listItem3.setText(value3);
		listItemView.listItem4.setText(value4);

		listItemView.layImage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// 判断点击行的位置
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				if (location[1] > 200) {
					PopupWindowTool dw = new PopupWindowTool(v, position,
							mHandler);
					dw.setBackgroundDrawable(act.getResources().getDrawable(
							R.drawable.popup_window_up));
					dw.showLikeQuickAction(0, 0);
				} else {
					PopupWindowTool dw = new PopupWindowTool(v, position,
							mHandler);
					dw.setBackgroundDrawable(act.getResources().getDrawable(
							R.drawable.popup_window_dowm));
					dw.showLikePopDownMenu(0, -30);
				}
			}
		});

		return convertView;
	}

	private static class PopupWindowTool extends PopupWindowUtil implements
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
			LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View root = null;
			int[] location = new int[2];
			anchor.getLocationOnScreen(location);
			if (location[1] > 200) {
				root = (View) inflater.inflate(R.layout.popup_tool_top, null);
			} else {
				root = (View) inflater
						.inflate(R.layout.popup_tool_bottom, null);
			}
			// ---------------------------------------------------------------------------
			LinearLayout btnOne = (LinearLayout) root.findViewById(R.id.btnOne);
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

			LinearLayout btnTwo = (LinearLayout) root.findViewById(R.id.btnTwo);
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

			LinearLayout btnFour = (LinearLayout) root
					.findViewById(R.id.btnFour);
			btnFour.setVisibility(View.GONE);
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

			// LinearLayout sepFour = (LinearLayout) root
			// .findViewById(R.id.sepFour);
			// sepFour.setVisibility(View.GONE);
			//
			// LinearLayout btnFive = (LinearLayout) root
			// .findViewById(R.id.btnFive);
			// btnFive.setVisibility(View.GONE);
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

			LinearLayout btnSix = (LinearLayout) root.findViewById(R.id.btnSix);
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