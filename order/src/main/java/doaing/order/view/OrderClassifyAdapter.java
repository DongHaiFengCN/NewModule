package doaing.order.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import bean.kitchenmanage.dish.DishesKind;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.module.DishesMessage;
import doaing.order.untils.MyBigDecimal;
import doaing.order.view.adapter.SeekT9DialogAdapter;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;

import static com.mob.commons.eventrecoder.EventRecorder.clear;

public class OrderClassifyAdapter extends BaseAdapter {

    private List<String> dishList = new ArrayList<>();
    private Database db;
    private MainActivity activity;
    private Query query;
    private float[] number;
    private int point = 1;
    private float total;
    private MyApplication myapp;
    private List<String> tasteList;
    private String m_taste;
    OrderClassifyAdapter(MainActivity activity, Database db, String kindId) {
        this.activity = activity;
        this.db = db;
        listDishLive(kindId);
        myapp = activity.getMyApp();

    }

    public void setKindId(String kindId) {
        listDishLive(kindId);
    }

    public float[] getNumber() {
        return number;
    }

    public void setNumber(float[] number) {
        this.number = number;
    }


    private Query listsLiveQuery(String kindId) {
        return QueryBuilder.select(SelectResult.expression(Meta.id)
        )
                .from(DataSource.database(db))
                .where(Expression.property("kindId").equalTo(Expression.string(kindId)));
    }

    private void listDishLive(String kindId) {
        dishList.clear();
        query = listsLiveQuery(kindId);
        query.addChangeListener(new QueryChangeListener() {
            @Override
            public void changed(QueryChange change) {
                clear();
                ResultSet rs = change.getResults();
                Result row;
                while ((row = rs.next()) != null) {
                    MyLog.e("----" + row.getString(0));
                    dishList.add(row.getString(0));
                }
                number = new float[dishList.size()];
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return dishList == null ? 0 : dishList.size();
    }

    @Override
    public Object getItem(int position) {
        return dishList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final HolderView view;
        if (convertView == null) {
            view = new HolderView();
            convertView = LayoutInflater.from(activity).inflate(R.layout.view_item_recl, parent, false);
            view.name = convertView.findViewById(R.id.item_info);
            view.price = convertView.findViewById(R.id.price_tv);
            view.addtion = convertView.findViewById(R.id.addtion_iv);
            view.number = convertView.findViewById(R.id.view_shu);
            view.substruct = convertView.findViewById(R.id.substruct_iv);
            convertView.setTag(view);
        } else {

            view = (HolderView) convertView.getTag();
        }
        final Document dishDoc = CDBHelper.getDocByID(dishList.get(position));
        if (dishDoc.getBoolean("sell")) {
            view.addtion.setVisibility(View.INVISIBLE);
            view.name.setText(dishDoc.getString("name") + "(估清)");
        } else {
            view.addtion.setVisibility(View.VISIBLE);
            view.name.setText(dishDoc.getString("name"));
        }
        // 当数量不为零，且关闭状态，打开减号与数量；当数量为零，处于开启状态则关闭。
        if (number[position] != 0.0f && view.substruct.getVisibility() == View.INVISIBLE
                && view.number.getVisibility() == View.INVISIBLE) {

            view.substruct.setVisibility(View.VISIBLE);

            view.number.setVisibility(View.VISIBLE);

        } else if (number[position] == 0.0f && view.substruct.getVisibility() == View.VISIBLE
                && view.number.getVisibility() == View.VISIBLE) {

            view.substruct.setVisibility(View.INVISIBLE);

            view.number.setVisibility(View.INVISIBLE);

        }
        //设置数量
        view.number.setText(MyBigDecimal.add(number[position], 0, 2) + "");
        view.price.setText("" + dishDoc.getFloat("price"));

        //加法指示器
        view.addtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CDBHelper.getDatabase().inBatch(new TimerTask() {
                        @Override
                        public void run() {
                            if (dishDoc.getBoolean("sell")){
                                return;
                            }
                            m_taste = null;
                            if (tasteList == null) {
                                tasteList = new ArrayList<String>();
                            }else {
                                tasteList.clear();
                            }
                            if (dishDoc.getArray("tasteIds").count() != 0) {
                                for (int i = 0; i < dishDoc.getArray("tasteIds").count(); i++) {
                                    tasteList.add(dishDoc.getArray("tasteIds").getString(i));
                                }
                                if (tasteList.size() == 1){
                                    m_taste = tasteList.get(0);
                                    setAdd(position);
                                }else {
                                    selTasteDialog(tasteList, position,true);
                                }
                                activity.getOrderAdapter().notifyDataSetChanged();

                            } else {
                                setAdd(position);
                                activity.getOrderAdapter().notifyDataSetChanged();
                            }

                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        });

        //减法指示器
        view.substruct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Document> goodsDoc = CDBHelper.getDocmentsByWhere(
                        Expression.property("className").equalTo(Expression.string("MsgGoods"))
                                .and(Expression.property("dishId").equalTo(Expression.string(dishList.get(position))))
                        ,null
                );
                m_taste = null;
                if (tasteList == null) {
                    tasteList = new ArrayList<String>();
                }else {
                    tasteList.clear();
                }
                if (dishDoc.getArray("tasteIds").count() != 0) {
                    for (int i = 0; i < dishDoc.getArray("tasteIds").count(); i++) {
                        tasteList.add(dishDoc.getArray("tasteIds").getString(i));
                    }
                    if (tasteList.size() == 1){
                        m_taste = tasteList.get(0);

                        setSub(goodsDoc.get(0));
                    }else {
                        selTasteDialog(tasteList, position,false);
                    }
                    activity.getOrderAdapter().notifyDataSetChanged();

                } else {
                    setSub(goodsDoc.get(0));
                    activity.getOrderAdapter().notifyDataSetChanged();
                }
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
    private void setAdd(int position) {
        MutableDocument goodsDoc = new MutableDocument("MsgGoods."+ ToolUtil.getUUID());
        goodsDoc.setString("className","MsgGoods");
        goodsDoc.setString("id",goodsDoc.getId());
        goodsDoc.setString("dishId",dishList.get(position));
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
    public void selTasteDialog(final List<String> mData, final int position, final boolean isChoice) {

        m_taste = tasteList.get(0);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
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
        final android.app.AlertDialog dialog = builder.create();
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
                if (isChoice) {
                    setAdd(position);
                }else{
                    List<Document> document = CDBHelper.getDocmentsByWhere(
                            Expression.property("className").equalTo(Expression.string("MsgGoods"))
                            .and(Expression.property("dishId").equalTo(Expression.string(dishList.get(position))))
                            .and(Expression.property("tasteId").equalTo(Expression.string(m_taste)))
                            ,null
                    );
                    if (document.size() == 0){
                        Toast.makeText(activity,"没有此口味的菜品",Toast.LENGTH_LONG).show();
                    }else {
                        setSub(document.get(0));
                    }
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }


//    /**
//     * 初始化需要发送的数据，并发送到 MainActivity
//     *
//     * @param dishesC 获取的菜品实体类
//     * @param flag    true+  false
//     * @return 是否发送成功
//     */
//
//
//    private void setMessage(final Document dishesC, final boolean flag, final int position) {
//        final DishesMessage dishesMessage = new DishesMessage();
//
//        dishesMessage.setDishKindId(dishesC.getString("kindId"));
//
//        dishesMessage.setSingle(true);
//
//        dishesMessage.setDishesC(dishesC);
//
//        dishesMessage.setName(dishesC.getString("name"));
//
//        //判断菜品引用的口味id在数据库中存在实例
//
//        //有口味，添加选择口味dialog
//        if (dishesC.getArray("tasteIds") != null && dishesC.getArray("tasteIds").count() > 0) {
//
//            //初始化一个缓存口味的数组
//            final String[] strings = new String[dishesC.getArray("tasteIds").count()];
//
//            for (int i = 0; i < dishesC.getArray("tasteIds").count(); i++) {
//
//                Document dishesTasteC = CDBHelper.getDocByID(dishesC.getArray("tasteIds").getString(i));
//
//                if (dishesTasteC != null) {
//
//                    strings[i] = dishesTasteC.getString("name");
//
//                }
//
//            }
//
//            //确认数据库口味不为空
//            if (strings[0] != null) {
//                dishesMessage.setDishesTaste(strings[0]);
//                if (dishesC.getArray("tasteIds").count() == 1) {
//                    Refresh(flag, dishesMessage);
//                } else {
//
//                    tasteSelectDialog(flag, position, dishesMessage, strings);
//                }
//
//
//            } else {
//                Refresh(flag, dishesMessage);
//            }
//
//        } else {
//
//            Refresh(flag, dishesMessage);
//        }
//
//
//    }
//
//    private void tasteSelectDialog(final boolean flag, final int position, final DishesMessage dishesMessage, final String[] strings) {
//        new AlertDialog.Builder(activity).setTitle("全部口味")
//
//
//                .setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        //得到口味
//
//                        dishesMessage.setDishesTaste(strings[i]);
//
//                    }
//                })
//                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        Refresh(flag, dishesMessage);
//
//
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                }).show();
//    }
//
//
//    private void Refresh(boolean flag, DishesMessage dishesMessage) {
//
//        dishesMessage.setOperation(flag);
//
//        EventBus.getDefault().postSticky(dishesMessage);
//
//    }


    class HolderView {

        TextView name;
        TextView price;
        TextView number;
        ImageView addtion;
        ImageView substruct;

    }

    public void dishCount(List<Document> goodsDocList) {
        if (number == null) {
            return;
        }
        resetNumber();
        for (Document doc : goodsDocList) {
            for (int i = 0; i < dishList.size(); i++) {
                if (doc.getString("dishId").equals(dishList.get(i))) {
                    number[i] = doc.getFloat("count") + number[i];
                    break;
                }
            }
        }
    }


    private void resetNumber() {
        //初始化数量
        for (int i = 0; i < number.length; i++) {

            number[i] = 0f;
        }

    }

}
