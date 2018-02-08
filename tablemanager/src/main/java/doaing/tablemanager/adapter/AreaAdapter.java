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

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.List;

import doaing.mylibrary.MyApplication;
import doaing.tablemanager.R;
import doaing.tablemanager.TableManagerActivity;
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

    private int mSelect = 0; //选中项
    private EditText editText;
    private List<Document> names;
    private TableManagerActivity context;
    private Database database;

    public AreaAdapter(List<Document> names, TableManagerActivity context, Database database) {

        this.names = names;
        this.context = context;
        this.database = database;
    }

    @Override
    public int getCount() {
        return names == null ? 1 : names.size() + 1;
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

            view.setTag(listItemView);
        } else {
            listItemView = (ListItemView) view.getTag();
        }
        if (i < names.size()) {
            listItemView.add_im.setVisibility(View.GONE);
            listItemView.tv_title.setVisibility(View.VISIBLE);
            if (mSelect == i) {
                view.setBackgroundResource(R.color.md_grey_50);  //选中项背景
                listItemView.imageView.setVisibility(View.VISIBLE);
                listItemView.edit_im.setVisibility(View.VISIBLE);

            } else {
                view.setBackgroundResource(R.color.md_grey_100);  //其他项背景
                listItemView.imageView.setVisibility(View.INVISIBLE);
                listItemView.edit_im.setVisibility(View.INVISIBLE);
            }
            listItemView.edit_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("房间编辑").setView(getLinearLayOfEditView())
                            .setPositiveButton("确认修改房间名称", null).setNegativeButton("", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNeutralButton("删除房间", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Document document = names.get(i);
                                    Array array = document.getArray("tableIDList");
                                    int count = array.count();
                                    for (int j = 0; j < count; j++) {
                                        try {
                                            database.delete(database.getDocument(array.getString(j)));
                                        } catch (CouchbaseLiteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        database.delete(document);
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    }
                                    names.remove(document);
                                    notifyDataSetChanged();
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
                                Document document = names.get(i);
                                document.setString("areaName", editText.getText().toString());

                                try {
                                    database.save(document);

                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }
                                alertDialog.dismiss();
                                notifyDataSetChanged();
                            }
                        }
                    });

                }
            });
            listItemView.tv_title.setText(names.get(i).getString("areaName"));
        } else {
            listItemView.tv_title.setVisibility(View.GONE);
            listItemView.imageView.setVisibility(View.GONE);
            listItemView.edit_im.setVisibility(View.GONE);
            listItemView.add_im.setVisibility(View.VISIBLE);
            listItemView.add_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("添加房间")
                            .setView(getLinearLayOfEditView())
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
                                Document document = new Document("AreaC." + ToolUtil.getUUID());
                                document.setString("channelId", ((MyApplication) context.getApplicationContext()).getCompany_ID());
                                document.setString("className", "AreaC");
                                document.setString("areaName", editText.getText().toString());
                                document.setBoolean("isvalid", true);
                                document.setString("areaNum", String.valueOf(names == null ? 0 : names.size()));
                                document.setArray("tableIDList", new Array());
                                try {
                                    database.save(document);
                                } catch (CouchbaseLiteException e) {
                                    e.printStackTrace();
                                }
                                names.add(document);
                                notifyDataSetChanged();
                                context.setAreaListViewItemPosition(0);
                                alertDialog.dismiss();
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

    class ListItemView {

        TextView tv_title;
        ImageView imageView;
        ImageView edit_im;
        ImageView add_im;

    }

    public LinearLayout getLinearLayOfEditView() {
        editText = new EditText(context);
        int left, top, right, bottom;
        left = top = right = bottom = 20;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        editText.setLayoutParams(params);
        editText.setHint("请输入新名字");
        LinearLayout ll = new LinearLayout(context); // + 增加行
        ll.setOrientation(LinearLayout.VERTICAL); // + 增加行
        editText.setBackground(context.getDrawable(R.drawable.shape_eidt_selector));
        ll.addView(editText); // + 增加行
        return ll;
    }

}
