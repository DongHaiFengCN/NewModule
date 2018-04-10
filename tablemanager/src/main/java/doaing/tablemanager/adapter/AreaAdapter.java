package doaing.tablemanager.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.List;

import doaing.mylibrary.MyApplication;
import doaing.tablemanager.R;
import doaing.tablemanager.TableManagerActivity;
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
 */

public class AreaAdapter extends BaseAdapter {

    public List<String> getAreaId() {
        return areaId;
    }

    private List<String> areaId;
    private int mSelect = 0; //选中项
    private EditText editText;
    private TableManagerActivity context;
    private Database database;

    public AreaAdapter(TableManagerActivity context, Database database) {

        this.context = context;
        this.database = database;
        areaId = areaQuery();
    }

    @Override
    public int getCount() {
        return areaId.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return areaId.size() == 0 ? null : areaId.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater listContainerLeft;
        listContainerLeft = LayoutInflater.from(context);
        ListItemView listItemView = null;
        if (view == null) {
            listItemView = new ListItemView();
            view = listContainerLeft.inflate(R.layout.area_listview_item, null);
            listItemView.tv_title = view.findViewById(R.id.title);
            listItemView.imageView = view.findViewById(R.id.imageView);
            listItemView.edit_im = view.findViewById(R.id.edit_im);
            listItemView.add_im = view.findViewById(R.id.area_add_im);
            listItemView.list_area_layout = view.findViewById(R.id.list_area_layout);

            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }
        if (i < areaId.size()) {
            listItemView.add_im.setVisibility(View.GONE);
            listItemView.tv_title.setVisibility(View.VISIBLE);
            if (mSelect == i) {
                listItemView.list_area_layout.setBackgroundResource(R.drawable.tableclick);  //选中项背景
                listItemView.edit_im.setVisibility(View.VISIBLE);
                listItemView.tv_title.setTextColor(context.getResources().getColor(R.color.white));

            } else {
                listItemView.list_area_layout.setBackgroundResource(R.drawable.tablenoclick);  //其他项背景
                listItemView.edit_im.setVisibility(View.INVISIBLE);
                listItemView.tv_title.setTextColor(context.getResources().getColor(R.color.md_black_1000));
            }
            listItemView.edit_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("房间编辑").setView(getLinearLayOfEditView(i))
                            .setPositiveButton("确认修改房间名称", null).setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNeutralButton("删除房间", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    List<Document> doc = CDBHelper.getDocmentsByWhere(
                                            Expression.parameter("className").equalTo(Expression.string("Table")
                                            .add(Expression.property("areaId").equalTo(Expression.string(areaId.get(i))))),
                                            null);
                                    int count = doc.size();
                                    if (count != 0){
                                        Toast.makeText(context,"区域下有桌位,不可删除。",Toast.LENGTH_SHORT).show();
                                    }else{
                                        try {
                                            database.delete(database.getDocument(areaId.get(i)));
                                        } catch (CouchbaseLiteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    updata();
                                    context.setAreaListViewItemPosition(0);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ("".equals(editText.getText().toString())) {
                                editText.setError("不能为空！");
                            } else {
                                //修改房间名字
                                MutableDocument document = database.getDocument(areaId.get(i)).toMutable();

                                document.setString("name", editText.getText().toString());

                                try {
                                    database.save(document);

                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }
                                updata();
                                alertDialog.dismiss();
                            }
                        }
                    });

                }
            });
            listItemView.tv_title.setText(database.getDocument(areaId.get(i)).getString("name"));
        } else {
            listItemView.tv_title.setVisibility(View.GONE);
            listItemView.edit_im.setVisibility(View.GONE);
            listItemView.add_im.setVisibility(View.VISIBLE);
            listItemView.list_area_layout.setBackgroundResource(R.drawable.tablenoclick);  //其他项背景
            final ListItemView finalListItemView = listItemView;
            listItemView.add_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("添加房间")
                            .setView(getLinearLayOfEditView(-1))
                            .setPositiveButton("确定", null).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("".equals(editText.getText().toString())) {
                                editText.setError("不能为空！");
                            } else {
                                finalListItemView.list_area_layout.setBackgroundResource(R.drawable.tablenoclick);
                                MutableDocument document = new MutableDocument("Area." + ToolUtil.getUUID());
                                document.setString("channelId", ((MyApplication) context.getApplicationContext()).getCompany_ID());
                                MyLog.e("AreaAdapter","channeldId="+((MyApplication) context.getApplicationContext()).getCompany_ID());
                                document.setString("className", "Area");
                                document.setString("name", editText.getText().toString());
                                document.setBoolean("isValid", true);
                                document.setString("num", String.valueOf(areaId == null ? 0 : areaId.size()));
                                document.setString("dataType", "BaseData");
                                try {
                                    database.save(document);
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }
                                updata();
                                alertDialog.dismiss();
                                context.setAreaListViewItemPosition(i);

                            }

                        }
                    });
                }
            });
        }


        return view;

    }

    public void changeSelected(int positon) { //刷新方法
        mSelect = positon;
        notifyDataSetChanged();
    }

    public void updata(){
        areaId = areaQuery();
        notifyDataSetChanged();
    }
    class ListItemView {

        TextView tv_title;
        ImageView imageView;
        ImageView edit_im;
        ImageView add_im;
        LinearLayout list_area_layout;
    }

    public LinearLayout getLinearLayOfEditView(int pos) {
        editText = new EditText(context);
        int left, top, right, bottom;
        left = top = right = bottom = 20;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        editText.setLayoutParams(params);
        editText.setHint("请输入新名字");
        if (pos > -1){
            editText.setText(database.getDocument(areaId.get(pos)).getString("areaName"));
        }
        LinearLayout ll = new LinearLayout(context); // + 增加行
        ll.setOrientation(LinearLayout.VERTICAL); // + 增加行
        editText.setBackground(context.getDrawable(R.drawable.shape_eidt_selector));
        ll.addView(editText); // + 增加行
        return ll;
    }

    private List<String> areaQuery() {
        ResultSet results = null;
        final List<String> areaIdList;
        //动态监听DisheKind信息
        Query query = listsLiveQuery();
        areaIdList = new ArrayList<>();
        try {
             results = query.execute();

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Result row ;
        while ((row = results.next()) != null) {

            String id = row.getString(0);
            areaIdList.add(id);

        }
        return areaIdList;
    }

    private Query listsLiveQuery() {
        return QueryBuilder.select(SelectResult.expression(Meta.id)
                , SelectResult.expression(Expression.property("name")))
                .from(DataSource.database(database))
                .where(Expression.property("className").equalTo(Expression.string("Area")))
                .orderBy(Ordering.property("num").ascending());
    }

}

