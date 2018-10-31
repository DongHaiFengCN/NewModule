package doaing.dishesmanager.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import doaing.dishesmanager.DishesActivity;
import doaing.dishesmanager.R;
import doaing.mylibrary.MyApplication;
import tools.CDBHelper;
import tools.MyLog;
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

public class DishesKindAdapter extends BaseAdapter {
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

    public DishesKindAdapter(Context context, Database database) {

        this.database = database;
        this.context = context;
        disheKindQuery();
    }


    @Override
    public int getCount() {
        return names.size() == 0 ? 1 : names.size() + 1;
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

            view = listContainerLeft.inflate(R.layout.disheskind_list_item, null);
            listItemView.linearLayout = view.findViewById(R.id.list_item_ll);
            listItemView.tvTitle = view.findViewById(R.id.title);
            listItemView.kindView = view.findViewById(R.id.kind_add_im);
            listItemView.kindEdit = view.findViewById(R.id.edit_im);
            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }

        if (mSelect == i) {
            //选中项背景
            listItemView.linearLayout.setBackgroundResource(R.drawable.animtableclick);
            listItemView.tvTitle.setTextColor(context.getResources().getColor(R.color.white));
            if (flag) {

                listItemView.kindEdit.setVisibility(View.VISIBLE);
                listItemView.kindEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        View view = LayoutInflater.from(context).inflate(R.layout.dishekind_add_dialog_item, null);

                        final EditText kindNameEt = view.findViewById(R.id.kindname_et);

                        kindNameEt.setText(database.getDocument(names.get(i)).getString("name"));

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(view);
                        builder.setTitle("菜类编辑");
                        builder.setPositiveButton("确定", null);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNeutralButton("删除菜类", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<Document> list = CDBHelper.getDocmentsByWhere(
                                        Expression.property("className").equalTo(Expression.string("Dish"))
                                                .and(Expression.property("kindId").equalTo(Expression.string(names.get(i))))
                                        , null);
                                if (list.size() > 0) {
                                    Toast.makeText(context, "先移除菜类下的菜品！", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Document document = database.getDocument(names.get(i));
                                try {
                                    database.delete(document);
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }

                                disheKindQuery();
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(0);


                            }
                        });

                        final AlertDialog alertDialog = builder.show();

                        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (kindNameEt.getText().length() > 0) {
                                    MutableDocument document = database.getDocument(names.get(i)).toMutable();
                                    document.setString("name", kindNameEt.getText().toString());
                                    try {
                                        database.save(document);
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    }
                                    notifyDataSetChanged();

                                    alertDialog.dismiss();


                                } else {

                                    kindNameEt.setError("空值");
                                }

                            }
                        });
                    }
                });
            }

        } else {
            //其他项背景
            listItemView.linearLayout.setBackgroundResource(R.drawable.animtablenoclick);
            listItemView.tvTitle.setTextColor(context.getResources().getColor(R.color.md_black_1000));
            listItemView.kindEdit.setVisibility(View.INVISIBLE);
        }
        if (names == null || i == names.size()) {

            listItemView.kindView.setVisibility(View.VISIBLE);
            listItemView.tvTitle.setVisibility(View.GONE);
            listItemView.kindEdit.setVisibility(View.GONE);
            listItemView.kindView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    View view = LayoutInflater.from(context).inflate(R.layout.dishekind_add_dialog_item, null);

                    final EditText kindNameEt = view.findViewById(R.id.kindname_et);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(view);
                    builder.setTitle("菜类添加");
                    builder.setPositiveButton("确定", null);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    final AlertDialog alertDialog = builder.show();

                    alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (kindNameEt.getText().length() > 0) {
                                MutableDocument document = new MutableDocument("DishesKind." + ToolUtil.getUUID());
                                document.setString("channelId", ((MyApplication) context.getApplicationContext()).getCompany_ID());
                                document.setString("className", "DishesKind");
                                document.setString("name", kindNameEt.getText().toString());
                                try {
                                    database.save(document);
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }

                                disheKindQuery();
                                notifyDataSetChanged();
                                alertDialog.dismiss();
                                EventBus.getDefault().postSticky(names.size() - 1);


                            } else {

                                kindNameEt.setError("空值");
                            }


                        }
                    });


                }
            });

        } else {

            listItemView.kindView.setVisibility(View.GONE);
            listItemView.tvTitle.setVisibility(View.VISIBLE);
            listItemView.tvTitle.setText(database.getDocument(names.get(i)).getString("name"));

        }

        return view;

    }

    public void changeSelected(int positon) { //刷新方法
        mSelect = positon;
        notifyDataSetChanged();
    }

    class ListItemView {

        TextView tvTitle;

        LinearLayout linearLayout;
        ImageView kindView;
        ImageView kindEdit;
    }

    private void disheKindQuery() {

        if (names != null) {
            names.clear();
        }
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

