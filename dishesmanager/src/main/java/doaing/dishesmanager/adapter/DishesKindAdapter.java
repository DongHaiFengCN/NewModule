package doaing.dishesmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
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
 *
 * @author donghaifeng
 */

public class DishesKindAdapter extends BaseAdapter {
    /**
     * 选中的item位置
     */
    private int mSelect = 0;

    public List<Document> getNames() {
        return names;
    }

    private List<Document> names = new ArrayList<>();
    private Context context;
    private Database database;

    public DishesKindAdapter(Context context, Database database) {

        this.database = database;
        this.context = context;
        disheKindQuery();
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater listContainerLeft;
        listContainerLeft = LayoutInflater.from(context);
        ListItemView listItemView;
        if (view == null) {
            listItemView = new ListItemView();
            view = listContainerLeft.inflate(R.layout.disheskind_list_item, null);
            listItemView.tvTitle = view.findViewById(R.id.title);
            listItemView.imageView = view.findViewById(R.id.imageView);
            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }
        if (mSelect == i) {
            //选中项背景
            view.setBackgroundResource(R.color.md_grey_50);
            listItemView.imageView.setVisibility(View.VISIBLE);
        } else {
            //其他项背景
            view.setBackgroundResource(R.color.md_grey_100);
            listItemView.imageView.setVisibility(View.INVISIBLE);
        }

        listItemView.tvTitle.setText(names.get(i).getString("kindName"));

        return view;

    }

    public void changeSelected(int positon) { //刷新方法
        mSelect = positon;
        notifyDataSetChanged();
    }

    class ListItemView {

        TextView tvTitle;
        ImageView imageView;
    }

    private void disheKindQuery() {
        //动态监听DishesKind信息
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(this.database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKindC"))
                        .and(Expression.property("setMenu").equalTo(Expression.booleanValue(false))));

        try {
            ResultSet rows = query.execute();
            Result row;
            while ((row = rows.next()) != null) {
                String id = row.getString(0);
                Document doc = this.database.getDocument(id);
                names.add(doc);
            }


        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }
}

