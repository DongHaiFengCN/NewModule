package doaing.dishesmanager.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.List;

import bean.kitchenmanage.dishes.DishesC;
import doaing.dishesmanager.R;

/**
 * Created by lenovo on 2018/3/23.
 */

public class ListAdapter extends BaseAdapter {

    private List<Document> dishesList;
    private Activity activity;

    public ListAdapter(Activity activity,List<Document> dishesList){
        this.activity = activity;
        this.dishesList = dishesList;
    }
    @Override
    public int getCount() {
        return dishesList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dishesList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {

        ViewHolder viewHolder;
        if (convertView ==null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.slip_list_item_guige,null);
            viewHolder = new ViewHolder();
            viewHolder.item_dishes = convertView.findViewById(R.id.guige_item_dishes);
            viewHolder.item_dishesSup = convertView.findViewById(R.id.guige_item_dishesSup);
            viewHolder.item_dishesSupCount = convertView.findViewById(R.id.guige_item_dishesSupCount);
            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.item_dishesSupCount.setText(dishesList.get(position).getInt("supCount")+"");
        viewHolder.item_dishesSup.setText(dishesList.get(position).getString("supDishesName"));
        viewHolder.item_dishes.setText(dishesList.get(position).getString("dishesName"));
        return convertView;
    }

    class ViewHolder{

        TextView item_dishes,item_dishesSup,item_dishesSupCount;
    }
}
