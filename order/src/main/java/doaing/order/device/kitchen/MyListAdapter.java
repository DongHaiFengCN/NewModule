package doaing.order.device.kitchen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import doaing.order.R;
import tools.CDBHelper;

/*
*
 * Created by lenovo on 2018/2/1.

*/


public class MyListAdapter extends BaseAdapter {

    private Activity activity;
    private List<String> list;

    public MyListAdapter(Activity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_kitchen, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Document document = CDBHelper.getDocByID(activity.getApplicationContext(), list.get(position));
        viewHolder.itemKitchenNum.setText("00" + (position + 1));
        viewHolder.itemKitchenKname.setText(document.getString("name"));
        viewHolder.itemKitchenPname.setText(document.getString("kitchenAdress"));
        viewHolder.itemKitchenDelete.setOnClickListener(new OnDeleteButtonClick(position));

        viewHolder.itemKitchenBianji.setOnClickListener(new OnEditorButtonClick(position));
        return convertView;
    }

    public void refreshItem(List<String> infomations) {
        list = infomations;
    }

    private class OnDeleteButtonClick implements View.OnClickListener {

        int mPosition;

        public OnDeleteButtonClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            try {
                new AlertDialog.Builder(activity)
                        .setTitle("确定删除该厨房吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        deleteOneItemFromDatabase(mPosition);
                                        list.remove(mPosition);
                                        notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private void deleteOneItemFromDatabase(int position) {

        Document doc = CDBHelper.getDocByID(activity, list.get(position));
        CDBHelper.deleDocument(activity.getApplicationContext(), doc);

    }

    private class OnEditorButtonClick implements View.OnClickListener {

        int mPosition;

        public OnEditorButtonClick(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            Document doc=CDBHelper.getDocByID(activity,list.get(mPosition));
            Intent intent1=new Intent(activity,AddkitchenActivity.class);
            intent1.putExtra("ipcontent", ""+doc.getString("kitchenAdress"));
            intent1.putExtra("clientname", ""+doc.getString("name"));
            intent1.putExtra("docId",list.get(mPosition));
            activity.startActivityForResult(intent1, 2016);//startActivityForResult(intent1, 2016);
        }

    }

    class ViewHolder {
        TextView itemKitchenNum;
        TextView itemKitchenKname;
        TextView itemKitchenPname;
        ImageView itemKitchenBianji;
        ImageView itemKitchenDelete;

        ViewHolder(View view) {
            itemKitchenNum = view.findViewById(R.id.item_kitchen_num);
            itemKitchenKname = view.findViewById(R.id.item_kitchen_kname);
            itemKitchenPname= view.findViewById(R.id.item_kitchen_pname);
            itemKitchenBianji = view.findViewById(R.id.item_kitchen_bianji);
            itemKitchenDelete = view.findViewById(R.id.item_kitchen_delete);
        }
    }
}
