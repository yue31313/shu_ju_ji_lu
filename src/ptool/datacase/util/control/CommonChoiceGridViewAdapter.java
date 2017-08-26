package ptool.datacase.util.control;

import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.util.Common;
import ptool.datacase.util.SystemConfig;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CommonChoiceGridViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> listItems; // 信息集合
	private LayoutInflater listContainer; // 视图容器
	private Activity act;
	private SystemConfig app;
	private LayoutParams para;

	public final class GridItemView { // 自定义控件集合
		public ImageView image;
		public ImageView imageChecked;
		public RelativeLayout layoutBack;
	}

	public CommonChoiceGridViewAdapter(Context context,
			List<Map<String, Object>> listItems) {
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		this.act = (Activity) context;
		app = (SystemConfig) act.getApplication();
		para = null;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 自定义视图
		GridItemView gridItemView = null;
		if (convertView == null) {
			gridItemView = new GridItemView();
			convertView = listContainer.inflate(R.layout.grid_view_item, null);
			// 获取控件对象
			gridItemView.image = (ImageView) convertView
					.findViewById(R.id.ItemImage);
			gridItemView.imageChecked = (ImageView) convertView
					.findViewById(R.id.ItemCheck);
			gridItemView.layoutBack = (RelativeLayout) convertView
					.findViewById(R.id.layoutBack);
			// 设置控件集到convertView
			convertView.setTag(gridItemView);
		} else {
			gridItemView = (GridItemView) convertView.getTag();
		}

		// 设置文字和图片
		if (Common.isInt(listItems.get(position).get("ListItemImage")
				.toString())) {
			gridItemView.image.setBackgroundResource((Integer) listItems.get(
					position).get("ListItemImage"));
		} else {
			if (para == null) {
				para = gridItemView.image.getLayoutParams();
				para.height = (int) (46 * app.getDensity());
				para.width = (int) (46 * app.getDensity());
			}
			gridItemView.image.setLayoutParams(para);
			gridItemView.image.setImageDrawable((Drawable) listItems.get(
					position).get("ListItemImage"));
		}

		if (listItems.get(position).get("ListItemCheck") != null) {
			gridItemView.layoutBack.setBackgroundResource((Integer) listItems
					.get(position).get("ListItemCheck"));
		} else {
			gridItemView.layoutBack.setBackgroundDrawable(null);
		}
		return convertView;
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
}