package ptool.datacase.util.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptool.datacase.R;
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

public class GridImageAdapter extends BaseAdapter{
	private Activity act;
	private SystemConfig app;
	private LayoutParams para;
    private LayoutInflater lif;
    private List<Map<String, Object>>  list = new ArrayList<Map<String, Object>>();
    public GridImageAdapter(Context c, List<Map<String, Object>> listItem){
    	this.act = (Activity) c;
		app = (SystemConfig) act.getApplication();
		para = null;
        lif = LayoutInflater.from(c);
        list = listItem;
    }
    
    public int getCount() {
        return list.size();
    }

    public Map<String, Object> getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /**
     * @param startPosition
     * @param endPosition
     */
    public void exchange(int startPosition, int endPosition){
        //比较一下 使startPosition永远小于endPosition的值 解决问题1 ，2
    	if(startPosition == 0 || endPosition == 0){
    		return;
    	}
        if(startPosition > endPosition){
            int temp = endPosition;
            endPosition = startPosition;
            startPosition = temp;
        }
        Map<String, Object> endObject = getItem(endPosition);
        Map<String, Object> startObject = getItem(startPosition);
        System.out.println(startPosition + "========"+endPosition);
        HashMap<String, Object> endMap = new HashMap<String, Object>();
        endMap.put("ListItemImage", endObject.get("ListItemImage"));
        endMap.put("ListItemTitle",  endObject.get("ListItemTitle"));
        endMap.put("ListItemId", endObject.get("ListItemId"));
		 
        list.set(startPosition,endMap);
        
        HashMap<String, Object> startMap = new HashMap<String, Object>();
        startMap.put("ListItemImage", startObject.get("ListItemImage"));
        startMap.put("ListItemTitle", startObject.get("ListItemTitle"));
        startMap.put("ListItemId", startObject.get("ListItemId"));
        
        list.set(endPosition,startMap);
        notifyDataSetChanged();
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = lif.inflate(R.layout.main_grid_item, null);
        }
            
        try {
            TextView  textView=(TextView)convertView.findViewById(R.id.ItemText);
            textView.setText(list.get(position).get("ListItemTitle").toString());
            ImageView  imgView=(ImageView)convertView.findViewById(R.id.ItemImage);
        	if (para == null) {
				para = imgView.getLayoutParams();
				para.height = (int) (56 * app.getDensity());
				para.width = (int) (56 * app.getDensity());
			}
        	imgView.setLayoutParams(para);
            imgView.setImageDrawable((Drawable) list.get(
					position).get("ListItemImage"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

}
