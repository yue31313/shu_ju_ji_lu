package ptool.datacase.util.control;

import java.util.List;
import java.util.Map;

import ptool.datacase.R;
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

public class ValueListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> listItems; // 信息集合
	private LayoutInflater listContainer; // 视图容器
	private Handler mHandler;
	private Activity act;
	
	public final class ListItemView { // 自定义控件集合
		public LinearLayout layImage;
		public TextView title;
	}
	
	public ValueListViewAdapter(Context context,
			List<Map<String, Object>> listItems, Handler handler) {
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		this.mHandler = handler;
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
			convertView = listContainer.inflate(R.layout.value_list_item, null);
			listItemView.title = (TextView) convertView
					.findViewById(R.id.ListItemTitle);
			listItemView.layImage = (LinearLayout) convertView
					.findViewById(R.id.LayImage);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
	 		listItemView.title.setText( (String) listItems.get(position).get("ListItemTitle"));
	 		
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
