package doaing.order.device.kitchen;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import doaing.order.R;
import tools.CDBHelper;

/*
*
 * Created by lenovo on 2018/2/1.
*/



public class AddKitchenAdapter extends BaseAdapter {

    private Activity activity;
    private List<String> list;
    public static Map<Integer, Boolean> isSelected;

    public AddKitchenAdapter(Activity a, List<String> l) {
        activity = a;
        list = l;
        isSelected = new HashMap<>();
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_addkitchen, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Document doc = CDBHelper.getDocByID( list.get(position));
        // 设置list中TextView的显示
        viewHolder.itemAddkitchenName.setText("" + doc.getString("name"));
        // 根据isSelected来设置checkbox的选中状况
        viewHolder.itemAddkitchenCb.setChecked(getIsSelected().get(position));
        viewHolder.itemAddkitchenRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewHolder.itemAddkitchenCb.toggle();
//                // 将CheckBox的选中状况记录下来
//                getIsSelected().put(position, viewHolder.itemAddkitchenCb.isChecked());
//                notifyDataSetChanged();
            }
        });
        viewHolder.itemAddkitchenCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSelected.put(position, true);
                } else {
                    isSelected.put(position, false);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public Map<Integer, Boolean> getIsSelected() {
        return isSelected;
    }


    class ViewHolder {
        TextView itemAddkitchenName;
        CheckBox itemAddkitchenCb;
        RelativeLayout itemAddkitchenRel;

        ViewHolder(View view) {
            itemAddkitchenName = view.findViewById(R.id.item_addkitchen_name);
            itemAddkitchenCb = view.findViewById(R.id.item_addkitchen_cb);
            itemAddkitchenRel = view.findViewById(R.id.item_addkitchen_rel);
        }
    }
}
