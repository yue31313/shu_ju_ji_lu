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
import android.widget.TextView;

public class CommonChoiceListViewAdapter extends BaseAdapter {
	private List<Map<String, Object>> listItems; // ��Ϣ����
	private LayoutInflater listContainer; // ��ͼ����
	private Activity act;
	private SystemConfig app;
	private LayoutParams para;
	public final class ListItemView { // �Զ���ؼ�����
		public ImageView image;
		public TextView title;
		public ImageView imageChecked;
	}

	public CommonChoiceListViewAdapter(Context context,
			List<Map<String, Object>> listItems) {
		listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.listItems = listItems;
		this.act = (Activity) context;
		app = (SystemConfig) act.getApplication();
		para = null;
	}

	/**
	 * ListView Item����
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// �Զ�����ͼ
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(R.layout.list_item, null);
			// ��ȡ�ؼ�����
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.ListItemImage);
			listItemView.title = (TextView) convertView
					.findViewById(R.id.ListItemTitle);
			listItemView.imageChecked = (ImageView) convertView
					.findViewById(R.id.ListItemCheck);

			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
		if (Common.isInt(listItems.get(position).get("ListItemImage")
				.toString())) {
			listItemView.image.setBackgroundResource((Integer) listItems.get(
					position).get("ListItemImage"));
		} else {
			if(para == null)
			{
			para = listItemView.image.getLayoutParams();
			para.height = (int) (46 * app.getDensity());
			para.width = (int) (46 * app.getDensity());
			}
			listItemView.image.setLayoutParams(para);
			listItemView.image.setBackgroundDrawable((Drawable) listItems.get(
					position).get("ListItemImage"));
		}
		listItemView.title.setText((String) listItems.get(position).get(
				"ListItemTitle"));
		if (listItems.get(position).get("ListItemCheck") != null) {
			listItemView.imageChecked.setBackgroundResource((Integer) listItems
					.get(position).get("ListItemCheck"));
		} else {
			listItemView.imageChecked.setBackgroundDrawable(null);
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