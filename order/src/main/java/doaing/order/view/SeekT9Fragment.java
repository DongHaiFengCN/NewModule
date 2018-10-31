package doaing.order.view;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.dish.Dish;
import bean.kitchenmanage.dish.DishesKind;
import bean.kitchenmanage.order.Goods;
import doaing.mylibrary.MyApplication;
import doaing.order.R;
import doaing.order.untils.MyBigDecimal;
import doaing.order.view.adapter.MyGridAdapter;
import tools.CDBHelper;
import tools.MyLog;
import tools.ToolUtil;

import static tools.Method.getFormatDate;




/*
*
 * Created by lenovo on 2017/10/26.
*/



public class SeekT9Fragment extends Fragment implements View.OnClickListener{

    ListView activitySeekList;
    EditText activitySeekEdit;
    LinearLayout activitySeekLin26,tlKeyGrid;
    public static String[][] pinyin2sz = new String[][]{{"a", "b", "c", ""}, {"d", "e", "f", ""}, {"g", "h", "i", ""}, {"j", "k", "l", ""}, {"m", "n", "o", ""}, {"p", "q", "r", "s"}, {"t", "u", "v", ""}, {"w", "x", "y", "z"}};
    private float total = 0.0f;
    public int point = 1, tastePos = 0;
    private float tmpAllPrice;
    private List<DishesKind> list;
    private SeekT9Adapter seekT9Adapter;
    private List<MutableDocument> t9GoodsList;

    private List<String> tasteList;

    View view;
    private Handler mHandler = null;
    private MyApplication myapp;
    private boolean isT9And26 = true;
    private boolean isT9And261 = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        MyLog.d( "onCreateView");
        View view = inflater.inflate(R.layout.activity_seek, container, false);
        mHandler = new Handler();
        myapp = (MyApplication) getActivity().getApplication();
        list = myapp.getDishesKindCList();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("T9and26", 0);
        isT9And261 = sharedPreferences.getBoolean("isFlag",true);
        initData(view);
        initView();

        return view;

    }

    @SuppressLint("WrongViewCast")
    private void initData(View view) {
        activitySeekList = view.findViewById(R.id.activity_seek_list);
        activitySeekEdit = view.findViewById(R.id.activity_seek_edit);
        activitySeekLin26 = view.findViewById(R.id.activity_seek_lin_26);
        tlKeyGrid = view.findViewById(R.id.tl_key_grid);
        view.findViewById(R.id.ibtn_key_1).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_2).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_3).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_4).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_5).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_6).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_7).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_8).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_9).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_l).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_0).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_r).setOnClickListener(this);
        view.findViewById(R.id.ibtn_key_del).setOnClickListener(this);
        view.findViewById(R.id.seek_26_q).setOnClickListener(this);
        view.findViewById(R.id.seek_26_w).setOnClickListener(this);
        view.findViewById(R.id.seek_26_e).setOnClickListener(this);
        view.findViewById(R.id.seek_26_r).setOnClickListener(this);
        view.findViewById(R.id.seek_26_t).setOnClickListener(this);
        view.findViewById(R.id.seek_26_y).setOnClickListener(this);
        view.findViewById(R.id.seek_26_u).setOnClickListener(this);
        view.findViewById(R.id.seek_26_i).setOnClickListener(this);
        view.findViewById(R.id.seek_26_o).setOnClickListener(this);
        view.findViewById(R.id.seek_26_p).setOnClickListener(this);
        view.findViewById(R.id.seek_26_a).setOnClickListener(this);
        view.findViewById(R.id.seek_26_s).setOnClickListener(this);
        view.findViewById(R.id.seek_26_d).setOnClickListener(this);
        view.findViewById(R.id.seek_26_f).setOnClickListener(this);
        view.findViewById(R.id.seek_26_g).setOnClickListener(this);
        view.findViewById(R.id.seek_26_h).setOnClickListener(this);
        view.findViewById(R.id.seek_26_j).setOnClickListener(this);
        view.findViewById(R.id.seek_26_k).setOnClickListener(this);
        view.findViewById(R.id.seek_26_l).setOnClickListener(this);
        view.findViewById(R.id.seek_26_z).setOnClickListener(this);
        view.findViewById(R.id.seek_26_x).setOnClickListener(this);
        view.findViewById(R.id.seek_26_c).setOnClickListener(this);
        view.findViewById(R.id.seek_26_v).setOnClickListener(this);
        view.findViewById(R.id.seek_26_b).setOnClickListener(this);
        view.findViewById(R.id.seek_26_n).setOnClickListener(this);
        view.findViewById(R.id.seek_26_m).setOnClickListener(this);
        view.findViewById(R.id.seek_26_sc).setOnClickListener(this);
        view.findViewById(R.id.seek_26_qh).setOnClickListener(this);


    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        MyLog.d( "onHiddenChanged");
        if(!hidden)
        {
            MyLog.d( "hidden="+hidden);
            t9GoodsList.clear();
            seekT9Adapter.notifyDataSetChanged();
            activitySeekEdit.setText("");
        }
    }

    public void initView()
    {
       if(t9GoodsList==null)
           t9GoodsList = new ArrayList<>();
       else
           t9GoodsList.clear();

        seekT9Adapter = new SeekT9Adapter((MainActivity) getActivity(), activitySeekEdit,t9GoodsList);

        seekT9Adapter.setListener(new SeekT9Adapter.SeekT9OnClickListener() {
            @Override
            public void OnClickListener(SeekT9Adapter.ViewHolder view, String name, float price, int pos, boolean isState) {
                if (!isState){
                    view.itemSeekLn.setBackgroundResource(R.color.lucency);
                    showDialog(view,name, price, pos);
                }
            }

        });
        activitySeekList.setAdapter(seekT9Adapter);
        ((MainActivity) getActivity()).setT9GoodsList(t9GoodsList);
        ((MainActivity) getActivity()).setSeekT9Adapter(seekT9Adapter);
        if (isT9And261){
            isT9And26 = false;
            tlKeyGrid.setVisibility(View.GONE);
            activitySeekLin26.setVisibility(View.VISIBLE);
        }else{
            isT9And26 = true;
            tlKeyGrid.setVisibility(View.VISIBLE);
            activitySeekLin26.setVisibility(View.GONE);
        }


        setSeetSearch();

    }


/*
*
     * 菜品选择弹出框编辑模块
     *
     * @param name  传入的菜品的名称
     * @param price 传入的菜品的价格
*/


    private void showDialog(final SeekT9Adapter.ViewHolder viewHolder, final String name, final float price, final int selGoodsPos)
    {
        final String[] taste = {null};
        tastePos = 0;
        if(tasteList==null)
        tasteList = new ArrayList<>();
        else
            tasteList.clear();

        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final Document dishes = CDBHelper.getDocByID( t9GoodsList.get(selGoodsPos).getString("dishId"));
        final AmountView amountView = view.findViewById(R.id.amount_view);
        final  float  sourceCount = t9GoodsList.get(selGoodsPos).getFloat("count");
        if (sourceCount == 0.0)
        {
            amountView.setNumber("1.0");
        } else {
            amountView.setNumber(sourceCount + "");
        }
        String all = MyBigDecimal.mul(amountView.getAmount()+ "", price + "", 2);
        price_tv.setText("总计 " + all + " 元");
        tmpAllPrice = Float.parseFloat(all);
        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls, boolean flag) {

                //实时计算当前菜品选择不同数量后的单品总价
                String all = MyBigDecimal.mul(ls + "", price + "", 2);
                tmpAllPrice = Float.parseFloat(all);

                price_tv.setText("总计 " + tmpAllPrice + " 元");

            }
        });
        if (dishes.getArray("tasteIds") != null)
        {
            for (int i = 0; i < dishes.getArray("tasteIds").count(); i++) {
                tasteList.add( dishes.getArray("tasteIds").getString(i));
            }

        }

        //设置每行展示3个
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        RecyclerView recyclerView = view.findViewById(R.id.view_dialog_recycler);
        recyclerView.setLayoutManager(manager);
        MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(), tasteList);
        myGridAdapter.setmOnItemOlickListener(new MyGridAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {
                taste[0] = tasteList.get(position);
            }
        });
        recyclerView.setAdapter(myGridAdapter);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());


        builder.setTitle(name);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                float destCount = Float.parseFloat(amountView.getEtAmount().getText().toString());
                Log.e("SeekT9",destCount+"----------"+amountView.getAmount());
                if(destCount<=0)
                {
                    Toast.makeText(getActivity(), "没有选择商品数量！", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Document> documentList = ((MainActivity)getActivity()).getGoodsList();
                float mCount = 0;
                for (int g = 0; g < documentList.size(); g++){
                    Document goodsDoc =documentList.get(g);
                    if (goodsDoc.getString("dishId").equals(t9GoodsList.get(selGoodsPos).getString("dishId"))){
                        mCount = mCount + goodsDoc.getFloat("count");
                    }
                }
                t9GoodsList.get(selGoodsPos).setFloat("count",destCount + mCount);
                seekT9Adapter.notifyDataSetChanged();

                MutableDocument goodsDoc = new MutableDocument("MsgGoods."+ToolUtil.getUUID());
                goodsDoc.setString("className","MsgGoods");
                goodsDoc.setString("id",goodsDoc.getId());
                goodsDoc.setString("dishId",dishes.getId());
                goodsDoc.setString("goodsTableMsgId",myapp.getTable_sel_obj().getId());
                goodsDoc.setString("tasteId",taste[0]);
                goodsDoc.setFloat("count",destCount);
                goodsDoc.setInt("status",2);//刚点餐 未生成订单
                goodsDoc.setString("channelId",myapp.getCompany_ID());
                goodsDoc.setInt("submitFlag",0);//打印提交
                goodsDoc.setInt("waitCall",0);//叫启
                goodsDoc.setInt("goodsType",0);
                goodsDoc.setString("createdYear","2018");
                goodsDoc.setString("dataType","BaseData");
                CDBHelper.saveDocument(goodsDoc);
                ((MainActivity) getActivity()).changeOrderGoodsByT9(goodsDoc);
                activitySeekEdit.setText("");
                seekT9Adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void setSeetSearch() {

        activitySeekEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isT9And26){
                    search("code9",activitySeekEdit.getText().toString());
                }else{
                    search("code26",activitySeekEdit.getText().toString());
                }

            }
        });

    }

    // 查询方法
    private void search(final String code, final String search){
        if (search.length() < 2)
            return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                List<Document> documentList = CDBHelper.getDocmentsByWhere(
                        Expression.property("className").equalTo(Expression.string("Dish"))
                                .and(Expression.property(code).like(Expression.string("%"+search + "%")))
                        , Ordering.property("name").ascending());
                t9GoodsList.clear();
                for (Document doc : documentList) {
                    //查找菜单有哪些菜类
                    for (DishesKind dishesKind : list){
                        if (!dishesKind.getId().equals(doc.getString("kindId"))){
                            continue;
                        }else{
                            MutableDocument goodsDoc = new MutableDocument();
                            goodsDoc.setString("dishId",doc.getId());
                            goodsDoc.setFloat("count",0);
                            t9GoodsList.add(goodsDoc);
                        }
                    }
                }
                seekT9Adapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    private  String findZDCKindId()
    {
        List<String> zdcIdList = CDBHelper.getIdsByWhere(
                Expression.property("className").equalTo(Expression.string("DishesKind"))
                 .and(Expression.property("name").equalTo(Expression.string("自点菜")))
                ,null);
        Log.e("SeekT9",""+zdcIdList.size());
        if(zdcIdList.size()>0) {
            return zdcIdList.get(0);
        }else{
            return "";
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.activity_seek_edit) {
        } else if (i == R.id.ibtn_key_1) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "1");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_2) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "2");
            isT9And26 = true;


        } else if (i == R.id.ibtn_key_3) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "3");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_4) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "4");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_5) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "5");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_6) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "6");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_7) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "7");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_8) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "8");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_9) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "9");
            isT9And26 = true;

        } else if (i == R.id.ibtn_key_l) {
//            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//            View view1 = View
//                    .inflate(getActivity(), R.layout.custom_dc_dialog, null);//设置弹窗布局
//            alert.setView(view1);
//            alert.setCancelable(true);
//            final EditText cm = view1.findViewById(R.id.custom_dc_c);//菜名
//            final EditText jg = view1.findViewById(R.id.custom_dc_t);//价格
//            final EditText f_count = view1.findViewById(R.id.custom_dc_count);
//            f_count.clearFocus();
//            f_count.setFocusableInTouchMode(false);
//            f_count.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                        f_count.setFocusableInTouchMode(true);
//                        f_count.requestFocus();
//                        f_count.selectAll();
//                    }
//                    return false;
//                }
//            });
//            //取消或确定按钮监听事件处理
//            final AlertDialog dialog = alert.create();
//            Button btn_cancel = view1
//                    .findViewById(R.id.custom_dc_qx);//取消按钮
//            btn_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//
//                }
//            });
//
//
//            Button btn_comfirm = view1
//                    .findViewById(R.id.custom_dc_qd);//确定按钮
//
//            btn_comfirm.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Goods obj = new Goods();
//                    if (TextUtils.isEmpty(f_count.getText().toString())) {
//                        Toast.makeText(getActivity(), "数量不能为空或者.", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    if (!TextUtils.isEmpty(cm.getText()) && !cm.getText().toString().equals(".")) {
//                        obj.setDishesName(cm.getText().toString());
//                        if (!jg.getText().toString().equals(".") && !TextUtils.isEmpty(jg.getText())) {
//                            float singlePrice = Float.parseFloat(jg.getText().toString());
//                            obj.setPrice(singlePrice);
//                            float counts = Float.parseFloat(f_count.getText().toString());
//                            obj.setDishesCount(counts);
//                            //obj.setDishesId("DishesC."+ UUID.randomUUID());
//                            String zdcDishedKindId = findZDCKindId();
//                            obj.setDishesKindId(zdcDishedKindId);
//                            obj.setGoodsType(3);
//                            obj.setCreatedTime(getFormatDate());
//                            ((MainActivity) getActivity()).getGoodsList().add(obj);
//                            //购物车计数器数据更新
//                            point = (((MainActivity) getActivity()).getPoint());
//                            point++;
//                            ((MainActivity) getActivity()).setPoint(point);
//
//                            //计算总价
//                            total = ((MainActivity) getActivity()).getTotal();
//                            total = MyBigDecimal.add(total, MyBigDecimal.mul(singlePrice, counts, 2), 2);
//                            ((MainActivity) getActivity()).setTotal(total);
//
//
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(getActivity(), "价格不可以为空或者.", Toast.LENGTH_LONG).show();
//                        }
//
//                    } else {
//                        Toast.makeText(getActivity(), "菜名不可以为空或者.", Toast.LENGTH_LONG).show();
//                    }
//
//                }
//            });
//            dialog.show();


        } else if (i == R.id.ibtn_key_0) {
            activitySeekEdit.setText("");
            t9GoodsList.clear();
            seekT9Adapter.notifyDataSetChanged();

        } else if (i == R.id.ibtn_key_r) {
            tlKeyGrid.setVisibility(View.GONE);
            activitySeekLin26.setVisibility(View.VISIBLE);
            activitySeekEdit.setText("");
            t9GoodsList.clear();
            seekT9Adapter.notifyDataSetChanged();
            SharedPreferences settings = getActivity().getSharedPreferences("T9and26", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            editor.putBoolean("isFlag", true);
            editor.commit();

        } else if (i == R.id.ibtn_key_del) {
            int length = activitySeekEdit.getSelectionEnd();
            if (length > 1) {
                activitySeekEdit.getText().delete(length - 1, length);
                search("code9",activitySeekEdit.getText().toString());
            }
            if (length == 1) {
                search("code9","oo");
                activitySeekEdit.getText().delete(length - 1, length);
            }


        } else if (i == R.id.activity_seek_lin_26) {
        } else if (i == R.id.seek_26_q) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "q");
            isT9And26 = false;

        } else if (i == R.id.seek_26_w) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "w");
            isT9And26 = false;

        } else if (i == R.id.seek_26_e) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "e");
            isT9And26 = false;

        } else if (i == R.id.seek_26_r) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "r");
            isT9And26 = false;

        } else if (i == R.id.seek_26_t) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "t");
            isT9And26 = false;

        } else if (i == R.id.seek_26_y) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "y");
            isT9And26 = false;

        } else if (i == R.id.seek_26_u) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "u");
            isT9And26 = false;

        } else if (i == R.id.seek_26_i) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "i");
            isT9And26 = false;

        } else if (i == R.id.seek_26_o) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "o");
            isT9And26 = false;

        } else if (i == R.id.seek_26_p) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "p");
            isT9And26 = false;

        } else if (i == R.id.seek_26_a) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "a");
            isT9And26 = false;

        } else if (i == R.id.seek_26_s) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "s");
            isT9And26 = false;

        } else if (i == R.id.seek_26_d) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "d");
            isT9And26 = false;

        } else if (i == R.id.seek_26_f) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "f");
            isT9And26 = false;

        } else if (i == R.id.seek_26_g) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "g");
            isT9And26 = false;

        } else if (i == R.id.seek_26_h) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "h");
            isT9And26 = false;

        } else if (i == R.id.seek_26_j) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "j");
            isT9And26 = false;

        } else if (i == R.id.seek_26_k) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "k");
            isT9And26 = false;

        } else if (i == R.id.seek_26_l) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "l");
            isT9And26 = false;

        } else if (i == R.id.seek_26_z) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "z");
            isT9And26 = false;

        } else if (i == R.id.seek_26_x) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "x");
            isT9And26 = false;

        } else if (i == R.id.seek_26_c) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "c");
            isT9And26 = false;

        } else if (i == R.id.seek_26_v) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "v");
            isT9And26 = false;

        } else if (i == R.id.seek_26_b) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "b");
            isT9And26 = false;

        } else if (i == R.id.seek_26_n) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "n");
            isT9And26 = false;

        } else if (i == R.id.seek_26_m) {
            activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "m");
            isT9And26 = false;

        } else if (i == R.id.seek_26_qh) {
            tlKeyGrid.setVisibility(View.VISIBLE);
            activitySeekLin26.setVisibility(View.GONE);
            activitySeekEdit.setText("");
            t9GoodsList.clear();
            seekT9Adapter.notifyDataSetChanged();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("T9and26", 0);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.commit();
            edit.putBoolean("isFlag", false);
            edit.commit();

        } else if (i == R.id.seek_26_sc) {
            int length1 = activitySeekEdit.getSelectionEnd();
            if (length1 > 1) {
                activitySeekEdit.getText().delete(length1 - 1, length1);
                search("code26",activitySeekEdit.getText().toString());
            }
            if (length1 == 1) {
                search("code26","oo");
                activitySeekEdit.getText().delete(length1 - 1, length1);
            }

        } else {
        }
    }
}
