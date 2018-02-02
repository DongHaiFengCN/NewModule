package doaing.dishesmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.List;

import doaing.dishesmanager.DishesActivity;
import doaing.dishesmanager.R;

/**
 * 项目名称：new
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/31 13:41
 * 修改人：donghaifeng
 * 修改时间：2018/1/31 13:41
 * 修改备注：
 */

public class DishesKindAdapter extends BaseAdapter {

    private int mSelect = 0; //选中项

    private List<Document> names;
    private Context context;

    public DishesKindAdapter(List<Document> names, Context context) {

        this.names = names;
        this.context = context;
    }

    @Override
    public int getCount() {
        return names == null ? 0 : names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater listContainerLeft;
        listContainerLeft = LayoutInflater.from(context);
        ListItemView listItemView = null;
        if (view == null) {
            listItemView = new ListItemView();
            view = listContainerLeft.inflate(R.layout.disheskind_list_item, null);
            listItemView.tv_title = view.findViewById(R.id.title);
            listItemView.imageView = view.findViewById(R.id.imageView);
            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }
        if (mSelect == i) {
            view.setBackgroundResource(R.color.md_grey_50);  //选中项背景
            listItemView.imageView.setVisibility(View.VISIBLE);
        } else {
            view.setBackgroundResource(R.color.md_grey_100);  //其他项背景
            listItemView.imageView.setVisibility(View.INVISIBLE);
        }

        listItemView.tv_title.setText(names.get(i).getString("kindName"));

        return view;

    }

    public void changeSelected(int positon) { //刷新方法
        mSelect = positon;
        notifyDataSetChanged();
    }

    class ListItemView {

        TextView tv_title;
        ImageView imageView;
    }

}

