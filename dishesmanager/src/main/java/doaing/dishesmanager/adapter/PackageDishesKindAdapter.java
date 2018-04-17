package doaing.dishesmanager.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import doaing.dishesmanager.R;
import doaing.mylibrary.MyApplication;
import tools.ToolUtil;

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

public class PackageDishesKindAdapter extends BaseAdapter {
    /**
     * 选中的item位置
     */
    private int mSelect = 0;

    public List<String> getNames() {
        return names;
    }

    private List<String> names = new ArrayList<>();
    private Context context;
    private Database database;

    public void setFlag(boolean flag) {
        this.flag = flag;
        notifyDataSetChanged();
    }

    private boolean flag = true;

    public PackageDishesKindAdapter(Context context, Database database) {

        this.database = database;
        this.context = context;
        disheKindQuery();
    }


    @Override
    public int getCount() {
        return names.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater listContainerLeft;
        listContainerLeft = LayoutInflater.from(context);
        ListItemView listItemView;
        if (view == null) {
            listItemView = new ListItemView();

            view = listContainerLeft.inflate(R.layout.package_disheskind_list_item, null);
            listItemView.linearLayout = view.findViewById(R.id.list_item_ll);
            listItemView.tvTitle = view.findViewById(R.id.title);
            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }

        if (mSelect == i) {
            //选中项背景
            listItemView.linearLayout.setBackgroundResource(R.drawable.animtableclick);
            listItemView.tvTitle.setTextColor(context.getResources().getColor(R.color.white));


        } else {
            //其他项背景
            listItemView.linearLayout.setBackgroundResource(R.drawable.animtablenoclick);
            listItemView.tvTitle.setTextColor(context.getResources().getColor(R.color.md_black_1000));

        }
        listItemView.tvTitle.setText(database.getDocument(names.get(i)).getString("name"));
        return view;

    }

    public void changeSelected(int positon) { //刷新方法
        mSelect = positon;
        notifyDataSetChanged();
    }

    class ListItemView {

        TextView tvTitle;

        LinearLayout linearLayout;

    }

    private void disheKindQuery() {
        //动态监听DishesKind信息
        Query query = QueryBuilder.select(SelectResult.expression(Meta.id))
                .from(DataSource.database(this.database))
                .where(Expression.property("className").equalTo(Expression.string("DishesKind")));

        try {
            ResultSet rows = query.execute();
            Result row;
            while ((row = rows.next()) != null) {
                String id = row.getString(0);
                names.add(id);
            }


        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }
}

