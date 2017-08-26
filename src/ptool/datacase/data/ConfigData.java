package ptool.datacase.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.R;
import ptool.datacase.util.Constant;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class ConfigData {
	/**
	 * 获得账户可选图标
	 * 
	 * @param fileList
	 */
	public static void GetIcon(List<Map<String, Object>> fileList,
			Context context, String path) {
		fileList.clear();
		File dir = new File(path);
		if ((dir.exists()) && (dir.listFiles().length > 0)) {
			List<File> list = new ArrayList<File>();

			File[] arrayOfFile = dir.listFiles();
			for (int i = 0; i < arrayOfFile.length; i++) {
				// if (arrayOfFile[i].getName().contains(".png")) {
				list.add(arrayOfFile[i]);
				// }
			}
			Collections.sort(list, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					return (object1.getName()).compareTo(object2.getName());
				}
			});
			for (int i = list.size() - 1; i >= 0; i--) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				Drawable d = Drawable.createFromPath(path
						+ list.get(i).getName());
				map.put("ListItemImage", d);
				map.put("ListItemTitle", list.get(i).getName());
				// map.put("ListItemId", list.get(i).getName());
				fileList.add(map);
			}
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ListItemImage",
				context.getResources().getDrawable(R.drawable.no));// 图像资源的ID
		map.put("ListItemTitle", Constant.FLAG_EMPTY);
		// map.put("ListItemId", "");
		fileList.add(0, map);
	}
	
	public static void CharTypeData(List<Map<String, Object>> listItem) {
		HashMap<String, Object> map = new HashMap<String, Object>();
 
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "数字");
		map.put("ListItemId", "1");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "单行普通文本");
		map.put("ListItemId", "2");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "多行文本");
		map.put("ListItemId", "3");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "带掩码文本");
		map.put("ListItemId", "4");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "有值集文本");
		map.put("ListItemId", "5");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "日期");
		map.put("ListItemId", "6");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "时间");
		map.put("ListItemId", "7");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "日期时间");
		map.put("ListItemId", "8");
		listItem.add(map);
		
		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "计算类型");
		map.put("ListItemId", "9");
		listItem.add(map);
	}
	
	public static void CountTypeData(List<Map<String, Object>> listItem) {
		HashMap<String, Object> map = new HashMap<String, Object>();
 
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "总和");
		map.put("ListItemId", "0");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "平均");
		map.put("ListItemId", "1");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "最大");
		map.put("ListItemId", "3");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "最小");
		map.put("ListItemId", "4");
		listItem.add(map);
	}
	
	public static void MathTypeData(List<Map<String, Object>> listItem) {
		HashMap<String, Object> map = new HashMap<String, Object>();
 
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "+");
		map.put("ListItemId", "0");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "-");
		map.put("ListItemId", "1");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "×");
		map.put("ListItemId", "2");
		listItem.add(map);

		map = new HashMap<String, Object>();
		map.put("ListItemImage", R.drawable.star);// 图像资源的ID
		map.put("ListItemTitle", "÷");
		map.put("ListItemId", "3");
		listItem.add(map);
	}
}
