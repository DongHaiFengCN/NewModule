package doaing.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import doaing.test.R;

/**
 * 项目名称：NewModule_2
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/3/16 14:18
 * 修改人：donghaifeng
 * 修改时间：2018/3/16 14:18
 * 修改备注：
 */

public class BestSellersAdapter extends BaseAdapter {


    private List<Map.Entry<String, Float>> mapList;
    private Context context;

    public BestSellersAdapter(Context context, List<Map.Entry<String, Float>> mapList) {

        if (context instanceof BestSellersActivity) {

            this.context = context;
        }
        this.mapList = mapList;

    }


    @Override
    public int getCount() {
        return mapList == null ? 0 : mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listContainer;
        listContainer = LayoutInflater.from(context);
        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            convertView = listContainer.inflate(R.layout.bestsellers_rank_listview_item, null);
            listItemView.rankTv = convertView.findViewById(R.id.bestsellers_rank_tv);
            listItemView.nameTv = convertView.findViewById(R.id.bestsellers_name_tv);
            listItemView.amountTv = convertView.findViewById(R.id.bestsellers_amount_tv);


            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        listItemView.rankTv.setText(String.valueOf(position+1));
        listItemView.nameTv.setText(mapList.get(position).getKey());
        listItemView.amountTv.setText(String.valueOf(mapList.get(position).getValue()));

        return convertView;
    }

    public void setMapList(List<Map.Entry<String, Float>> mapList) {
        this.mapList = mapList;
        notifyDataSetChanged();
    }

    class ListItemView {

        TextView rankTv;
        TextView nameTv;
        TextView amountTv;


    }
}
