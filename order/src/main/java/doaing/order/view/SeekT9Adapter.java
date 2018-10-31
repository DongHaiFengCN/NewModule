package doaing.order.view;

import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import bean.kitchenmanage.dish.Dish;
import bean.kitchenmanage.order.Goods;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.untils.MyBigDecimal;
import doaing.order.view.adapter.SeekT9DialogAdapter;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;

import static tools.Method.getNewFormatDate;



/*
*
 * Created by lenovo on 2017/10/26.
*/



public class SeekT9Adapter extends BaseAdapter {

    private static final String TAG = "SeekT9Adapter";
    private List<MutableDocument> mGoodsList;
    private MainActivity activity;
    private SeekT9OnClickListener listener;
    //private float number=1;
    private int point = 1;
    private float total;

    private List<String> tasteList;
    private String m_taste;

    private MyApplication myapp;
    private EditText editText;
    private boolean isSell = false;
    private float mCount = 0;
    public SeekT9Adapter(MainActivity context, EditText editText, List<MutableDocument> mData) {
        this.activity = context;
        this.editText = editText;
        myapp = (MyApplication) activity.getApplication();
        this.mGoodsList = mData;
    }

    public List<MutableDocument> getGoodsList(){
        return mGoodsList;
    }

    public void setListener(SeekT9OnClickListener listener) {
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return mGoodsList == null ? 0 : mGoodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        final Document dishes;
        mCount = 0;
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_seek, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        dishes = CDBHelper.getDocByID( mGoodsList.get(position).getString("dishId"));
        if (dishes.getBoolean("sell")){
            viewHolder.viewTj.setVisibility(View.INVISIBLE);
            isSell = true;
        }else{
            viewHolder.viewTj.setVisibility(View.VISIBLE);
            isSell = false;
        }
        try {
            viewHolder.itemSeekInfo.setText(ToolUtil.emojiRecovery2(dishes.getString("name")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        viewHolder.itemSeekTv.setText(dishes.getFloat("price") + "");
//        for (int i = 0; i < activity.getGoodsList().size(); i++){
//            Document goodsDoc = activity.getGoodsList().get(i);
//            if (goodsDoc.getString("dishId").equals(mGoodsList.get(position).getString("dishId"))){
//                mCount = mCount + goodsDoc.getFloat("count");
//            }
//        }
        mCount = mGoodsList.get(position).getFloat("count");
        viewHolder.viewShu.setText("" + mCount);

        viewHolder.itemSeekLn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("item click", "position = " + position);
                v.setBackgroundResource(R.color.lucency);
                if (listener != null) {
                    listener.OnClickListener(viewHolder, dishes.getString("name"),dishes.getFloat("price"), position,isSell);
                }
            }
        });

        final String str = viewHolder.viewShu.getText().toString();
        Log.e("viewShu", str);
        if (str.equals("0.0")) {
            viewHolder.viewShu.setVisibility(View.INVISIBLE);
            viewHolder.viewJian.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.viewShu.setVisibility(View.VISIBLE);
            viewHolder.viewJian.setVisibility(View.VISIBLE);
        }


        viewHolder.viewTj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CDBHelper.getDatabase().inBatch(new TimerTask() {
                        @Override
                        public void run() {
                            if (dishes.getBoolean("sell")){
                                return;
                            }
                            m_taste = null;
                            if (tasteList == null)
                                tasteList = new ArrayList<String>();
                            else
                                tasteList.clear();
                            if (dishes.getArray("tasteIds").count() != 0) {
                                for (int i = 0; i < dishes.getArray("tasteIds").count(); i++) {
                                    tasteList.add(dishes.getArray("tasteIds").getString(i));
                                }
                                if (tasteList.size() == 1){
                                    m_taste = tasteList.get(0);
                                    setAdd(position, viewHolder);
                                }else {
                                    selTasteDialog(tasteList, position, viewHolder);
                                }
                                activity.getOrderAdapter().notifyDataSetChanged();

                            } else {
                                setAdd(position, viewHolder);
                                activity.getOrderAdapter().notifyDataSetChanged();
                            }

                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.viewJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGoodsList.get(position).getFloat("count") > 0) {
                    mGoodsList.get(position).setFloat("count",mGoodsList.get(position).getFloat("count") - 1);
                }

                if (mGoodsList.get(position).getFloat("count") <= 0) {
                    viewHolder.viewShu.setVisibility(View.INVISIBLE);
                    viewHolder.viewJian.setVisibility(View.INVISIBLE);
                }
                viewHolder.viewShu.setText(mGoodsList.get(position).getFloat("count") + "");

                Document obj = mGoodsList.get(position);
                setSub(obj);

            }

        });


        return convertView;
    }

    private void setSub(Document goodsObj)
    {
        if (activity.getGoodsList().size() != 0)
        {
            for (int i = 0; i < activity.getGoodsList().size(); i++)
            {
                Document doc = activity.getGoodsList().get(i);
                MutableDocument mutableDoc =  doc.toMutable();
                if (doc.getInt("goodsType") == 3){
                    continue;
                }
                if (doc.getString("dishId").equals(goodsObj.getString("dishId")))//名称相同
                {
                    if(TextUtils.isEmpty(goodsObj.getString("tasteId")))
                    {
                        float tmpCount = MyBigDecimal.sub(doc.getFloat("count"),1,1);
                        if (tmpCount== 0.0)
                        {
                            CDBHelper.deleDocument(doc);
                            point = activity.getPoint();
                            point--;
                            activity.setPoint(point);
                        }else{
                            mutableDoc.setFloat("count",tmpCount);
                            CDBHelper.saveDocument(mutableDoc);
                        }
                        total = activity.getTotal();
                        Document dishDoc = CDBHelper.getDocByID(goodsObj.getString("dishId"));
                        total = MyBigDecimal.sub(total, MyBigDecimal.mul(dishDoc.getFloat("price"),1,1), 2);
                        activity.setTotal(total);
                        activity.Listener();
                        break;
                    }
                    else
                    {
                        if(goodsObj.getString("tasteId").equals(doc.getString("tasteId")))
                        {
                            float tmpCount = MyBigDecimal.sub(doc.getFloat("count"),1,1);
                            if (tmpCount== 0.0)
                            {
                                CDBHelper.deleDocument(doc);
                                point = activity.getPoint();
                                point--;
                                activity.setPoint(point);
                            }else{
                                mutableDoc.setFloat("count",tmpCount);
                                CDBHelper.saveDocument(mutableDoc);
                            }
                            total = activity.getTotal();
                            Document dishDoc = CDBHelper.getDocByID(goodsObj.getString("dishId"));
                            total = MyBigDecimal.sub(total, MyBigDecimal.mul(dishDoc.getFloat("price"),1,1), 2);
                            activity.setTotal(total);
                            CDBHelper.saveDocument(mutableDoc);
                            break;
                        }
                    }

                }
            }
        }
        activity.getOrderAdapter().notifyDataSetChanged();
    }
    private void setAdd(int position, ViewHolder viewHolder) {
        boolean isInsert = true;
        editText.setText("");
        mCount = mGoodsList.get(position).getFloat("count") + 1 ;
        mGoodsList.get(position).setFloat("count",mCount);
        viewHolder.viewShu.setText(mCount+ "");

        MutableDocument goodsDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());
        goodsDoc.setString("className","MsgGoods");
        goodsDoc.setString("id",goodsDoc.getId());
        goodsDoc.setString("dishId",mGoodsList.get(position).getString("dishId"));
        goodsDoc.setString("goodsTableMsgId",myapp.getTable_sel_obj().getId());
        goodsDoc.setString("tasteId",m_taste);
        goodsDoc.setFloat("count",1);
        goodsDoc.setInt("status",2);//刚点餐 未生成订单
        goodsDoc.setString("channelId",myapp.getCompany_ID());
        goodsDoc.setInt("submitFlag",0);//打印提交
        goodsDoc.setInt("waitCall",0);//叫启
        goodsDoc.setInt("goodsType",0);
        goodsDoc.setString("createdYear","2018");
        goodsDoc.setString("dataType","BaseData");
        CDBHelper.saveDocument(goodsDoc);
        if (!viewHolder.viewShu.getText().toString().equals("0")) {
            viewHolder.viewShu.setVisibility(View.VISIBLE);
            viewHolder.viewJian.setVisibility(View.VISIBLE);
        }
        //当前的选择的菜品加入订单列表
        activity.getGoodsList().add(goodsDoc);
        //购物车计数器数据更新
        point = activity.getPoint();
        point++;
        activity.setPoint(point);
        //计算总价
        total = activity.getTotal();
        Document dishDoc = CDBHelper.getDocByID(goodsDoc.getString("dishId"));
        total = MyBigDecimal.add(total, MyBigDecimal.mul(dishDoc.getFloat("price"),goodsDoc.getFloat("count"),1), 2);
        activity.setTotal(total);
    }

    //自定义弹窗
    public void selTasteDialog(final List<String> mData, final int position, final ViewHolder viewHolder) {

        m_taste = tasteList.get(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = View
                .inflate(activity, R.layout.view_t9_dialog, null);//设置弹窗布局
        builder.setView(view);
        builder.setCancelable(true);
        RecyclerView rlv_caipin = view.findViewById(R.id.rlv_caipin);
        rlv_caipin.setLayoutManager(new LinearLayoutManager(activity));
        SeekT9DialogAdapter seekT9DialogAdapter = new SeekT9DialogAdapter(activity, mData);

        seekT9DialogAdapter.setmOnItemOlickListener(new SeekT9DialogAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {

                m_taste = tasteList.get(position);
            }
        });
        rlv_caipin.setAdapter(seekT9DialogAdapter);
        //取消或确定按钮监听事件处理
        final AlertDialog dialog = builder.create();
        Button btn_cancel = view
                .findViewById(R.id.view_caipin_but);//取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        Button btn_comfirm = view
                .findViewById(R.id.view_caipin_but_ok);//确定按钮

        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                setAdd(position, viewHolder);
            }
        });
        dialog.show();
    }


    interface SeekT9OnClickListener {
        void OnClickListener(ViewHolder view, String name, float price, int pos,boolean isState);
    }

    public class ViewHolder {
        TextView itemSeekInfo;
        TextView itemSeekTv;
        LinearLayout itemSeekLn;
        RelativeLayout viewJian;
        TextView viewShu;
        RelativeLayout viewTj;

        ViewHolder(View view) {
            itemSeekInfo = view.findViewById(R.id.item_seek_info);
            itemSeekTv = view.findViewById(R.id.item_seek_tv);
            itemSeekLn = view.findViewById(R.id.item_seek_ln);
            viewJian = view.findViewById(R.id.view_jian);
            viewShu = view.findViewById(R.id.view_shu);
            viewTj = view.findViewById(R.id.view_tj);
        }
    }
}
