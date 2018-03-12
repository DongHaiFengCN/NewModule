package doaing.order.device.kitchen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.gprinter.save.PortParamDataBase;

import java.util.List;

import bean.kitchenmanage.kitchen.KitchenClientC;
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
    PortParameters mPortParam;
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
        viewHolder.itemKitchenKname.setText(document.getString("name"));
        //viewHolder.itemKitchenPname.setText(""+document.getString("kitchenAdress"));
        Log.e("Port","---"+document.getString("kitchenAdress"));
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
//                                        //list中就一个时，不去执行转换位置
//                                        if (list.size() > 1 ){
//                                            //点击最后一个时，不去执行转换位置
//                                            if (mPosition != (list.size()-1)){
//                                                setKitchenCfg(mPosition);
//                                            }
//                                        }
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

    private void setKitchenCfg(int pos){
        KitchenClientC kitchen;
        PortParamDataBase database = new PortParamDataBase(activity);
        mPortParam = new PortParameters();

            for (int j = pos; j < list.size()-1;j++) {


                Log.e("PortType", "" + j + "------" + database.queryPortParamDataBase("" + 0).getPortType());
                mPortParam = database.queryPortParamDataBase("" + (j + 1));
                database.deleteDataBase(""+j);
                database.insertPortParam(j, mPortParam);

                Log.e("PortType", "" + (j) + "------" + database.queryPortParamDataBase("" + (1 + j)).getPortType());
                kitchen = CDBHelper.getObjById(activity.getApplicationContext(), list.get(j + 1), KitchenClientC.class);
                if (kitchen != null) {
                    kitchen.setKitchenAdress("" + (j + 1));
                    CDBHelper.createAndUpdate(activity.getApplicationContext(), kitchen);
                }
            }
            database.close();

        //list = CDBHelper.getIdsByClass(activity.getApplicationContext(),KitchenClientC.class);
    }

    private void deleteOneItemFromDatabase(int position) {
        PortParamDataBase database = new PortParamDataBase(activity);
        mPortParam = new PortParameters();
        //删除打印机
        database.deleteDataBase(""+position);
        database.close();
        Document doc = CDBHelper.getDocByID(activity, list.get(position));
        Log.e("MyList",doc.getId());
        //CDBHelper.deleDocument(activity.getApplicationContext(), doc);
        try {
            CDBHelper.getDatabase().delete(doc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

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
        TextView itemKitchenKname;
        //TextView itemKitchenPname;
        ImageView itemKitchenBianji;
        ImageView itemKitchenDelete;

        ViewHolder(View view) {
            itemKitchenKname = view.findViewById(R.id.item_kitchen_kname);
            //itemKitchenPname= view.findViewById(R.id.item_kitchen_pname);
            itemKitchenBianji = view.findViewById(R.id.item_kitchen_bianji);
            itemKitchenDelete = view.findViewById(R.id.item_kitchen_delete);
        }
    }
}
